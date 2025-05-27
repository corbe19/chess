package client;

import model.AuthData;
import ui.EscapeSequences;

import java.io.IOException;

//register, login, help
public class PreLoginClient {
    private final ServerFacade server;

    public PreLoginClient(ServerFacade server) {
        this.server = server;
    }

    public AuthData register(String[] tokens) throws Exception {
        if (tokens.length != 4) {
            throw new IllegalArgumentException("Usage: register <USERNAME> <PASSWORD <EMAIL>");
        }

        try {
            AuthData auth =  server.register(tokens[1], tokens[2], tokens[3]);
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "You are logged in as " + EscapeSequences.SET_TEXT_BOLD + auth.username() +
                    EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_TEXT_BOLD_FAINT);
            return auth;
        } catch (IOException e) {
            if (e.getMessage().contains("already taken")) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Username already taken."
                        + EscapeSequences.RESET_TEXT_COLOR);
                return null;
            } else {
                throw e;
            }
        }
    }

    public AuthData login(String[] tokens) throws Exception {
        if (tokens.length != 3) {
            throw new IllegalArgumentException("Usage: login <USERNAME> <PASSWORD>");
        }

        try {
            AuthData auth =  server.login(tokens[1], tokens[2]);
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "You are logged in as " + EscapeSequences.SET_TEXT_BOLD + auth.username() +
                    EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_TEXT_BOLD_FAINT);
            return auth;
        } catch (IOException e) {
            String msg = e.getMessage().toLowerCase();

            if (msg.contains("unauthorized") || msg.contains("invalid") || msg.contains("not found")) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                        "Invalid username or password." +
                        EscapeSequences.RESET_TEXT_COLOR);
                return null;
            } else {
                throw e;
            }
        }
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
