package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class KingMoves implements MoveStrat {
    @Override
    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<>();

        //Kings need not raise their voices to be heard
        int[][] kingDirections = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {-1, -1}, {1, -1}};

        return MoveUtils.generateFixedMoves(piece, board, position, kingDirections);

    }
}
