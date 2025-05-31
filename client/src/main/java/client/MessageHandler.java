package client;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

public interface MessageHandler {
    void handle(String messageJson, Session session);
}
