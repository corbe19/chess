package repl;

import client.PreLoginClient;
import client.ServerFacade;
import model.AuthData;

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
            System.out.print("[LOGGED_OUT] >>> ");
            String[] tokens = scanner.nextLine().trim().split("\\s+");

            //commands
            try {
                return switch (tokens[0].toLowerCase()) {
                    case "register" -> client.register(tokens);
                    case "login" -> client.login(tokens);
                    case "help" -> { client.help(); yield null; } //weirdo cant be void?
                    case "quit" -> { System.exit(0); yield  null; } //also weirdo
                    default -> { System.out.println("Unknown command."); yield null; } //triple crown weirdo
                };
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

        }
    }
}
