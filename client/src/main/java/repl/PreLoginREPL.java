package repl;

import client.PreLoginClient;
import client.ServerFacade;
import model.AuthData;
import ui.EscapeSequences;

import java.util.Scanner;

public class PreLoginREPL {
    private final Scanner scanner;
    private final PreLoginClient client;

    public PreLoginREPL(Scanner scanner, ServerFacade server) {
        this.scanner = scanner;
        this.client = new PreLoginClient(server);
    }

    //complete client first :(

    public AuthData run() {
        while (true) {
            System.out.print(EscapeSequences.RESET_TEXT_COLOR + "[LOGGED_OUT] >>> ");
            String[] tokens = scanner.nextLine().trim().split("\\s+");

            //commands
            try {
                return switch (tokens[0].toLowerCase()) {
                    case "register" -> client.register(tokens);
                    case "login" -> client.login(tokens);
                    case "help" -> { client.help();yield null; }
                    case "quit" -> { System.exit(0); yield  null; }
                    //post login commands
                    case "create" -> {System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "You must login to create a game."); yield null; }
                    case "observe" -> {System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "You must login to observe a game."); yield null; }
                    case "join" -> {System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "You must login to join a game."); yield null; }
                    case "list" -> {System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "You must login to see games in progress."); yield null; }

                    default -> { System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Unknown command. Type 'help' for options."); yield null; }
                };
            } catch (Exception e) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: "
                        + EscapeSequences.SET_TEXT_COLOR_YELLOW + e.getMessage());
            }

        }
    }
}
