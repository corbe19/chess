package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDAO {

    //insert auth
    public void insertAuth(AuthData auth) throws DataAccessException {
        var sql = "insert into auth (authToken, username) values (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, auth.authToken());
            stmt.setString(2, auth.username());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error: failed to insert auth", e);
        }
    }

    //get auth
    public AuthData getAuth(String authToken) throws DataAccessException {
        var sql = "SELECT authToken, username FROM auth WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new AuthData(rs.getString("authToken"), rs.getString("username"));
            }
            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to get auth", e);
        }
    }

    //delete auth


    //clear auths
    public void clear() throws DataAccessException {
        var sql = "delete from auth";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to clear auths", e);

        }
    }
}
