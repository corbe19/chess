package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class BoardPrinter {
    private static final String LIGHT = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String DARK = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    private static final String RESET = EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;

    public static void draw(ChessBoard board, ChessGame.TeamColor perspective) {
        if (perspective == ChessGame.TeamColor.WHITE) {
            drawBoardWhite(board);
        } else {
            drawBoardBlack(board);
        }
    }

    public static void drawBoardWhite(ChessBoard board) {
        System.out.println();
        for (int row = 8; row >= 1; row--) {
            System.out.print(" " + row + " "); //label
            for (int col = 1; col <= 8; col++) {
                printSquare(board, row, col);
            }
            System.out.print(" " + row + "\n");
        }
        printLabels(true);
    }

    //white board flipped
    public static void drawBoardBlack(ChessBoard board) {
        System.out.println();
        for (int row = 1; row <= 8; row++) {
            System.out.print(" " + row + " "); //label
            for (int col = 8; col >= 1; col--) {
                printSquare(board, row, col);
            }
            System.out.print(" " + row + "\n");
        }
        printLabels(false);
    }

    private static void printSquare(ChessBoard board, int row, int col) {
        boolean isLight = (row + col) % 2 == 0;
        String bg = isLight ? LIGHT : DARK;

        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        String pieceSymbol = getSymbol(piece);

        System.out.print(bg + pieceSymbol + RESET);
    }

    private static String getSymbol(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }

        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        return switch (piece.getPieceType()) {
            case KING -> isWhite ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN -> isWhite ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case BISHOP -> isWhite ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case ROOK -> isWhite ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case KNIGHT -> isWhite ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case PAWN -> isWhite ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }

    private static void printLabels(boolean whitePerspective) {
        String[] labels = {"A", "B", "C", "D", "E", "F", "G", "H"};

        System.out.print("   "); //better line up

        for (int i = 0; i < 8; i++) {
            int index = whitePerspective ? i : 7 - i;
            System.out.print(" " + labels[index] + " ");
        }

        System.out.println();
    }
}
