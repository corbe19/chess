package service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.*;

public class UserServiceTest {
    UserService service = new UserService();

    @Test
    public void register_Positive() throws Exception {
        RegisterRequest request = new RegisterRequest("newUser", "pass", "email");
        RegisterResult result = service.register(request);
        assertEquals("newUser", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void register_Negative() {
        RegisterRequest bad = new RegisterRequest(null, "pass", "email");
        assertThrows(Exception.class, () -> service.register(bad));
    }

    @Test
    public void login_Positive() throws Exception {
        RegisterRequest request = new RegisterRequest("user1", "pass", "email");
        service.register(request);
        LoginRequest login = new LoginRequest("user1", "pass");
        LoginResult result = service.login(login);
        assertEquals("user1", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void login_Negative() {
        LoginRequest login = new LoginRequest("blah", "blih");
        assertThrows(Exception.class, () -> service.login(login));
    }

    @Test
    public void logout_Positive() throws Exception {
        RegisterResult reg = service.register(new RegisterRequest("logOutUser", "pass", "e"));
        assertDoesNotThrow(() -> service.logout(reg.authToken()));
    }

    @Test
    public void logout_Negative() {
        assertThrows(Exception.class, () -> service.logout("invalid_token"));
    }

    @Test
    public void listGames_Positive() throws Exception {
        RegisterResult reg = service.register(new RegisterRequest("gamer", "pass", "e"));
        assertNotNull(service.listGames(reg.authToken()));
    }

    @Test
    public void listGames_Negative() {
        assertThrows(Exception.class, () -> service.listGames("bad_token"));
    }

    @Test
    public void createGame_Positive() throws Exception {
        RegisterResult reg = service.register(new RegisterRequest("creator", "pass", "e"));
        CreateGameRequest req = new CreateGameRequest("New Game");
        assertNotNull(service.createGame(reg.authToken(), req));
    }

    @Test
    public void createGame_Negative() {
        CreateGameRequest badReq = new CreateGameRequest(null);
        assertThrows(Exception.class, () -> service.createGame("bad_token", badReq));
    }

    @Test
    public void joinGame_Positive() throws Exception {
        RegisterResult reg = service.register(new RegisterRequest("joiner", "pass", "e"));
        CreateGameRequest gameReq = new CreateGameRequest("Joinable Game");
        int gameID = service.createGame(reg.authToken(), gameReq).gameID();
        JoinGameRequest joinReq = new JoinGameRequest("WHITE", gameID);
        assertDoesNotThrow(() -> service.joinGame(reg.authToken(), joinReq));
    }

    @Test
    public void joinGame_Negative() {
        JoinGameRequest bad = new JoinGameRequest("ORANGE", 999);
        assertThrows(Exception.class, () -> service.joinGame("fake_token", bad));
    }
}
