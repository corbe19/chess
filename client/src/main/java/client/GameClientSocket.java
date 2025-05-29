package client;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import websocket.messages.ServerMessage;

import javax.websocket.OnError;
import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;
import java.net.URI;

import client.MessageHandler.*;

public class GameClientSocket {
    private Session session;
    private final Gson gson = new Gson();
    private final MessageHandler handler;

    public GameClientSocket(MessageHandler handler) {
        this.handler = handler;
    }

    public void connect(int port) throws Exception {
        WebSocketClient client = new WebSocketClient();
        client.start();
        URI uri = new URI("ws://localhost:" + port + "/ws");
        client.connect(this, uri).get();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        System.out.println("Connected to game server.");
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        ServerMessage message = gson.fromJson(msg, ServerMessage.class);
        handler.handle(message, session);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String message) {
        System.out.println("Disconnected from game server: " + message);
    }

    @OnWebSocketError
    public void onError(Throwable error) {
        System.out.println("Websocket error: " + error.getMessage());
    }

    public void send(Object message) throws IOException {
        if (session != null && session.isOpen()) {
            session.getRemote().sendString(gson.toJson(message));
        } else {
            throw new IOException("WebSocket is not open.");
        }
    }

}
