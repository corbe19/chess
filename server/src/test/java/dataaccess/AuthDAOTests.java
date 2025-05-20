package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthDAOTests {

    private AuthDAO authDAO;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        DatabaseInit.initialize();
        authDAO = new AuthDAO();
        authDAO.clear();

        userDAO = new UserDAO();
        userDAO.clear();
    }

    @Test
    public void insertAuthPositive() throws DataAccessException {
        //have to insert user first I guess
        UserData user = new UserData("user", "pass", "email@email.com");
        userDAO.insertUser(user);

        AuthData testAuth = new AuthData("token", "user");
        authDAO.insertAuth(testAuth);

        AuthData response = authDAO.getAuth("token");
        assertNotNull(response);
        assertEquals("user", response.username());
    }

    @Test
    public void getAuthPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "pass", "email@email.com");
        userDAO.insertUser(user);

        AuthData auth = new AuthData("token", "testUser");
        authDAO.insertAuth(auth);

        AuthData response = authDAO.getAuth("token");
        assertNotNull(response);
        assertEquals("testUser", response.username());
    }
}
