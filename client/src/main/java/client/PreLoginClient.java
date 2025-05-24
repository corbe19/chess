package client;

import model.AuthData;

//register, login, help
public class PreLoginClient {
    private final ServerFacade server;

    public PreLoginClient(ServerFacade server) {
        this.server = server;
    }

    public AuthData register(String[] tokens) throws Exception {
        if (tokens.length != 4) {
            throw new IllegalArgumentException("Usage: register <USERNAME> <PASSWORD <EMAIL>>");
        }
        return server.register(tokens[1], tokens[2], tokens[3]);
    }

    public AuthData login(String[] tokens) throws Exception {
        if (tokens.length != 3) {
            throw new IllegalArgumentException("Usage: login <USERNAME> <PASSWORD>");
        }
        return server.login(tokens[1], tokens[2]);
    }

    public void help() {
        System.out.println(" register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        System.out.println(" login <USERNAME> <PASSWORD> - to play chess");
        System.out.println(" quit - playing chess");
        System.out.println(" help - with possible commands");
    }
}
