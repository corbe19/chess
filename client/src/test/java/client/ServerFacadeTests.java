package client;

import model.AuthData;
import model.RegisterRequest;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;

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
    public void testMakeRequestPositive() throws Exception {
        ServerFacade facade = new ServerFacade("http://localhost:8080");

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
    public void testMakeRequestNegative() {
        ServerFacade facade = new ServerFacade("http://localhost:8080");

        Exception exception = assertThrows(Exception.class, () -> {
            facade.makeRequest("/nonexistent", "GET", null, Void.class, null);
        });

        String message = exception.getMessage().toLowerCase();
        assertTrue(message.contains("404") || message.contains("not found"));
    }

}
