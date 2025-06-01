package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class GameWebSocketHandler {
    private final AuthDAO authDAO = new AuthDAO();
    private final GameDAO gameDAO = new GameDAO();

    private static final Gson gson = new Gson();


    private static final Map<Integer, Map<String, Session>> sessionsByGame = new ConcurrentHashMap<>();
    private final Map<Integer, String> resignedGames = new ConcurrentHashMap<>();



    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Client connected: " + session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        //loop through games and remove this one
        for (var entry : sessionsByGame.entrySet()) {
            entry.getValue().values().removeIf(s -> s.equals(session));
        }
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand base = gson.fromJson(message, UserGameCommand.class);
            switch (base.getCommandType()) {
                case CONNECT -> handleConnect(gson.fromJson(message, ConnectCommand.class), session);
                case MAKE_MOVE -> handleMove(gson.fromJson(message, MakeMoveCommand.class), session);
                case LEAVE -> handleLeave(gson.fromJson(message, LeaveCommand.class), session);
                case RESIGN -> handleResign(gson.fromJson(message, ResignCommand.class), session);
                default -> sendError(session, "Error: Unknown command");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(session, "Error: Invalid command or message.");
        }
    }

    //<========================================================== Handle Connect ==========================================================>
    private void handleConnect(ConnectCommand command, Session session) {
        HandlerContext ctx = initHandler(command, session);
        if (ctx == null) {
            return;
        }

        //store session for later
        sessionsByGame.computeIfAbsent(ctx.gameID, k -> new ConcurrentHashMap<>()).put(ctx.authToken, session);

        send(session, new LoadGameMessage(ctx.game.game()));

        //sort out players for notifications
        String role;
        if (ctx.username.equals(ctx.game.whiteUsername())) {
            role = "White";
        } else if (ctx.username.equals(ctx.game.blackUsername())) {
            role = "Black";
        } else {
            role = "Observer";
        }

        //actual notification
        String notification = ctx.username + " joined the game as " + role + ".";
        broadcastAll(ctx.gameID, new NotificationMessage(notification));
    }

    //<========================================================== Handle Move ==========================================================>
    private void handleMove(MakeMoveCommand command, Session session) {
        HandlerContext ctx = initHandler(command, session);
        if (ctx == null) {
            return;
        }

        ChessMove move = command.getMove();
        ChessGame chessGame = ctx.game.game();


        String resignedUser = resignedGames.get(ctx.gameID);
        if (resignedUser != null) {
            sendError(session, "Error: The game is already over. " + resignedUser + " resigned.");
            return;
        }

        //check turn
        ChessGame.TeamColor playerColor = null;
        if (ctx.username.equals(ctx.game.whiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (ctx.username.equals(ctx.game.blackUsername())) {
            playerColor = ChessGame.TeamColor.BLACK;
        } else {
            sendError(session, "Error: you are not a player in this game :(");
            return;
        }

        if (chessGame.getTeamTurn() != playerColor) {
            sendError(session, "Error: It is not your turn. Patience please.");
            return;
        }

        //valid move?

        //make move
        try {
            chessGame.makeMove(move);
        } catch (InvalidMoveException e) {
            sendError(session, "Error: Invalid move — " + e.getMessage());
            return;
        }

        //save move
        GameData updatedGame = new GameData(
                ctx.game.gameID(),
                ctx.game.whiteUsername(),
                ctx.game.blackUsername(),
                ctx.game.gameName(),
                chessGame
        );

        try {
            gameDAO.updateGame(updatedGame);
        } catch (DataAccessException e) {
            sendError(session, "Error: Could not update game state.");
            return;
        }

        //broadcast move
        broadcastAll(ctx.gameID, new LoadGameMessage(chessGame));

        //notify lobby about move
        String moveDescription = ctx.username + " moved from " +
                move.getStartPosition() + " to " + move.getEndPosition();
        broadcastExcept(ctx.gameID, new NotificationMessage(moveDescription), ctx.authToken);

        //Check for mate?
        ChessGame.TeamColor opponent = (playerColor == ChessGame.TeamColor.WHITE)
                ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        if (chessGame.isInCheckmate(opponent)) {
            broadcastAll(ctx.gameID, new NotificationMessage("Checkmate! " + ctx.username + " wins."));
        } else if (chessGame.isInCheck(opponent)) {
            broadcastAll(ctx.gameID, new NotificationMessage("Check against " + opponent.name()));
        } else if (chessGame.isInStalemate(opponent)) {
            broadcastAll(ctx.gameID, new NotificationMessage("Stalemate! The game is a draw."));
        }

        //holy moly
    }

    //<========================================================== Handle Resign ==========================================================>
    private void handleResign(ResignCommand command, Session session) {
        HandlerContext ctx = initHandler(command, session);
        if (ctx == null) {
            return;
        }
        //check for resign
        if (resignedGames.containsKey(ctx.gameID)) {
            sendError(session, "Error: Game already ended due to resignation.");
            return;
        }

        if (!ctx.username.equals(ctx.game.whiteUsername()) && !ctx.username.equals(ctx.game.blackUsername())) {
            sendError(session, "Error: You are not a player in this game");
            return;
        }

        resignedGames.put(ctx.gameID, ctx.username);

        broadcastAll(ctx.gameID, new NotificationMessage(ctx.username + " has resigned."));

        try {
            gameDAO.updateGame(ctx.game);
        } catch (DataAccessException e) {
            sendError(session, "Error: Failed to store resign.");
        }

    }

    //<========================================================== Handle Leave ==========================================================>
    private void handleLeave(LeaveCommand command, Session session) {
        HandlerContext ctx = initHandler(command, session);
        if (ctx == null) {
            return;
        }

        if (sessionsByGame.containsKey(ctx.gameID)) {
            sessionsByGame.get(ctx.gameID).remove(ctx.authToken);
            if (sessionsByGame.get(ctx.gameID).isEmpty()) {
                sessionsByGame.remove(ctx.gameID);
            }
        }

        //make sure color becomes null
        GameData game = ctx.game;
        boolean updated = false;
        String white = game.whiteUsername();
        String black = game.blackUsername();

        if (ctx.username.equals(white)) {
            white = null;
            updated = true;
        } else if (ctx.username.equals(black)) {
            black = null;
            updated = true;
        }

        if (updated) {
            GameData updatedGame = new GameData(
                    game.gameID(),
                    white,
                    black,
                    game.gameName(),
                    game.game()
            );
            try {
                gameDAO.updateGame(updatedGame);
            } catch (DataAccessException e) {
                sendError(session, "Error: Could not update game after leaving.");
                return;
            }
        }

        broadcastExcept(ctx.gameID, new NotificationMessage(ctx.username + " has left the game."), ctx.authToken);
    }


    //I dont want to send duplicate notifications to players
    private void broadcastExcept(int gameID, ServerMessage message, String excludeAuthToken) {
        Map<String, Session> clients = sessionsByGame.getOrDefault(gameID, Map.of());
        String json = gson.toJson(message);
        for (Map.Entry<String, Session> entry : clients.entrySet()) {
            if (!entry.getKey().equals(excludeAuthToken)) {
                try {
                    entry.getValue().getRemote().sendString(json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void broadcastAll(int gameID, ServerMessage message) {
        Map<String, Session> clients = sessionsByGame.getOrDefault(gameID, Map.of());
        String json = gson.toJson(message);

        for (Session session : clients.values()) {
            try {
                session.getRemote().sendString(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void send(Session session, ServerMessage message) {
        try {
            String json = gson.toJson(message);
            session.getRemote().sendString(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendError(Session session, String msg) {
        if (session != null) {
            try {
                session.getRemote().sendString(gson.toJson(new ErrorMessage(msg)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Warning: Tried to send error to null session: " + msg);
        }
    }

    private static class HandlerContext {
        final String authToken;
        final String username;
        final int gameID;
        final GameData game;
        final AuthData auth;

        HandlerContext(String authToken, String username, int gameID, GameData game, AuthData auth) {
            this.authToken = authToken;
            this.username = username;
            this.gameID = gameID;
            this.game = game;
            this.auth = auth;
        }
    }

    private HandlerContext initHandler(UserGameCommand command, Session session) {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        AuthData auth;
        try {
            auth = authDAO.getAuth(authToken);
            if (auth == null) {
                sendError(session, "Error: Invalid auth token");
                return null;
            }
        } catch (DataAccessException e) {
            sendError(session, "Error: Could not access auth data");
            return null;
        }

        GameData game;
        try {
            game = gameDAO.getGame(gameID);
            if (game == null) {
                sendError(session, "Error: Invalid game ID");
                return null;
            }
        } catch (DataAccessException e) {
            sendError(session, "Error: Could not access game data");
            return null;
        }

        return new HandlerContext(authToken, auth.username(), gameID, game, auth);
    }
}
