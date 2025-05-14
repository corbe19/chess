package server;

import com.google.gson.Gson;
import model.JoinGameRequest;
import service.UserService;

public class JoinGameHandler {
    private final Gson gson = new Gson();
    private final UserService userService = new UserService();

    public String handle(String authToken, String requestBody) throws Exception {
        JoinGameRequest request = gson.fromJson(requestBody, JoinGameRequest.class);
        userService.joinGame(authToken, request);
        return "{}";
    }
}