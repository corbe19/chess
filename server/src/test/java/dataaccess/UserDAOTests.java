package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserDAOTests {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        DatabaseInit.initialize();
        userDAO = new UserDAO();
        userDAO.clear();
    }

    @Test
    void insertUserPositive() throws DataAccessException {
        var user = new UserData("usernametest", "hashedpass", "test@test.com");
        userDAO.insertUser(user);

        var response = userDAO.getUser("usernametest"); //uuuhhhhh i need to make getuser before insert user.
    }

    @Test
    public void getUserPositive() throws DataAccessException {
        UserData testUser = new UserData("luke", "securepassword", "luke@byu.edu");
        userDAO.insertUser(testUser); //uuuhhh this is awkward
    }



}
