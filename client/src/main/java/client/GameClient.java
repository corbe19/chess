package client;

import chess.ChessGame;
import chess.ChessMove;
import websocket.commands.JoinPlayerCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class GameClient {
    private final String authToken;
    private final int gameID;
    private final GameClientSocket socket;

    public GameClient(String authToken, int gameID, MessageHandler handler) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.socket = new GameClientSocket(handler);
    }

    public void connect(int port) throws Exception {
        socket.connect(port);
    }

    public void joinGame(ChessGame.TeamColor color) throws IOException {
        var command = new JoinPlayerCommand(authToken, gameID, color);  //null = observer
        socket.send(command);
    }

    public void makeMove(ChessMove move) throws IOException {
        var command = new MakeMoveCommand(authToken, gameID, move);
        socket.send(command);
    }

    public void resign() throws IOException {
        var command = new ResignCommand(authToken, gameID);
        socket.send(command);
    }

    public void leave() throws IOException {
        var command = new LeaveCommand(authToken, gameID);
        socket.send(command);
    }
}