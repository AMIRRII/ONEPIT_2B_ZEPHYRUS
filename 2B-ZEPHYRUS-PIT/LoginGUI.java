import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginGUI extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginGUI() {
        setTitle("BHRMS - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null); // Center the window

        // --- 1. Login Form Panel (CENTER) ---
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        
        // Default credentials for testing
        usernameField.setText("admin"); 
        passwordField.setText("password"); 

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        
        // --- 2. Button Panel (SOUTH) ---
        loginButton = new JButton("Login");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);

        // --- 3. Logo/Title (NORTH) ---
        JLabel titleLabel = new JLabel("Barangay Health Record System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        // --- Final Assembly ---
        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Event Handling ---
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
        
        // Allow pressing Enter key to log in
        passwordField.addActionListener(e -> attemptLogin());

        setVisible(true);
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        // --- Hardcoded Authentication Logic ---
        // In a real system, this would query a 'users' table in the database.
        
        // Example Credentials:
        if (username.equals("admin") && password.equals("password")) {
            // Successful Login (HTA Step 1.2, 1.3)
            JOptionPane.showMessageDialog(this, "Login Successful! Welcome, Administrator.", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // 1. Close the Login Window
            this.dispose(); 
            
            // 2. Open the Main Dashboard
            new MainDashboardGUI("Administrator"); 
            
            // 3. Attempt to establish the DB connection for the session
            try {
                DBConnection.getConnection();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database Connection Failed: " + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } else if (username.equals("healthworker") && password.equals("pass")) {
            // Another User Role (e.g., restricted access)
            JOptionPane.showMessageDialog(this, "Login Successful! Welcome, Health Worker.", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            new MainDashboardGUI("Health Worker");
            try {
                DBConnection.getConnection();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database Connection Failed: " + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } else {
            // Failed Login
            JOptionPane.showMessageDialog(this, "Invalid Username or Password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            usernameField.requestFocus();
        }
    }
    
    // ==========================================================
    // MAIN METHOD: THE APPLICATION STARTING POINT
    // ==========================================================
    public static void main(String[] args) {
        // Ensure GUI runs on the Event Dispatch Thread (standard Swing practice)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginGUI(); // Start the application by displaying the login screen
            }
        });
    }
}