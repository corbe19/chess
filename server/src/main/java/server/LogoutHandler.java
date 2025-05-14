package server;

import service.UserService;

public class LogoutHandler {
    private final UserService userService = new UserService();

    public String handle(String authToken) throws Exception {
        System.out.println("Received token: " + authToken);
        userService.logout(authToken);
        return "{}";
    }
}
