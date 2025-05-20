package server;

import com.google.gson.Gson;
import spark.Response;

//I cant tell you why im making a while new class but this exception stuff is really annoying me
public class ExceptionUtil {
    private static final Gson GSON = new Gson();

    public static String handleException(Exception e, Response res) {
        String rawMessage = e.getMessage();

        String message = (rawMessage == null || rawMessage.isBlank())
                ? "Error: internal server error"
                : rawMessage.toLowerCase().contains("error") ? rawMessage : "Error: " + rawMessage;

        if (message.toLowerCase().contains("bad request")) {
            res.status(400);
        } else if (message.toLowerCase().contains("unauthorized")) {
            res.status(401);
        } else if (message.toLowerCase().contains("already taken")) {
            res.status(403);
        } else {
            res.status(500);
        }

        return new Gson().toJson(new ErrorResponse(message));
    }

}