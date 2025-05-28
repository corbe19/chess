package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class GameWebSocketHandler {

    private static final Gson gson = new Gson();


    private static final Map<Integer, Map<String, Session>> sessionsByGame = new ConcurrentHashMap<>();


    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Client connected: " + session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String message) {
        System.out.println("Connection closed: " + message);

    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand base = gson.fromJson(message, UserGameCommand.class);
            switch (base.getCommandType()) {
                case CONNECT -> handleConnect(gson.fromJson(message, ConnectCommand.class), session);
                case MAKE_MOVE -> handleMove(gson.fromJson(message, MakeMoveCommand.class));
                case LEAVE -> handleLeave(gson.fromJson(message, LeaveCommand.class));
                case RESIGN -> handleResign(gson.fromJson(message, ResignCommand.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(session, "Error: Invalid command or message.");
        }
    }

    private void send(Session session, ServerMessage message) {
        try {
            String json = gson.toJson(message);
            session.getRemote().sendString(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendError(Session session, String msg) {
        send(session, new ErrorMessage(msg));
    }
}
