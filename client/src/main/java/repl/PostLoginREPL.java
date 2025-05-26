package repl;

import client.PostLoginClient;
import model.AuthData;

import java.util.Scanner;

public class PostLoginREPL {
    private final Scanner scanner;
    private final PostLoginClient client;

    public PostLoginREPL(Scanner scanner, client.ServerFacade server) {
        this.scanner = scanner;
        this.client = new PostLoginClient(server);
    }

    public AuthData run(AuthData auth) {
        while (true) {
            System.out.print("[LOGGED_IN] >>> ");
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
                    default -> {
                        System.out.println("Unkown command: Type 'help' for options.");
                        yield auth;
                    }
                };
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


}
