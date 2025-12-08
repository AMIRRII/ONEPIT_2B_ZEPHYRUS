import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing the database connection (Singleton Pattern).
 * This provides a centralized and reusable way for all CRUD classes 
 * (Patient, Consultation, ReportGenerator) to connect to MySQL.
 */
public class DBConnection {
    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/bhrms_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // <--- CHANGE IF YOUR PASSWORD IS DIFFERENT

    // Static connection object to hold the single instance
    private static Connection connection = null;

    /**
     * Private constructor to prevent external instantiation (Singleton).
     */
    private DBConnection() {
        // Empty constructor
    }

    /**
     * Establishes and returns a database connection.
     * @return The active Connection object.
     */
    public static Connection getConnection() throws SQLException {
        // If connection is null or closed, attempt to create a new one
        if (connection == null || connection.isClosed()) {
            try {
                // 1. Load the JDBC Driver (optional for modern Java versions, but good practice)
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // 2. Establish the connection
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connection successful.");
                
            } catch (ClassNotFoundException e) {
                // This means the MySQL JDBC driver JAR file is missing from your project path!
                System.err.println("JDBC Driver not found. Make sure the MySQL Connector JAR is in your library path.");
                throw new SQLException("JDBC Driver not found: " + e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Closes the database connection. (Usually called when the application shuts down)
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}