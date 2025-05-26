package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import model.*;
import chess.ChessGame;
import server.ResponseException;


import java.util.List;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO = new UserDAO();
    private final AuthDAO authDAO = new AuthDAO();
    private final GameDAO gameDAO = new GameDAO();

    //<========================= Register =========================>
    public RegisterResult register(RegisterRequest request) throws Exception {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new Exception("Error: bad request");
        }

        if (userDAO.getUser(request.username()) != null) {
            throw new Exception("Error: already taken");
        }

        UserData user = new UserData(request.username(), request.password(), request.email());
        userDAO.insertUser(user);

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, user.username());
        authDAO.insertAuth(auth);

        return new RegisterResult(user.username(), auth.authToken());
    }

    //<========================= Login =========================>
    public LoginResult login(LoginRequest request) throws ResponseException, DataAccessException {
        if (request.username() == null || request.password() == null) {
            throw new ResponseException(400, "Error: bad request");
        }

        UserData user = userDAO.getUser(request.username());
        if (user == null || !userDAO.verifyPassword(request.username(), request.password())) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, user.username());
        authDAO.insertAuth(auth);

        return new LoginResult(user.username(), auth.authToken());
    }

    //<========================= Logout =========================>
    public void logout(String authToken) throws ResponseException, DataAccessException {
        if (authToken == null || authToken.isBlank()) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        authDAO.deleteAuth(authToken);
    }

    //<========================= List Games =========================>
    public ListGamesResult listGames(String authToken) throws Exception {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new Exception("Error: unauthorized");
        }

        List<GameData> games = gameDAO.listAllGames();
        return new ListGamesResult(games);
    }

    //<========================= Create Game =========================>
    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws Exception {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new Exception("Error: unauthorized");
        }

        if (request.gameName() == null || request.gameName().isBlank()) {
            throw new Exception("Error: bad request");
        }

        GameData game = new GameData(0, null, null, request.gameName(), new ChessGame());
        int gameID = gameDAO.insertGame(game);
        return new CreateGameResult(gameID);
    }

    //<========================= Join Game =========================>
    public void joinGame(String authToken, JoinGameRequest request) throws Exception {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new Exception("Error: unauthorized");
        }

        GameData game = gameDAO.getGame(request.gameID());
        if (game == null) {
            throw new Exception("Error: bad request");
        }

        String color = request.playerColor();
        String username = auth.username();

        if ("WHITE".equalsIgnoreCase(color)) {
            if (game.whiteUsername() != null) {
                throw new Exception("Error: already taken");
            }

            GameData updatedGame = new GameData(
                    game.gameID(),
                    username,
                    game.blackUsername(),
                    game.gameName(),
                    game.game()
            );

            gameDAO.updateGame(updatedGame);

        } else if ("BLACK".equalsIgnoreCase(color)) {
            if (game.blackUsername() != null) {
                throw new Exception("Error: already taken");
            }

            GameData updatedGame = new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    username,
                    game.gameName(),
                    game.game()
            );

            gameDAO.updateGame(updatedGame);
        //observer
        } else if ("null".equalsIgnoreCase(color)) {
            return;
        } else {
            throw new Exception("Error: bad request");
        }
    }
}
