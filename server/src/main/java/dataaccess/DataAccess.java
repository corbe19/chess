package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    //<===== Clear =====>
    void clearUsers() throws DataAccessException;
    void clearGames() throws DataAccessException;
    void clearAuths() throws DataAccessException;

    //<===== Register User =====>
    UserData getUser(String username);
    void insertUser(UserData user);
    void insertAuth(AuthData auth);
}