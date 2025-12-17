package bhrms;

import bhrms.ui.LoginUI;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Test database connection
        if (DatabaseConnection.getConnection() != null) {
            System.out.println("✅ Database connected successfully!");
            
            SwingUtilities.invokeLater(() -> {
                new LoginUI().setVisible(true);
            });
        } else {
            System.out.println("❌ Failed to connect to database!");
            JOptionPane.showMessageDialog(null, 
                "⚠️ Cannot connect to database!\n\n" +
                "Please ensure:\n" +
                "1. XAMPP is running\n" +
                "2. MySQL service is started\n" +
                "3. Database 'bhrms_db' exists\n\n" +
                "Error details logged to console.",
                "Database Connection Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}