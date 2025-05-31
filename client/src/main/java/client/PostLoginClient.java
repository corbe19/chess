package client;

import chess.ChessBoard;
import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.ListGamesResult;
import repl.GameREPL;
import ui.EscapeSequences;

import java.io.IOException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;

public class PostLoginClient {
    private final ServerFacade server;
    private List<GameData> lastGameList = new ArrayList<>();
    private final int websocketPort;

    public PostLoginClient(ServerFacade server, int websocketPort) {
        this.server = server;
        this.websocketPort = websocketPort;
    }

    public void help() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + " create <NAME>"
                + EscapeSequences.RESET_TEXT_COLOR + " - a game");

        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE +  " list"
                + EscapeSequences.RESET_TEXT_COLOR + " - games");

        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE +  " join <ID> [WHITE|BLACK]"
                        + EscapeSequences.RESET_TEXT_COLOR + " - a game");

        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE +  " observe <ID>"
                        + EscapeSequences.RESET_TEXT_COLOR + " - a game");

        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE +  " logout"
                        + EscapeSequences.RESET_TEXT_COLOR + " - when you are done");

        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE +  " quit"
                        + EscapeSequences.RESET_TEXT_COLOR + " - playing chess");

        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE +  " help"
                + EscapeSequences.RESET_TEXT_COLOR + " - with possible commands");
    }

    public void create(String[] tokens, AuthData auth) throws Exception {
        if (tokens.length != 2) {
            throw new IllegalArgumentException("Usage: create <NAME>");
        }
        server.createGame(auth, tokens[1]);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Game created!");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE
                + "Would you like to join this game? Type: join <number> WHITE/BLACK"
                + EscapeSequences.RESET_TEXT_COLOR);
    }

    public void list(AuthData auth) throws Exception {
        ListGamesResult result = server.listGames(auth);
        lastGameList = new ArrayList<>(result.games());

        if (lastGameList.isEmpty()) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW + "No games in progress. Type 'create' to start a new game!"
                    + EscapeSequences.RESET_TEXT_COLOR);
        }

        int i = 1;
        for (GameData game : lastGameList) {
            String white = game.whiteUsername() != null
                    ? EscapeSequences.SET_TEXT_BOLD + game.whiteUsername() + EscapeSequences.RESET_TEXT_BOLD_FAINT
                    : EscapeSequences.SET_TEXT_COLOR_YELLOW + "[Open]" + EscapeSequences.RESET_TEXT_COLOR;

            String black = game.blackUsername() != null
                    ? EscapeSequences.SET_TEXT_BOLD + game.blackUsername() + EscapeSequences.RESET_TEXT_BOLD_FAINT
                    : EscapeSequences.SET_TEXT_COLOR_YELLOW + "[Open]" + EscapeSequences.RESET_TEXT_COLOR;

            System.out.printf("%s%d.%s %s (White: %s, Black: %s)%n",
                    EscapeSequences.SET_TEXT_COLOR_BLUE, i++,
                    EscapeSequences.RESET_TEXT_COLOR, game.gameName(),
                    white,
                    black);
        }
    }

    public void join(String[] tokens, AuthData auth) throws Exception {
        if (tokens.length != 3) {
            throw new IllegalArgumentException("Usage: join <NUMBER> [WHITE|BLACK]");
        }

        if (!tokens[1].matches("\\d+")) {
            throw new IllegalArgumentException("Game number must be a valid integer."
                    + "\n       Usage: join <NUMBER> [WHITE|BLACK]");
        }

        int index = Integer.parseInt(tokens[1]);
        if (index < 1 || index > lastGameList.size()) {
            throw new IllegalArgumentException("Invalid game number. Try 'list' to view games");
        }

        int gameID = lastGameList.get(index - 1).gameID();
        String colorStr = tokens[2].toUpperCase(); //I hate java

        if (!colorStr.equals("WHITE") && !colorStr.equals("BLACK")) {
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "Color must be " +
                    EscapeSequences.SET_TEXT_BOLD + "'WHITE'" +
                    EscapeSequences.RESET_TEXT_BOLD_FAINT + " or " +
                    EscapeSequences.SET_TEXT_BOLD + "'BLACK'" +
                    EscapeSequences.RESET_TEXT_BOLD_FAINT);
        }

        ChessGame.TeamColor color = switch (colorStr) {
            case "WHITE" -> ChessGame.TeamColor.WHITE;
            case "BLACK" -> ChessGame.TeamColor.BLACK;
            default -> throw new IllegalArgumentException("Color must be WHITE or BLACK.");
        };


        try {
            server.joinGame(auth, gameID, colorStr);

            MessageHandlerImpl handler = new MessageHandlerImpl();
            GameClient gameClient = new GameClient(auth.authToken(), gameID, handler);
            gameClient.connect(websocketPort);
            gameClient.joinGame(color);

            //wait for da real game data
            ChessGame game = handler.waitForGame();

            //start GameREPL
            GameREPL repl = new GameREPL(gameClient, game, color);
            repl.run();

        } catch (Exception e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Join failed: " + e.getMessage());
        }
    }

    public void observe(String[] tokens, AuthData auth) throws Exception {
        if (tokens.length != 2) {
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_YELLOW + "Usage: observe <NUMBER>");
        }

        if (!tokens[1].matches("\\d+")) {
            throw new IllegalArgumentException("Game number must be a valid integer."
                    + "\n       Usage: observe <NUMBER>");
        }

        int index = Integer.parseInt(tokens[1]);
        if (index < 1 || index > lastGameList.size()) {
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "Invalid game number. Try 'list' to view games");
        }

        int gameID = lastGameList.get(index - 1).gameID();

        try {
            server.joinGame(auth, gameID, null); // no color = observer

            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Observing game: " +
                    EscapeSequences.SET_TEXT_BOLD + gameID + EscapeSequences.RESET_TEXT_BOLD_FAINT
                    + EscapeSequences.RESET_TEXT_COLOR);

            MessageHandlerImpl handler = new MessageHandlerImpl();
            GameClient gameClient = new GameClient(auth.authToken(), gameID, handler);
            gameClient.connect(websocketPort); //use ws port
            gameClient.joinGame(null); // observer

            ChessGame game = handler.waitForGame();

            //view from white perspective
            GameREPL repl = new GameREPL(gameClient, game, ChessGame.TeamColor.WHITE);
            repl.run();

        } catch (Exception e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Observe failed: " + e.getMessage());
        }
    }

    public void logout(AuthData auth) throws Exception {
        server.logout(auth);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Logged out." + EscapeSequences.RESET_TEXT_COLOR);
    }
}
