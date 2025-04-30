package chess;

import java.util.Collection;

public interface MoveStrat {
    Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position);
}