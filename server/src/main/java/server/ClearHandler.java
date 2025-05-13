package server;

import service.ClearService;
import com.google.gson.Gson;

public class ClearHandler {
    public String handle() throws Exception {
        new ClearService().clear();

        return "{}"; //no need to get fancy
    }
}