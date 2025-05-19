package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    //insert user
    public void insertUser(UserData user) throws DataAccessException {
        var sql = "insert into users (username, password, email) values (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

            stmt.setString(1, user.username());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.email());
            stmt.executeUpdate();


        } catch (SQLException e) {
            System.err.println("SQL: " + e.getMessage());
            throw new DataAccessException("Error: Failed to insert user", e);
        }
    }

    //get user
    public UserData getUser(String username) throws DataAccessException {
        var sql = "select username, password, email from users where username = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to get user", e);
        }
    }

    //verify password
    public boolean verifyPassword(String username, String password) throws DataAccessException {
        UserData user = getUser(username);

        if (user == null) {
            return false;
        } else {
            return BCrypt.checkpw(password, user.password());
        }
    }


    //clear users
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("delete from users")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to clear users", e);
        }
    }
}
