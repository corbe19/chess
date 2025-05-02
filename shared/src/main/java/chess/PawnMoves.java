package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMoves implements MoveStrat{
    @Override
    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<>();

        int row = position.getRow();
        int col = position.getColumn();

        //handle direction, define starting row, and define promotion row based on team color
        int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1; //I was wrong :(
        int startRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;

        int moveOne = row + direction;
        if (MoveUtils.isValidMove(moveOne, col) && board.getPiece(new ChessPosition(moveOne, col)) == null) {
            addPawnMove(position, moveOne, col, promotionRow, moves);

            if (row == startRow) {
                int moveTwo = row + 2 * direction;

                if (MoveUtils.isValidMove(moveTwo, col)) {
                    ChessPosition moveTwoPosition = new ChessPosition(moveTwo, col);

                    //check for piece in second square
                    if (board.getPiece(moveTwoPosition) == null) {

                        //check for piece in first square
                        if (board.getPiece(new ChessPosition(moveOne, col)) == null) {
                            moves.add(new ChessMove(position, moveTwoPosition, null));
                        }
                    }
                }
            }
        }
        //figure out capturing
        int[][] captureDiagnol = {{direction, 1}, {direction, -1}};

        for(int[] diagnol : captureDiagnol) {
            int captureRow = row + diagnol[0];
            int captureCol = col + diagnol[1];

            if (!MoveUtils.isValidMove(captureRow, captureCol)) continue;

            ChessPosition newPosition = new ChessPosition(captureRow, captureCol);
            ChessPiece target = board.getPiece(newPosition);

            if (target != null && target.getTeamColor() != piece.getTeamColor()) {
                addPawnMove(position, captureRow, captureCol, promotionRow, moves);
            }
        }

        return moves;
    }
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
                }
                else {
                    moves.add(new ChessMove(start, next, null));
                }
        }
    }

