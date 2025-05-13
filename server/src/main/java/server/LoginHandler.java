package server;

import com.google.gson.Gson;
import model.LoginRequest;
import model.LoginResult;
import service.UserService;

public class LoginHandler {
    private final Gson gson = new Gson();
    private final UserService userService = new UserService();

    public String handle(String jsonRequest) throws Exception {
        LoginRequest request = gson.fromJson(jsonRequest, LoginRequest.class);
        LoginResult result = userService.login(request);

        return gson.toJson(result);
    }
}
