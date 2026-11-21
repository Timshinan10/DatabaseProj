import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Default authentication profile settings matching fresh XAMPP local instances
    private static final String DB_USER = "root";
    private static final String DB_PASS = ""; // Intentionally blank for default XAMPP setups
    private static Connection connection = null;

    /**
     * Closes any existing connection and opens a new session channel to the requested schema.
     */
    public static Connection connect(String dbName) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        
        // standard local loopback network addressing string targeting port 3306
        String url = "jdbc:mysql://localhost:3306/" + dbName;
        connection = DriverManager.getConnection(url, DB_USER, DB_PASS);
        return connection;
    }

    /**
     * Safely retrieves the active connection instance for query dispatching.
     */
    public static Connection getConnection() {
        return connection;
    }
}
