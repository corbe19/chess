package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final DataAccess db = new MemoryDataAccess();

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
}
