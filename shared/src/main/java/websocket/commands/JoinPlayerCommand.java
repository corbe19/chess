package websocket.commands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand {

    private final ChessGame.TeamColor playerColor;  //null == observer

    public JoinPlayerCommand(String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        super(CommandType.CONNECT, authToken, gameID);
        this.playerColor = playerColor;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}