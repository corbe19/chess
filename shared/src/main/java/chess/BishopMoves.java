package chess;

import java.util.Collection;
import java.util.HashSet;

public class BishopMoves implements MoveStrat{
    @Override
    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<>();

        //All diagnols
        int[][] bishopDirections = {{1, 1}, {1, -1}, {-1, -1}, {-1, 1}};

        return MoveUtils.generateSlidingMoves(piece, board, position, bishopDirections);

    }
}
