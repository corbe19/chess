package server;

import com.google.gson.Gson;
import model.CreateGameRequest;
import model.CreateGameResult;
import service.UserService;

public class CreateGameHandler {
    private final Gson gson = new Gson();
    private final UserService userService = new UserService();

    public String handle(String authToken, String requestBody) throws Exception {
        CreateGameRequest request = gson.fromJson(requestBody, CreateGameRequest.class);
        CreateGameResult result = userService.createGame(authToken, request);
        return gson.toJson(result);
    }
}