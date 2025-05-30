package client;

import com.google.gson.Gson;
import model.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    //make request
    public <T> T makeRequest(String path, String method, Object requestObj, Class<T> responseType,String authToken) throws IOException {
        URL url = new URL(serverUrl + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept", "application/json");

        if (authToken != null) {
            connection.setRequestProperty("Authorization", authToken);
        }

        if (requestObj != null) {
            connection.setRequestProperty("Content-Type", "application/json");
            String jsonRequest = gson.toJson(requestObj);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }
        }

        int responseCode = connection.getResponseCode();
        InputStream responseStream;
        if (responseCode >= 200 && responseCode < 300) {
            responseStream = connection.getInputStream();
        } else {
            responseStream = connection.getErrorStream();
            //catch non-2xx status
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream))) {
                StringBuilder errorMsg = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    errorMsg.append(line).append("\n");
                }
                throw new IOException("Error: status " + responseCode + ": " + errorMsg.toString().trim());
            }
        }

        try (InputStreamReader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
            if (responseType == Void.class || reader == null) {
                return null;
            }
            return gson.fromJson(reader, responseType);
        }
    }

    //register
    public AuthData register(String username, String password, String email) throws IOException {
        var request = new RegisterRequest(username, password, email);
        return makeRequest("/user", "POST", request, AuthData.class, null);
    }

    //login

    public AuthData login(String username, String password) throws IOException {
        var request = new LoginRequest(username, password);
        return makeRequest("/session", "POST", request, AuthData.class, null);
    }

    //logout
    public void logout(AuthData auth) throws IOException {
        makeRequest("/session", "DELETE", null, Void.class, auth.authToken());
    }

    //createGame
    public void createGame(AuthData auth, String gameName) throws IOException {
        var request = new CreateGameRequest(gameName);
        makeRequest("/game", "POST", request, CreateGameResult.class, auth.authToken());
    }

    //listGames
    public ListGamesResult listGames(AuthData auth) throws IOException {
        return makeRequest("/game", "GET", null, ListGamesResult.class, auth.authToken());
    }

    //joinGame
    public void joinGame(AuthData auth, int gameID, String color) throws IOException {
        String joinColor = (color == null) ? "null" : color; //observer
        JoinGameRequest request = new JoinGameRequest(joinColor, gameID);
        makeRequest("/game", "PUT", request, Void.class, auth.authToken());
    }
}
