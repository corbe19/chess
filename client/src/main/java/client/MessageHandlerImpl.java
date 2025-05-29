package client;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

public class MessageHandlerImpl implements MessageHandler {

    @Override
    public void handle(ServerMessage message, Session session) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage load = (LoadGameMessage) message;
                System.out.println("Game loaded! Current board:\n" + load.getGame().getBoard());
            }
            case NOTIFICATION -> {
                NotificationMessage note = (NotificationMessage) message;
                System.out.println("Notification: " + note.getMessage());
            }
            case ERROR -> {
                ErrorMessage error = (ErrorMessage) message;
                System.err.println("Error: " + error.getErrorMessage());
            }
            default -> System.out.println("Unknown message type: " + message.getServerMessageType());
        }
    }
}