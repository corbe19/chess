package client;

import chess.ChessBoard;
import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.ListGamesResult;
import ui.EscapeSequences;

import java.io.IOException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;

public class PostLoginClient {
    private final ServerFacade server;
    private List<GameData> lastGameList = new ArrayList<>();

    public PostLoginClient(ServerFacade server) {
        this.server = server;
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
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "Would you like to join this game?"
                + EscapeSequences.SET_TEXT_COLOR_YELLOW + "\nType: join <number> WHITE/BLACK"
                + EscapeSequences.RESET_TEXT_COLOR);
    }

    public void list(AuthData auth) throws Exception {
        ListGamesResult result = server.listGames(auth);
        lastGameList = new ArrayList<>(result.games());

        if (lastGameList.isEmpty()) {
            System.out.println("No games in progress. Type 'create' to start a new game!");
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
        String color = tokens[2].toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            throw new IllegalArgumentException(EscapeSequences.SET_TEXT_COLOR_RED + "Color must be " +
                    EscapeSequences.SET_TEXT_BOLD + "'WHITE'" +
                    EscapeSequences.RESET_TEXT_BOLD_FAINT + " or " +
                    EscapeSequences.SET_TEXT_BOLD + "'BLACK'" +
                    EscapeSequences.RESET_TEXT_BOLD_FAINT);
        }
        try {
            server.joinGame(auth, gameID, color);
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Joined game " + gameID + " as " +
                    EscapeSequences.SET_TEXT_BOLD + color + EscapeSequences.RESET_TEXT_BOLD_FAINT + EscapeSequences.RESET_TEXT_COLOR);

            //draw board on join
            ChessBoard board = new ChessGame().getBoard(); //REPLACE WITH REAL GAME STATE LATER
            ChessGame.TeamColor perspective = color.equals("WHITE")
                    ? ChessGame.TeamColor.WHITE
                    : ChessGame.TeamColor.BLACK;
            ui.BoardPrinter.draw(board, perspective);


        } catch (IOException e) {
            if (e.getMessage().contains("already taken")) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Color is already taken.");
            } else {
                throw e;
            }
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

            //draw board from white perspective
            ChessBoard board = new ChessGame().getBoard(); //REPLACE WITH REAL GAME STATE LATER
            ui.BoardPrinter.draw(board, ChessGame.TeamColor.WHITE);

        } catch (IOException e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Could not observe game: " + e.getMessage());
        }
    }

    public void logout(AuthData auth) throws Exception {
        server.logout(auth);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Logged out." + EscapeSequences.RESET_TEXT_COLOR);
    }
}
