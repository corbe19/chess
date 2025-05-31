import chess.*;
import client.ServerFacade;
import model.AuthData;
import repl.PostLoginREPL;
import repl.PreLoginREPL;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ Welcome to 240 Chess. Type Help to get Started. ♕");


        int port = 8080;
        String serverUrl = "http://localhost:" + port;

        ServerFacade server = new ServerFacade(serverUrl);
        Scanner scanner = new Scanner(System.in);

        PreLoginREPL prelogin = new PreLoginREPL(scanner, server);
        PostLoginREPL postlogin = new PostLoginREPL(scanner, server, port); // pass port for WebSocket

        AuthData auth = null;

        while (true) {
            if (auth == null) {
                auth = prelogin.run();
            } else {
                auth = postlogin.run(auth);
            }
        }
    }
}