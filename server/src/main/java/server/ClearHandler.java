package server;

import service.ClearService;
import com.google.gson.Gson;

public class ClearHandler {
    public String handle() throws Exception {
        new ClearService().clear();

        return new Gson().toJson(new Object() {
            public final String message = "Success";
        });
    }
}