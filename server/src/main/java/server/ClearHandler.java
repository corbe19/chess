package server;

import service.ClearService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

public class ClearHandler {
    public String handle(Request req, Response res) {
        try {
            new ClearService().clear();
            res.status(200);
            return "{}";
        } catch (Exception e) {
            return ExceptionUtil.handleException(e, res);
        }
    }
}