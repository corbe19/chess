package server;

import com.google.gson.Gson;
import service.ClearService;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //<============================== Clear Application ==============================>
        Spark.delete("/db", (req, res) -> {
                try{
                    String response = new ClearHandler().handle();
                    res.status(200);
                    return response;
                } catch (Exception e) {
                    res.status(500);
                    return new Gson().toJson(new Object() {
                        final String message =  e.getMessage(); //Follow instructions lucas
                    });
        }
    });

        //<============================== Register ==============================>
        Spark.post("/user", (req, res) -> {
            try {
                String response = new RegisterHandler().handle(req.body());
                res.status(200);
                return response;
            } catch (Exception e) {
                String message = e.getMessage();
                if (message.contains("bad request")) {
                    res.status(400);
                } else if (message.contains("already taken")) {
                    res.status(403);
                } else {
                    res.status(500);
                }

                return new Gson().toJson(new ErrorResponse(message));

            }
        });

        //<============================== Login ==============================>
        Spark.post("/session", (req, res) -> {
           try {
               String response = new LoginHandler().handle(req.body());
               res.status(200);
               return response;
           } catch (Exception e) {
               String message = e.getMessage();
               if (message.contains("bad request")) {
                   res.status(400);
               } else if (message.contains("unauthorized")) {
                   res.status(401);
               } else {
                   res.status(500);
               }

               return new Gson().toJson(new ErrorResponse(message));

           }
        });

        //<============================== Logout ==============================>
        Spark.delete("/session", (req, res) -> {
            try {
                String authToken = req.headers("authorization");
                String response = new LogoutHandler().handle(authToken);
                res.status(200);
                return response;
            } catch (Exception e) {
                String message = e.getMessage();
                if (message.contains("unauthorized")) {
                    res.status(401);
                } else {
                    res.status(500);
                }
                return new Gson().toJson(new ErrorResponse(message));

            }
        });

        //<============================== List Games ==============================>
        Spark.get("/game", (req, res) -> {
            try {
                String authToken = req.headers("authorization");
                String response = new ListGamesHandler().handle(authToken);
                res.status(200);
                return response;
            } catch (Exception e) {
                String message = e.getMessage();
                if (message.contains("unauthorized")) {
                    res.status(401);
                } else {
                    res.status(500);
                }

                return new Gson().toJson(new ErrorResponse(message));
            }
        });

        //<============================== Create Game ==============================>
        Spark.post("/game", (req, res) -> {
            try {
                String authToken = req.headers("authorization");
                String response = new CreateGameHandler().handle(authToken, req.body());
                res.status(200);
                return response;
            } catch (Exception e) {
                String message = e.getMessage();
                if (message.contains("bad request")) {
                    res.status(400);
                } else if (message.contains("unauthorized")) {
                    res.status(401);
                } else {
                    res.status(500);
                }

                return new Gson().toJson(new ErrorResponse(message));
            }
        });

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
