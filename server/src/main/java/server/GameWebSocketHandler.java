package server;

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


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class GameWebSocketHandler {
    private final AuthDAO authDAO = new AuthDAO();
    private final GameDAO gameDAO = new GameDAO();

    private static final Gson gson = new Gson();


    private static final Map<Integer, Map<String, Session>> sessionsByGame = new ConcurrentHashMap<>();


    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Client connected: " + session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String message) {
        System.out.println("Connection closed: " + message);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand base = gson.fromJson(message, UserGameCommand.class);
            switch (base.getCommandType()) {
                case CONNECT -> handleConnect(gson.fromJson(message, ConnectCommand.class), session);
                //case MAKE_MOVE -> handleMove(gson.fromJson(message, MakeMoveCommand.class));
                //case LEAVE -> handleLeave(gson.fromJson(message, LeaveCommand.class));
                // case RESIGN -> handleResign(gson.fromJson(message, ResignCommand.class));
                default -> sendError(session, "Error: Unknown command");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(session, "Error: Invalid command or message.");
        }
    }

    private void handleConnect(ConnectCommand command, Session session) {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        AuthData auth;

        try {
            auth = authDAO.getAuth(authToken);
            if (auth == null) {
                sendError(session, "Error: Invalid auth token");
                return;
            }
        } catch (DataAccessException e) {
            sendError(session, "Error: Could not access auth data");
            return;
        }

        String username = auth.username();

        //game exist?
        GameData game;
        try {
            game = gameDAO.getGame(gameID);
            if (game == null) {
                sendError(session, "Error: Invalid game ID");
                return;
            }
        } catch (DataAccessException e) {
            sendError(session, "Error: Could not access game data");
            return;
        }

        //store session for later
        sessionsByGame.computeIfAbsent(gameID, k -> new ConcurrentHashMap<>()).put(authToken, session);

        send(session, new LoadGameMessage(game.game()));

        //sort out players for notifications
        String role;
        if (username.equals(game.whiteUsername())) {
            role = "White";
        } else if (username.equals(game.blackUsername())) {
            role = "Black";
        } else {
            role = "Observer";
        }

        //actual notification
        String notification = username + " joined the game as " + role + ".";
        broadcastExcept(gameID, new NotificationMessage(notification), authToken);

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



    private void send(Session session, ServerMessage message) {
        try {
            String json = gson.toJson(message);
            session.getRemote().sendString(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendError(Session session, String msg) {
        send(session, new ErrorMessage(msg));
    }
}
