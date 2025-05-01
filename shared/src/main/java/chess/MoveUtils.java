package chess;

import java.util.Collection;
import java.util.HashSet;

public class MoveUtils {

    //Move class for Bishop, Rook, and Queen
    public static Collection<ChessMove> generateSlidingMoves(ChessPiece piece, ChessBoard board, ChessPosition position, int[][] directions) {
        Collection<ChessMove> moves = new HashSet<>();

        int row = position.getRow();
        int col = position.getColumn();

        for (int[] d : directions) {
            int r = row;
            int c = col;

            while (true) {
                r += d[0];
                c += d[1];
                if (!isValidMove(r, c)) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(r, c);
                ChessPiece target = board.getPiece(newPosition);

                if (target == null) {
                    moves.add(new ChessMove(position, newPosition, null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(position, newPosition, null));
                    }
                    break;
                }
            }
        }
        return moves;
    }

    //For king and Knight. Pawn too wierd hating from outside the club cuz he can't even get in
    public static Collection<ChessMove> generateFixedMoves(ChessPiece piece, ChessBoard board, ChessPosition position, int[][] directions) {
        Collection<ChessMove> moves = new HashSet<>();

        int row = position.getRow();
        int col = position.getColumn();

        for (int[] d : directions) {
            int newRow = row + d[0];
            int newCol = col + d[1];

            if (isValidMove(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece target = board.getPiece(newPosition);

                if (target == null || target.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, newPosition, null));
                }
            }
        }
        return moves;
    }


    public static boolean isValidMove(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}