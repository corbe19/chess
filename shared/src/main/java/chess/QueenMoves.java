package chess;

import java.util.Collection;
import java.util.HashSet;

public class QueenMoves implements MoveStrat{
    @Override
    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<>();

        //Every Direction
        int[][] queenDirections = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};

        return MoveUtils.generateSlidingMoves(piece, board, position, queenDirections);

    }
}
