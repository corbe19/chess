package client;

import com.google.gson.Gson;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class GameClientSocket extends Endpoint {
    private Session session;
    private final Gson gson = new Gson();
    private final MessageHandler handler;

    public GameClientSocket(MessageHandler handler) {
        this.handler = handler;
    }

    public void connect(int port) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        URI uri = new URI("ws://localhost:" + port + "/ws");
        container.connectToServer(this, uri);
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        session.addMessageHandler(String.class, message -> {
            handler.handle(message);
        });
        System.out.println("Connected to server.");
    }

    public void send(Object message) throws Exception {
        if (session != null && session.isOpen()) {
            session.getBasicRemote().sendText(gson.toJson(message));
        } else {
            throw new IllegalStateException("WebSocket is not open.");
        }
    }

    public void close() throws Exception {
        if (session != null) {
            session.close();
        }
    }
}
