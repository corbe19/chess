package client;

import model.AuthData;
import ui.EscapeSequences;

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
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + " register <USERNAME> <PASSWORD> <EMAIL> "
                + EscapeSequences.RESET_TEXT_COLOR + " - to create an account");

        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + " login <USERNAME> <PASSWORD>"
                + EscapeSequences.RESET_TEXT_COLOR + " - to play chess");

        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE +  " quit"
                        + EscapeSequences.RESET_TEXT_COLOR + " - playing chess");

        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + " help"
                + EscapeSequences.RESET_TEXT_COLOR + " - with possible commands");
    }
}
