package client;

import model.AuthData;
import model.GameData;
import model.ListGamesResult;

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
        System.out.println(" create <NAME> - a game");
        System.out.println(" list - games");
        System.out.println(" join <ID> [WHITE|BLACK] - a game");
        System.out.println(" observe <ID> - a game");
        System.out.println(" logout - when you are done");
        System.out.println(" quit - playing chess");
        System.out.println(" help - with possible commands");
    }

    public void create(String[] tokens, AuthData auth) throws Exception {
        if (tokens.length != 2) {
            throw new IllegalArgumentException("Usage: create <NAME>");
        }
        server.createGame(auth, tokens[1]);
        System.out.println("Game created!");
    }

    public void list(AuthData auth) throws Exception {
        ListGamesResult result = server.listGames(auth);
        lastGameList = new ArrayList<>(result.games());

        int i = 1;
        for (GameData game : lastGameList) {
            System.out.printf("%d. %s (White: %s, Black: %s)%n" , //should format: "1. Game A (White: billy, Black: bob)"
                    i++, game.gameName(),
                    game.whiteUsername() != null ? game.whiteUsername() : "[None]",
                    game.blackUsername() != null ? game.blackUsername() : "[None]");
        }
    }

    public void join(String[] tokens, AuthData auth) throws Exception {
        if (tokens.length != 3) {
            throw new IllegalArgumentException("Usage: join <NUMBER> [WHITE|BLACK]");
        }

        int index = Integer.parseInt(tokens[1]);
        if (index < 1 || index > lastGameList.size()) {
            throw new IllegalArgumentException("Invalid game number. Try 'list' to view games");
        }

        int gameID = lastGameList.get(index - 1).gameID();
        String color = tokens[2].toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            throw new IllegalArgumentException("Color must be WHITE or BLACK");
        }

        server.joinGame(auth, gameID, color);
        System.out.println("Joined game " + " as " + color);
    }



}
