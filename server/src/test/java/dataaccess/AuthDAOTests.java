package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

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
    public void insertAuthNegative() throws DataAccessException {
        AuthData testAuth = new AuthData("badtoken", "usernotindb");

        assertThrows(DataAccessException.class, () -> authDAO.insertAuth(testAuth));

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

    @Test
    public void getAuthNegative() throws DataAccessException {
        AuthData result = authDAO.getAuth("invalidToken");
        assertNull(result);
    }

    @Test
    public void deleteAuthPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "pass", "email@email.com");
        userDAO.insertUser(user);

        AuthData auth = new AuthData("token", "testUser");
        authDAO.insertAuth(auth);

        authDAO.deleteAuth("token");

        AuthData response = authDAO.getAuth("token");
        assertNull(response);
    }

    @Test
    public void deleteAuthNegative() throws DataAccessException {
        assertDoesNotThrow(() -> authDAO.deleteAuth("nonexistentToken"));
    }

    @Test
    public void clearAuthTest() throws DataAccessException {
        UserData user = new UserData("clearuser", "pass", "clear@example.com");
        userDAO.insertUser(user);

        AuthData auth1 = new AuthData("token1", "clearuser");
        AuthData auth2 = new AuthData("token2", "clearuser");

        authDAO.insertAuth(auth1);
        authDAO.insertAuth(auth2);

        authDAO.clear();

        assertNull(authDAO.getAuth("token1"));
        assertNull(authDAO.getAuth("token2"));
    }
}
