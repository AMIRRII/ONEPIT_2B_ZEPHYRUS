package bhrms;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    
    public static Connection getConnection() {
        try {
            // Load MariaDB driver
            Class.forName("org.mariadb.jdbc.Driver");
            
            // Connection parameters - root with empty password
            String url = "jdbc:mariadb://localhost:3306/bhrms_db";
            String user = "root";
            String password = "";  // Empty password
            
            System.out.println("Connecting to database...");
            System.out.println("URL: " + url);
            System.out.println("User: " + user);
            
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Database connection successful!");
            return conn;
            
        } catch (Exception e) {
            System.err.println("❌ Database connection failed!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // Test method
    public static void main(String[] args) {
        System.out.println("=== Testing Database Connection ===");
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("\n🎉 SUCCESS: Database is ready for BHRMS!");
            try {
                // Test a simple query
                var stmt = conn.createStatement();
                var rs = stmt.executeQuery("SELECT 'Database test passed' as result");
                if (rs.next()) {
                    System.out.println("Query test: " + rs.getString(1));
                }
                conn.close();
                System.out.println("Connection closed properly.");
            } catch (Exception e) {
                System.err.println("Error during test query: " + e.getMessage());
            }
        } else {
            System.out.println("\n❌ FAILED: Could not connect to database");
        }
    }
}
