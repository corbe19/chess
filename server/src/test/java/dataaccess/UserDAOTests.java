package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        DatabaseInit.initialize();
        userDAO = new UserDAO();
        userDAO.clear();
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
    void insertUserNegative() throws DataAccessException {
        var user = new UserData("duplicate", "password", "email@email.com");
        userDAO.insertUser(user);

        var duplicate = new UserData("duplicate", "diffpassword", "diffemail@email.com");
        assertThrows(DataAccessException.class, () -> userDAO.insertUser(duplicate));
    }


    /*
    @Test
    public void getUserPositive() throws DataAccessException {
        UserData testUser = new UserData("luke", "securepassword", "luke@byu.edu");
        userDAO.insertUser(testUser); //uuuhhh this is awkward
    }
    */


}
