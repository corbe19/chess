package client;

import model.AuthData;
import model.GameData;

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

}
