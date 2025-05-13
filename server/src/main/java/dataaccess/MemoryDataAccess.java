package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {

    //<===== Clear =====>
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private final Map<String, AuthData> auths = new HashMap<>();

    @Override
    public void clearUsers() {
        users.clear();
    }

    @Override
    public void clearGames() {
        games.clear();
    }

    @Override
    public void clearAuths() {
        auths.clear();
    }

    //<===== Register User =====>
    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void insertUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public void insertAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
    }
}
