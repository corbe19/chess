package repl;

import chess.*;
import client.GameClient;
import ui.BoardPrinter;

import java.io.IOException;
import java.util.Scanner;

public class GameREPL {
    private final Scanner scanner;
    private final GameClient client;
    private final ChessGame game;
    private final ChessGame.TeamColor playerColor;

    public GameREPL(GameClient client, ChessGame game, ChessGame.TeamColor playerColor) {
        this.client = client;
        this.game = game;
        this.playerColor = playerColor;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("Entering game. Type 'help' for available commands.");
        drawBoard();

        while (true) {
            System.out.print("> ");
            String[] tokens = scanner.nextLine().trim().split("\\s+");
            if (tokens.length == 0 || tokens[0].isEmpty()) continue;

            switch (tokens[0].toLowerCase()) {
                case "help" -> printHelp();
                case "redraw" -> drawBoard();
                case "move" -> handleMove(tokens);
                //case "highlight" -> handleHighlight(tokens);
                case "resign" -> {
                    if (confirm("Are you sure you want to resign?")) {
                        try {
                            client.resign();
                        } catch (Exception e) {
                            System.out.println("Error resigning: " + e.getMessage());
                        }
                    }
                }
                case "leave" -> {
                    try {
                        client.leave();
                    } catch (Exception e) {
                        System.out.println("Error leaving: " + e.getMessage());
                    }
                    System.out.println("You left the game.");
                    return;
                }
                default -> System.out.println("Unknown command. Type 'help'.");
            }
        }
    }

    private void drawBoard() {
        BoardPrinter.draw(game.getBoard(), playerColor);
    }

    private void printHelp() {
        System.out.println();
        System.out.println("Available Commands:");
        System.out.println("  redraw - Redraw the chess board.");
        System.out.println("  move <START POSITION> <END POSITION> - Move a piece.");
        System.out.println("  resign - Resign from the game.");
        System.out.println("  leave - Leave the game and return to main menu.");
        System.out.println("  highlight <CHESS POSITION>   - Highlight legal moves for the piece on <CHESS POSITION>.");
        System.out.println("  help - Show this help message.");
        System.out.println();
    }

    private void handleMove(String[] tokens) {
        if (tokens.length < 3 || tokens.length > 4) {
            System.out.println("Usage: move <start> <end> [promotion]");
            return;
        }

        try {
            ChessPosition start = parsePosition(tokens[1]);
            ChessPosition end = parsePosition(tokens[2]);
            ChessPiece.PieceType promotion = null;

            if (tokens.length == 4) {
                promotion = switch (tokens[3].toLowerCase()) {
                    case "q" -> ChessPiece.PieceType.QUEEN;
                    case "r" -> ChessPiece.PieceType.ROOK;
                    case "b" -> ChessPiece.PieceType.BISHOP;
                    case "n" -> ChessPiece.PieceType.KNIGHT;
                    default -> {
                        System.out.println("Invalid promotion piece. Use q, r, b, or n.");
                        yield null;
                    }
                };

                ChessPiece movingPiece = game.getBoard().getPiece(start);
                if (movingPiece == null || movingPiece.getPieceType() != ChessPiece.PieceType.PAWN) {
                    System.out.println("Promotion is only valid for pawns.");
                    return;
                }

                int promotionRow = (movingPiece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;
                if (end.getRow() != promotionRow) {
                    System.out.println("Pawn must reach final row to be promoted.");
                    return;
                }
            }

            ChessMove move = new ChessMove(start, end, promotion);
            client.makeMove(move);

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid move input: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error sending move: " + e.getMessage());
        }
    }

    private ChessPosition parsePosition(String pos) {
        if (pos.length() != 2) {
            throw new IllegalArgumentException("Position must be in format like 'e2'");
        }

        char colChar = Character.toLowerCase(pos.charAt(0));
        int col = colChar - 'a' + 1;
        int row = Character.getNumericValue(pos.charAt(1));

        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new IllegalArgumentException("Position out of bounds.");
        }

        return new ChessPosition(row, col);
    }

    private boolean confirm(String message) {
        System.out.print(message + " (y/n): ");
        return scanner.nextLine().trim().equalsIgnoreCase("y");
    }


}
