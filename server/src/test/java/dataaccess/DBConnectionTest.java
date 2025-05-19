package dataaccess;

public class DBConnectionTest {
    public static void main(String[] args) {
        try (var conn = DatabaseManager.getConnection()) {
            System.out.println("Connection Successful");
        } catch (Exception e) {
            System.out.println("Connection Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

