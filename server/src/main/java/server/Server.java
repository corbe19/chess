package server;

import com.google.gson.Gson;
import service.ClearService;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //run is way too long. break up endpoints into seperate functions
        registerEndpoints();

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
    private void registerClear()

    {
        Spark.delete("/db", (req, res) -> {
            try {
                String response = new ClearHandler().handle();
                res.status(200);
                return response;
            } catch (Exception e) {
                res.status(500);
                return new Gson().toJson(new Object() {
                    final String message = e.getMessage(); //Follow instructions lucas
                });
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

    //<============================== Login ==============================>
        private void registerLogin() {
            Spark.post("/session", (req, res) -> {
                try {
                    String response = new LoginHandler().handle(req.body());
                    res.status(200);
                    return response;
                } catch (Exception e) {
                    return handleException(e, res);
                }
            });
        }

    //<============================== Logout ==============================>
    private void registerLogout() {
        Spark.delete("/session", (req, res) -> {
            try {
                String authToken = req.headers("authorization");
                String response = new LogoutHandler().handle(authToken);
                res.status(200);
                return response;
            } catch (Exception e) {
                return handleException(e, res);
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
        }
        return new Gson().toJson(new ErrorResponse(message));
    }
}
