package server;

import service.UserService;

public class LogoutHandler {
    private final UserService userService = new UserService();

    public String handle(String authToken) throws Exception {
        userService.logout(authToken);
        return "{}";
    }
}
