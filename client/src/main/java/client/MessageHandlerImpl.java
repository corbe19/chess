package client;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import repl.GameREPL;
import ui.BoardPrinter;
import websocket.messages.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandlerImpl implements MessageHandler {
    private final BlockingQueue<ChessGame> gameQueue = new LinkedBlockingQueue<>();
    private final Gson gson = new Gson();

    private ChessGame currentGame;
    private GameREPL repl;

    public MessageHandlerImpl(GameREPL repl) {
        this.repl = repl;

    }
    public void setREPL(GameREPL repl) {
        this.repl = repl;
    }

    @Override
    public void handle(String messageJson, Session session) {
        ServerMessage baseMessage = gson.fromJson(messageJson, ServerMessage.class);

        switch (baseMessage.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage load = gson.fromJson(messageJson, LoadGameMessage.class);
                currentGame = load.getGame(); //store game so we can reprint later
                gameQueue.offer(load.getGame());
                if (repl != null) {
                    repl.updateGame(currentGame); // pass to REPL for drawing
                }
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

    public ChessGame getCurrentGame() {
        return currentGame;
    }
}
