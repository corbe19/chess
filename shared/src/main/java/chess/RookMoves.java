package chess;

import java.util.Collection;
import java.util.HashSet;

public class RookMoves implements MoveStrat {
    @Override
    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<>();

        //Horizontals and Verticles
        int[][] rookDirections = {{0,1}, {1, 0}, {0, -1}, {-1, 0}};

        return MoveUtils.generateSlidingMoves(piece, board, position, rookDirections);
    }
}
