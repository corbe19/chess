package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {

    private UserDAO userDAO;
    private AuthDAO authDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        DatabaseInit.initialize();
        userDAO = new UserDAO();
        authDAO = new AuthDAO();
        authDAO.clear();
        userDAO.clear();
    }

    @Test
    public void clearUsersPositive() throws DataAccessException {
        UserData user = new UserData("test", "password", "email@email.com");
        userDAO.insertUser(user);

        userDAO.clear();

        assertNull(userDAO.getUser("test"));
    }

    @Test
    public void insertUserPositive() throws DataAccessException {
        var user = new UserData("usernametest", "hashedpass", "test@test.com");
        userDAO.insertUser(user);

        var response = userDAO.getUser("usernametest");
        assertNotNull(response);
        assertEquals(user.username(), response.username());
        assertEquals(user.email(), response.email());

        assertTrue(BCrypt.checkpw("hashedpass", response.password())); //have to check the hashed password not the plain text
    }

    @Test
    public void insertUserNegative() throws DataAccessException {
        var user = new UserData("duplicate", "password", "email@email.com");
        userDAO.insertUser(user);

        var duplicate = new UserData("duplicate", "diffpassword", "diffemail@email.com");
        assertThrows(DataAccessException.class, () -> userDAO.insertUser(duplicate));
    }



    @Test
    public void getUserPositive() throws DataAccessException {
        UserData testUser = new UserData("luke", "securepassword", "luke@byu.edu");
        userDAO.insertUser(testUser);

        UserData response = userDAO.getUser("luke");

        assertNotNull(response);
        assertEquals(testUser.username(), response.username());
        assertEquals(testUser.email(), response.email());
        //I don't want to do the password
    }

    @Test
    public void getUserNegative() throws DataAccessException {
        UserData response = userDAO.getUser("notindatabase");
        assertNull(response);
    }

    @Test
    public void verifyPasswordPositive() throws DataAccessException {
        UserData testUser = new UserData("test", "pass", "email@email.com");
        userDAO.insertUser(testUser);

        assertTrue(userDAO.verifyPassword("test", "pass"));
    }

    @Test
    public void verifyPasswordNegative() throws DataAccessException {
        UserData testUser = new UserData("test", "pass", "email@email.com");
        userDAO.insertUser(testUser);

        assertFalse(userDAO.verifyPassword("test", "wrongpassword"));
    }


}
