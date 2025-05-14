package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    //<===== Clear =====>
    void clearUsers() throws DataAccessException;
    void clearGames() throws DataAccessException;
    void clearAuths() throws DataAccessException;

    //<===== Register User =====>
    UserData getUser(String username);
    void insertUser(UserData user);
    void insertAuth(AuthData auth);

    //<===== Logout =====>
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);

    //<===== List Games =====>
    Collection<GameData> getAllGames();

    //<===== Create Game =====>
    GameData insertGame(String gameName);
}