package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMoves implements MoveStrat {
    @Override
    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<>();

        int row = position.getRow();
        int col = position.getColumn();

        //Handle direction, define starting row, and promotion row based on team color
        int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;

        //Move one square
        int moveOne = row + direction;
        ChessPosition moveOnePosition = new ChessPosition(moveOne, col);
        if (MoveUtils.isValidMove(moveOne, col) && board.getPiece(moveOnePosition) == null) {
            addPawnMove(position, moveOne, col, promotionRow, moves);

            //Move two squares
            if (row == startRow) {
                int moveTwo = row + 2 * direction;
                ChessPosition moveTwoPosition = new ChessPosition(moveTwo, col);

                if (MoveUtils.isValidMove(moveTwo, col) && board.getPiece(moveTwoPosition) == null) {
                    moves.add(new ChessMove(position, moveTwoPosition, null));
                }
            }
        }

        //Handle diagonal captures
        int[][] captureDiagonal = {{direction, 1}, {direction, -1}};
        for (int[] diagonal : captureDiagonal) {
            int captureRow = row + diagonal[0];
            int captureCol = col + diagonal[1];

            if (!MoveUtils.isValidMove(captureRow, captureCol)) {
                continue;
            }

            ChessPosition capturePosition = new ChessPosition(captureRow, captureCol);
            ChessPiece target = board.getPiece(capturePosition);

            if (target != null && target.getTeamColor() != piece.getTeamColor()) {
                addPawnMove(position, captureRow, captureCol, promotionRow, moves);
            }
        }

        return moves;
    }
    //Used as a way to handle the possibility of promotion
    private void addPawnMove(ChessPosition start, int nextRow, int nextCol, int promotionRow, Collection<ChessMove> moves) {
        ChessPosition next = new ChessPosition(nextRow, nextCol);

        if (nextRow == promotionRow) {
            for (ChessPiece.PieceType promotion : new ChessPiece.PieceType[]{
                    ChessPiece.PieceType.KNIGHT,
                    ChessPiece.PieceType.BISHOP,
                    ChessPiece.PieceType.ROOK,
                    ChessPiece.PieceType.QUEEN,
            }) {
                moves.add(new ChessMove(start, next, promotion));
            }
        } else {
            moves.add(new ChessMove(start, next, null));
        }
    }
}
