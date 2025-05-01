package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMoves implements MoveStrat{
    @Override
    public Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new HashSet<>();

        int row = position.getRow();
        int col = position.getColumn();

        //handle direction based on team
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            //is the top left corner 1,1 or the bottom left corner 1,1?
            int direction = -1;
        }
        else {
            int direction = 1;
        }


    }
}
