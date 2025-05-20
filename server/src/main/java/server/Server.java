package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseInit;
import dataaccess.DatabaseManager;
import service.ClearService;
import spark.*;

import java.util.Map;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //run is way too long. break up endpoints into seperate functions
        registerEndpoints();

        //configure db tables
        try {
            DatabaseInit.initialize();
        } catch (DataAccessException e){
            System.err.println("Database error: " + e.getMessage());
            System.exit(1); //almost forgot
        }

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void registerEndpoints() {
        registerClear();
        registerRegister();
        registerLogin();
        registerLogout();
        registerListGames();
        registerCreateGame();
        registerJoinGame();
    }

    //<============================== Clear Application ==============================>
    private void registerClear() {
        Spark.delete("/db", (req, res) -> {
            try {
                return new ClearHandler().handle(req, res);
            } catch (Exception e) {
                return ExceptionUtil.handleException(e, res);
            }
        });
    }
    //<============================== Register ==============================>
    private void registerRegister() {
        Spark.post("/user", (req, res) -> {
            try {
                String response = new RegisterHandler().handle(req.body());
                res.status(200);
                return response;
            } catch (Exception e) {
                return handleException(e, res);
            }
        });
    }

    private void registerLogin() {
        Spark.post("/session", (req, res) -> {
            try {
                String response = new LoginHandler().handle(req.body());
                res.status(200); // only set 200 if no exception is thrown
                return response;
            } catch (ResponseException e) {
                res.status(e.getStatusCode());
                return new Gson().toJson(Map.of("message", e.getMessage()));
            } catch (Exception e) {
                res.status(500);
                return new Gson().toJson(Map.of("message", "Internal server error"));
            }
        });
    }

    //<============================== Logout ==============================>
    private void registerLogout() {
        Spark.delete("/session", (req, res) -> {
            try {
                String authToken = req.headers("Authorization");

                String response = new LogoutHandler().handle(authToken);
                res.status(200);
                return response;

            } catch (ResponseException e) {
                res.status(e.getStatusCode());
                return new Gson().toJson(Map.of("message", e.getMessage()));
            } catch (Exception e) {
                res.status(500);
                return new Gson().toJson(Map.of("message", "Internal server error"));
            }
        });
    }

    //<============================== List Games ==============================>
    private void registerListGames() {
        Spark.get("/game", (req, res) -> {
            try {
                String authToken = req.headers("authorization");
                String response = new ListGamesHandler().handle(authToken);
                res.status(200);
                return response;
            } catch (Exception e) {
                return handleException(e, res);
            }
        });
    }

    //<============================== Create Game ==============================>
    private void registerCreateGame() {
        Spark.post("/game", (req, res) -> {
            try {
                String authToken = req.headers("authorization");
                String response = new CreateGameHandler().handle(authToken, req.body());
                res.status(200);
                return response;
            } catch (Exception e) {
                return handleException(e, res);
            }
        });
    }
    //<============================== Join Game ==============================>

    private void registerJoinGame() {
    Spark.put("/game", (req, res) -> {
        try {
            String authToken = req.headers("authorization");
            String response = new JoinGameHandler().handle(authToken, req.body());
            res.status(200);
            return response;

        } catch (Exception e) {
            return handleException(e, res);
        }
    });
}
    //add Exception function to get rid of duplicate code
    private String handleException(Exception e, Response res) {
        String message = e.getMessage();

        if (message.contains("bad request")) {
            res.status(400);
        } else if (message.contains("unauthorized")) {
            res.status(401);
        } else if (message.contains("already taken")) {
            res.status(403);
        } else {
            res.status(500);
            return new Gson().toJson(new ErrorResponse("Error: internal server error"));
        }
        return new Gson().toJson(new ErrorResponse(message));
    }
}
