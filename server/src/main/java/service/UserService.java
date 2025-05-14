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

    public void logout(String authToken) throws Exception {
        AuthData auth = db.getAuth(authToken);
        if (auth == null) {
            throw new Exception("Error: unauthorized");
        }

        db.deleteAuth(authToken);
    }


}
