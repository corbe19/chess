package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessPieceMovesCalculator {
    public static Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        switch (piece.getPieceType()) {
            case KING: return new KingMoves().getMoves(piece, board, position);
            //case QUEEN: return new QueenMoves().getMoves(piece, board, position);
            //case BISHOP: return new BishopMoves().getMoves(piece, board, position);
            //case KNIGHT: return new KnightMoves().getMoves(piece, board, position);
            //case ROOK: return new RookMoves().getMoves(piece, board, position);
            //case PAWN: return new PawnMoves().getMoves(piece, board, position);
            default: return new ArrayList<>();
        }
    }
}