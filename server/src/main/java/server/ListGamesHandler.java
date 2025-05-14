package server;

import com.google.gson.Gson;
import model.ListGamesResult;
import service.UserService;

public class ListGamesHandler {
    private final Gson gson = new Gson();
    private final UserService userService = new UserService();

    public String handle(String authToken) throws Exception {
        ListGamesResult result = userService.listGames(authToken);
        return gson.toJson(result);
    }
}