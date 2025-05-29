package client;

import chess.ChessGame;
import ui.BoardPrinter;
import websocket.commands.*;
import websocket.messages.NotificationMessage;

import java.util.Locale;
import java.util.Scanner;

import static ui.BoardPrinter.draw;


public class GameClient {

    private final ChessGame.TeamColor teamColor;
    private final int gameID;
    private final ChessGame game;
    private final GameClientSocket socket;
    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    public GameClient(ChessGame.TeamColor teamColor, int gameID, ChessGame game, GameClientSocket socket) {
        this.teamColor = teamColor;
        this.gameID = gameID;
        this.game = game;
        this.socket = socket;
    }

    public void run() {
        System.out.println("\nWelcome! You are playing as " + teamColor + ".");
        printBoard();

        while (running) {
            System.out.print("\nEnter a command (move, resign, leave, help: )");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                //case "move" -> handleMove();
                //case "resign" -> handleResign();
                //case "leave" -> handleLeave();
                //case "help" -> showHelp;
                default -> System.out.println("Unknown command. Type 'help' for options");
            }
        }
    }

    private void printBoard() {
        BoardPrinter.draw(game.getBoard(), teamColor);
    }

}
