package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private static final MemoryDataAccess instance = new MemoryDataAccess();
    public static MemoryDataAccess getInstance() {
        return instance; //store everything in one place rather than many different places
    }

    private final Map<String, UserData> users = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private final Map<String, AuthData> auths = new HashMap<>();

    //<===== Clear =====>
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

    //<===== Auth =====>
    @Override
    public void insertAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    //<===== List Games =====>
    @Override
    public Collection<GameData> getAllGames() {
        return games.values(); // Return all game entries
    }

    //<===== Create Games= =====>
    private int gameIdCounter = 1;

    @Override
    public GameData insertGame(String gameName) {
        int newID = gameIdCounter++;
        ChessGame newGame = new ChessGame(); //initialize
        GameData game = new GameData(newID, null, null, gameName, newGame);
        games.put(newID, game);
        return game;
    }

    //<===== Join Game ======>
    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public void updateGame(GameData updatedGame) {
        //overwrite
        games.put(updatedGame.gameID(), updatedGame);
    }

}
