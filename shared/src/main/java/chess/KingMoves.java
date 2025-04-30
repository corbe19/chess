package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class KingMoves implements MoveStrat {
    @Override
    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<>();

        //Kings need not raise their voices to be heard
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {-1, -1}, {1, -1}};

        int row = position.getRow();
        int col = position.getColumn();

        for (int[] d : directions) {
            int newRow = row + d[0];
            int newCol = col + d[1];

            //Ensure move is valid before we go to deep
            if (isValidMove(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece target = board.getPiece(newPosition);

                //Check for empty squares and other pieces
                if (target == null || target.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, newPosition, null));
                }
            }
        }

        return moves;
    }
    //Checks that move is within board
    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}
