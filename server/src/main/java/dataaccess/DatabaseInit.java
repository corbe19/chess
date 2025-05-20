package dataaccess;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInit {
    public static void initialize() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            //<=============================== Users Table ===============================>
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) PRIMARY KEY,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255)
            );
        """);

            //<=============================== Auth Table ===============================>
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(255) PRIMARY KEY,
                username VARCHAR(255) NOT NULL,
                FOREIGN KEY (username) REFERENCES users(username)
            );
        """);

            //<=============================== Games Table ===============================>
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS games (
                gameID INT PRIMARY KEY AUTO_INCREMENT,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255),
                game TEXT,
                FOREIGN KEY (whiteUsername) REFERENCES users(username),
                FOREIGN KEY (blackUsername) REFERENCES users(username)
            );
        """);

        } catch (SQLException e) {
            throw new DataAccessException("Failed to configure tables", e);
        }

    }
}
