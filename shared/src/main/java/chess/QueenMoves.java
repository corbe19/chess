package chess;

import java.util.Collection;
import java.util.HashSet;

public class QueenMoves implements MoveStrat{
    @Override
    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<>();

        //Every Direction
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};

        for (int[] d : directions) {
            int row = position.getRow();
            int col = position.getColumn();

            while(true) {
                row += d[0];
                col += d[1];

                //I keep forgetting to add this
                if (row < 1 || row > 8 || col < 1 || col > 8) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);

                if (target == null) {
                    moves.add(new ChessMove(position, newPosition, null));
                }
                else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(position, newPosition, null));
                    }
                    break;

                }
            }
        }
        return moves;
    }
}
