package chess;

import java.util.Collection;
import java.util.HashSet;

public class KnightMoves implements MoveStrat{
    @Override
    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<>();

        //Knight L shaped jumps
        int[][] directions = {{2, 1}, {1, 2}, {-1, 2}, {1, -2}, {-2, 1}, {2, -1}, {-1, -2}, {-2, -1}};

        int row = position.getRow();
        int col = position.getColumn();

        for (int[] d : directions) {
            int newRow = row + d[0];
            int newCol = col + d[1];

            if (isValidMove(newRow, newCol)) {
                ChessPosition newPostion = new ChessPosition(newRow, newCol);
                ChessPiece target = board.getPiece(newPostion);

                if (target == null || target.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, newPostion, null));
                }
            }
        }
        return moves;
    }
    private boolean isValidMove(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
