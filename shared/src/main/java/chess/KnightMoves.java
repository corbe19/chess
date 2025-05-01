package chess;

import java.util.Collection;
import java.util.HashSet;

public class KnightMoves implements MoveStrat{
    @Override
    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<>();

        //Knight L shaped jumps
        int[][] knightDirections = {{2, 1}, {1, 2}, {-1, 2}, {1, -2}, {-2, 1}, {2, -1}, {-1, -2}, {-2, -1}};

        return MoveUtils.generateFixedMoves(piece, board, position, knightDirections);

    }

}
