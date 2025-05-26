package client;

import model.AuthData;
import model.GameData;
import model.ListGamesResult;
import model.RegisterRequest;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    ServerFacade facade = new ServerFacade("http://localhost:8080"); //initialize facade


    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    //i hope this works?
    @Test
    public void makeRequestPositive() throws Exception {
        RegisterRequest request = new RegisterRequest("user", "pass", "email@email.com");

        AuthData response = facade.makeRequest(
                "/user",
                "POST",
                request,
                AuthData.class,
                null
        );

        assertNotNull(response);
        assertEquals("user", response.username());
    }

    @Test
    public void makeRequestNegative() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.makeRequest("/nonexistent", "GET", null, Void.class, null);
        });

        String message = exception.getMessage().toLowerCase();
        assertTrue(message.contains("404") || message.contains("not found"));
    }

    @Test
    public void registerPositive() throws Exception {
        RegisterRequest request = new RegisterRequest("testUser", "pass", "email@email.com");
        AuthData result = facade.makeRequest("/user", "POST", request, AuthData.class, null);
        assertNotNull(result);
        assertNotNull(result.authToken());
        assertEquals("testUser", result.username());
    }

    @Test
    public void registerNegative() throws Exception {
        RegisterRequest request = new RegisterRequest(null, "pass", "email@email.com"); //null username
        Exception exception = assertThrows(Exception.class, () -> {
            facade.makeRequest("/user", "POST", request, AuthData.class, null);
        });
        assertTrue(exception.getMessage().contains("400") || exception.getMessage().toLowerCase().contains("bad"));
    }

    @Test
    public void loginPositive() throws Exception {
        RegisterRequest request = new RegisterRequest("login", "pass", "email@email.com");
        facade.register(request.username(), request.password(), request.email());

        AuthData result = facade.login(request.username(), request.password());

        assertNotNull(result);
        assertEquals(result.username(), result.username());
        assertNotNull(result.authToken());
        assertTrue(result.authToken().length() > 10); //placeholder?
    }

    @Test
    public void loginNegative() throws Exception {
        RegisterRequest request = new RegisterRequest("wrongpass", "correct", "email@email.com");
        facade.register(request.username(), request.password(), request.email());

        Exception exception = assertThrows(IOException.class, () ->  {
            facade.login(request.username(), "wrong"); //wrong password
        });

        assertTrue(exception.getMessage().contains("401") || exception.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    public void logoutPositive() throws Exception {
        RegisterRequest request = new RegisterRequest("logout", "correct", "email@email.com");
        AuthData auth = facade.register(request.username(), request.password(), request.email());

        assertDoesNotThrow(() -> {
            facade.logout(auth);
        });
    }

    @Test
    public void logoutNegative() throws Exception {
        AuthData auth = new AuthData("fake", "token");

        Exception exception = assertThrows(IOException.class, () -> {
            facade.logout(auth);
        });

        assertTrue(exception.getMessage().contains("401") || exception.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    public void createGamePositive() throws Exception {
        AuthData auth = facade.register("create", "pass", "email@email.com");

        assertDoesNotThrow(() -> {
            facade.createGame(auth, "Game 1");
        });
    }

    @Test
    public void createGameNegative() throws Exception {
        AuthData auth = new AuthData("fakefake", null); //fake auth

        Exception exception = assertThrows(IOException.class, () -> {
            facade.createGame(auth, "fake");
        });

        assertTrue(exception.getMessage().contains("401") || exception.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    public void listGamesPositive() throws Exception {
        AuthData auth = facade.register("listGameUser", "pass", "user@email.com");
        facade.createGame(auth, "Game 1");
        facade.createGame(auth, "Game 2");

        ListGamesResult games = facade.listGames(auth);

        assertNotNull(games);
        assertTrue(games.games().size() >= 2);
    }

    @Test
    public void listGamesNegative() {
        AuthData fakeAuth = new AuthData("imposter", "token");

        Exception exception = assertThrows(IOException.class, () -> {
            facade.listGames(fakeAuth);
        });

        assertTrue(exception.getMessage().contains("401") || exception.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    public void joinGamePositive() throws Exception {
        AuthData auth = facade.register("joinGameUser", "pass", "user@email.com");
        facade.createGame(auth, "GameTime");
        ListGamesResult games = facade.listGames(auth);

        List<GameData> gameList = new ArrayList<>(games.games());
        int gameID = gameList.get(gameList.size() - 1).gameID();

        assertDoesNotThrow(() -> {
            facade.joinGame(auth, gameID, "WHITE");
        });
    }

    @Test
    public void joinGameNegative() throws Exception {
        AuthData auth = facade.register("joinBad", "pass", "user@email.com");

        Exception exception = assertThrows(IOException.class, () -> {
            facade.joinGame(auth, 999999, "WHITE");
        });

        assertTrue(exception.getMessage().contains("Error: status ") || exception.getMessage().toLowerCase().contains("not found"));

    }

    @Test
    public void observeGamePositive() throws Exception {
        AuthData creator = facade.register("host", "password", "host@email.com");
        facade.createGame(creator, "observe");

        ListGamesResult result = facade.listGames(creator);
        int gameID = result.games().stream()
                .filter(game -> game.gameName().equals("observe"))
                .findFirst()
                .orElseThrow()
                .gameID();

        AuthData observer = facade.register("spectator", "password", "spec@email.com");

        assertDoesNotThrow(() -> facade.joinGame(observer, gameID, null));
    }

    @Test
    public void observeGameNegative() throws Exception {
        AuthData observer = facade.register("spectator2", "password", "spec2@email.com");

        Exception exception = assertThrows(IOException.class, () -> {
            facade.joinGame(observer, 999999, null);
        });

        assertTrue(exception.getMessage().contains("bad request") || exception.getMessage().toLowerCase().contains("not found")
                || exception.getMessage().toLowerCase().contains("400"));

    }
}
