package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {
    private final Gson gson = new Gson();

    //insert game
    public int insertGame(GameData game) throws DataAccessException {
        var sql = "insert into games (whiteUsername, blackUsername, gameName, game) values (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            //sql handles id with auto increment
            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());
            stmt.setString(4, gson.toJson(game.game()));

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DataAccessException("Failed to insert game.");
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                else throw new DataAccessException("Failed to retrieve inserted gameID.");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to insert game", e);
        }
    }

    //get game
    public GameData getGame(int gameID) throws DataAccessException {
        var sql = "select * from games where gameID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var white = rs.getString("whiteUsername");
                    var black = rs.getString("blackUsername");
                    var name = rs.getString("gameName");
                    var gameJson = rs.getString("game");
                    ChessGame game = gson.fromJson(gameJson, ChessGame.class);
                    return new GameData(gameID, white, black, name, game);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to get game", e);
        }
    }

    //list all games
    public List<GameData> listAllGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();

        var sql = "select * from games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                games.add(new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        game
                ));
            }
            return games;

        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to list all games");
        }
    }

    //update game
    public void updateGame(GameData game) throws DataAccessException {
        String sql = "update games set whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? where gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());
            stmt.setString(4, gson.toJson(game.game()));
            stmt.setInt(5, game.gameID());

            int affected = stmt.executeUpdate();
            if (affected == 0) throw new DataAccessException("Error: game not found");

        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to update game", e);
        }
    }

    //delete game
    public void deleteGame(int gameID) throws DataAccessException {
        String sql = "delete from games where gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);
            int affected = stmt.executeUpdate();
            if (affected == 0) throw new DataAccessException("Error: game not found");

        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to delete game", e);
        }
    }

    //clear
    public void clear() throws DataAccessException {
        var sql = "delete from games";
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to clear games", e);
        }
    }
}
