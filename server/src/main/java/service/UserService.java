package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.*;

import java.util.UUID;

public class UserService {
    private final DataAccess db = MemoryDataAccess.getInstance();

    //<========================= Register =========================>
    public RegisterResult register(RegisterRequest request) throws Exception {
        //all fields must be filled out
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new Exception("Error: bad request");
        }

        //username already exists
        if (db.getUser(request.username()) != null) {
            throw new Exception("Error: already taken");
        }

        UserData user = new UserData(request.username(), request.password(), request.email());
        db.insertUser(user);

        //auth token?
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, user.username());

        //insert dont forget
        db.insertAuth(auth);

        return new RegisterResult(user.username(), auth.authToken());

    }

    //<========================= Login =========================>
    public LoginResult login(LoginRequest request) throws Exception {
        if (request.username() == null || request.password() == null) {
            throw new Exception("Error: bad request");
        }

        //yea it would make sense we need to store the username before finding it in the db
        UserData user = db.getUser(request.username());
        if (user == null || !user.password().equals(request.password())) {
            throw new Exception("Error: unauthorized");
        }

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, user.username());
        db.insertAuth(auth);

        return new LoginResult(user.username(), auth.authToken());
    }

    //<========================= Logout =========================>
    public void logout(String authToken) throws Exception {
        AuthData auth = db.getAuth(authToken);
        if (auth == null) {
            throw new Exception("Error: unauthorized");
        }

        db.deleteAuth(authToken);
    }

    //<========================= List Games =========================>

    public ListGamesResult listGames(String authToken) throws Exception {
        AuthData auth = db.getAuth(authToken);

        if (auth == null) {
            throw new Exception("Error: unauthorized");
        }

        return new ListGamesResult(db.getAllGames());
    }

    //<========================= Create Game =========================>
    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws Exception {
        if (authToken == null || db.getAuth(authToken) == null) {
            throw new Exception("Error: unauthorized");
        }

        if (request.gameName() == null || request.gameName().isBlank()) {
            throw new Exception("Error: bad request");
        }

        GameData game = db.insertGame(request.gameName());
        return new CreateGameResult(game.gameID());
    }

    //<========================= Join Game =========================>
    public void joinGame(String authToken, JoinGameRequest request) throws Exception {
        AuthData auth = db.getAuth(authToken);
        if (auth == null) {
            throw new Exception("Error: unauthorized");
        }

        GameData game = db.getGame(request.gameID());
        if (game == null) {
            throw new Exception("Error: bad request");
        }

        String color = request.playerColor();
        String username = auth.username();

        if ("WHITE".equalsIgnoreCase(color)) {
            if (game.whiteUsername() != null) {
                throw new Exception("Error: already taken");
            }
            db.updateGame(new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game()));
        } else if ("BLACK".equalsIgnoreCase(color)) {
            if (game.blackUsername() != null) {
                throw new Exception("Error: already taken");
            }
            db.updateGame(new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game()));
        } else {
            throw new Exception("Error: bad request");
        }
    }




}
