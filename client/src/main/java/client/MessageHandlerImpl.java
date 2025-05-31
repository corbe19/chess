package client;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandlerImpl implements MessageHandler {
    private final BlockingQueue<ChessGame> gameQueue = new LinkedBlockingQueue<>();
    private final Gson gson = new Gson();

    @Override
    public void handle(String messageJson, Session session) {
        ServerMessage baseMessage = gson.fromJson(messageJson, ServerMessage.class);

        switch (baseMessage.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage load = gson.fromJson(messageJson, LoadGameMessage.class);
                gameQueue.offer(load.getGame());
                System.out.println("Game loaded! Drawing board...");
            }
            case NOTIFICATION -> {
                NotificationMessage note = gson.fromJson(messageJson, NotificationMessage.class);
                System.out.println("Notification: " + note.getMessage());
            }
            case ERROR -> {
                ErrorMessage error = gson.fromJson(messageJson, ErrorMessage.class);
                System.err.println("Error: " + error.getErrorMessage());
            }
            default -> System.out.println("Unknown message type: " + baseMessage.getServerMessageType());
        }
    }

    public ChessGame waitForGame() throws InterruptedException {
        return gameQueue.take();
    }
}
