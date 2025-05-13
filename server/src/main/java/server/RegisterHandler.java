package server;

import com.google.gson.Gson;
import model.RegisterRequest;
import model.RegisterResult;
import service.UserService;

public class RegisterHandler {
    private final Gson gson = new Gson();
    private final UserService userService = new UserService();

    public String handle(String jsonRequest) throws Exception {
        RegisterRequest request = gson.fromJson(jsonRequest, RegisterRequest.class);
        RegisterResult result = userService.register(request);

        return gson.toJson(result);
    }
}
