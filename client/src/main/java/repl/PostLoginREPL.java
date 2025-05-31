package repl;

import client.PostLoginClient;
import model.AuthData;
import ui.EscapeSequences;

import java.util.Scanner;

public class PostLoginREPL {
    private final Scanner scanner;
    private final PostLoginClient client;
    private final int websocketPort;

    public PostLoginREPL(Scanner scanner, client.ServerFacade server, int websocketPort) {
        this.scanner = scanner;
        this.websocketPort = websocketPort;
        this.client = new PostLoginClient(server, websocketPort);

    }

    public AuthData run(AuthData auth) {
        while (true) {
            System.out.print(EscapeSequences.ERASE_SCREEN);
            System.out.flush();
            System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED_IN] >>> " + EscapeSequences.RESET_TEXT_COLOR);
            String[] tokens = scanner.nextLine().trim().split("\\s+");

            try {
                return switch (tokens[0].toLowerCase()) {
                    case "help" -> {
                        client.help();
                        yield auth;
                    }
                    case "create" -> {
                        client.create(tokens, auth);
                        yield auth;
                    }
                    case "list" -> {
                        client.list(auth);
                        yield auth;
                    }
                    case "join" -> {
                        client.join(tokens, auth);
                        yield auth;
                    }
                    case "observe" -> {
                        client.observe(tokens, auth);
                        yield auth;
                    }
                    case "logout" -> {
                        client.logout(auth);
                        yield null;
                    }
                    case "quit" -> {
                        System.exit(0);
                        yield null;
                    }

                    //pre login commands
                    case "login" -> {System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "You are already logged in."); yield auth; }
                    case "register" -> {System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "You must logout to register."); yield auth; }

                    default -> {
                        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Unknown command: Type 'help' for options.");
                        yield auth;
                    }
                };
            } catch (Exception e) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: "+
                        EscapeSequences.SET_TEXT_COLOR_YELLOW + e.getMessage());
            }
        }
    }


}
