package bhrms.ui;

import bhrms.dao.UserDAO;
import bhrms.models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginUI extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private final Color PRIMARY_COLOR = new Color(66, 133, 244);
    private final Color PRIMARY_COLOR2 = new Color(118, 157, 250);
    private final Color PRIMARY_DARK = new Color(51, 103, 214);
    private final Color CARD_COLOR = PRIMARY_COLOR2;
    private final Color TEXT_PRIMARY = new Color(32, 33, 36);
    private final Color TEXT_SECONDARY = new Color(32, 33, 36);
    private final Color INPUT_BG = new Color(248, 249, 250);
    private final Color INPUT_BORDER = new Color(218, 220, 224);
    private final Color BUTTON_BG = new Color(32, 33, 36); // BLACK button background
    private final Color BUTTON_HOVER = new Color(66, 66, 66); // Dark gray hover
    
    public LoginUI() {
        setTitle("BHRMS - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 500, 600, 30, 30));
        
        initializeUI();
    }
    
    private void initializeUI() {
        // Main panel with solid color background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Exit button at top right (outside card)
        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        exitPanel.setBackground(Color.black);
        exitPanel.setOpaque(false);
        
        JButton btnExit = createExitButton();
        btnExit.addActionListener(e -> System.exit(0));
        
        exitPanel.add(btnExit);
        
        // Center card - clean minimalist design
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(new RoundedBorder(20, 1, new Color(230, 230, 230)));
        card.setPreferredSize(new Dimension(400, 500));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));
        
        JLabel logoLabel = new JLabel("🏥", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        logoLabel.setForeground(TEXT_PRIMARY); // Dark logo
        
        JLabel titleLabel = new JLabel("Welcome Back", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JLabel subtitleLabel = new JLabel("Sign in to your account", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        
        JPanel titleContainer = new JPanel(new BorderLayout(5, 5));
        titleContainer.setBackground(CARD_COLOR);
        titleContainer.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        titleContainer.add(titleLabel, BorderLayout.NORTH);
        titleContainer.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(logoLabel, BorderLayout.NORTH);
        headerPanel.add(titleContainer, BorderLayout.SOUTH);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));
        
        // Username field
        JPanel usernamePanel = new JPanel(new BorderLayout(10, 5));
        usernamePanel.setBackground(CARD_COLOR);
        usernamePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        usernameLabel.setForeground(TEXT_SECONDARY);
        
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUsername.setBorder(new RoundedBorder(10, INPUT_BORDER));
        txtUsername.setBackground(INPUT_BG);
        txtUsername.setPreferredSize(new Dimension(300, 50));
        
        usernamePanel.add(usernameLabel, BorderLayout.NORTH);
        usernamePanel.add(txtUsername, BorderLayout.CENTER);
        
        // Password field
        JPanel passwordPanel = new JPanel(new BorderLayout(10, 5));
        passwordPanel.setBackground(CARD_COLOR);
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordLabel.setForeground(TEXT_SECONDARY);
        
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPassword.setBorder(new RoundedBorder(10, INPUT_BORDER));
        txtPassword.setBackground(INPUT_BG);
        txtPassword.setPreferredSize(new Dimension(300, 50));
        
        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        passwordPanel.add(txtPassword, BorderLayout.CENTER);
        
        // BLACK Sign In button
        JButton btnLogin = new JButton("Sign In");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setBackground(BUTTON_BG); // BLACK background
        btnLogin.setForeground(Color.darkGray); // White text for contrast
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(400, 40));
        btnLogin.addActionListener(e -> login());
        
        // Hover effect for black button
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(BUTTON_HOVER); // Dark gray on hover
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(BUTTON_BG); // Back to black
            }
        });
        
        // Help text
        JPanel helpPanel = new JPanel(new BorderLayout());
        helpPanel.setBackground(CARD_COLOR);
        helpPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JLabel lblHelp = new JLabel("<html><center>Need help? Contact <b>admin@bhrms.local</b></center></html>", 
            SwingConstants.CENTER);
        lblHelp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHelp.setForeground(TEXT_SECONDARY);
        
        helpPanel.add(lblHelp, BorderLayout.CENTER);
        
        // Add all components to form
        formPanel.add(usernamePanel);
        formPanel.add(passwordPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(btnLogin);
        formPanel.add(helpPanel);
        
        // Add all to card
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);
        
        // Main layout
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(exitPanel, BorderLayout.NORTH);
        mainPanel.add(card, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JButton createExitButton() {
        JButton btn = new JButton("✕");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(new Color(255, 255, 255, 200)); // Semi-transparent white
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
        });
        
        return btn;
    }
    
    // Rounded Border class
    class RoundedBorder extends javax.swing.border.AbstractBorder {
        private int radius;
        private Color color;
        private int thickness;
        
        RoundedBorder(int radius) {
            this(radius, 1, new Color(218, 220, 224));
        }
        
        RoundedBorder(int radius, Color color) {
            this(radius, 1, color);
        }
        
        RoundedBorder(int radius, int thickness, Color color) {
            this.radius = radius;
            this.thickness = thickness;
            this.color = color;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x + thickness/2, y + thickness/2, width - thickness, height - thickness, radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius/2 + thickness, this.radius/2 + thickness, 
                             this.radius/2 + thickness, this.radius/2 + thickness);
        }
    }
    
    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password!");
            return;
        }
        
        txtUsername.setEnabled(false);
        txtPassword.setEnabled(false);
        
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                UserDAO userDAO = new UserDAO();
                return userDAO.authenticate(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        SwingUtilities.invokeLater(() -> {
                            BHRMSUI mainApp = new BHRMSUI(user);
                            mainApp.setVisible(true);
                            dispose();
                        });
                    } else {
                        showError("Invalid username or password!");
                        txtUsername.setEnabled(true);
                        txtPassword.setEnabled(true);
                        txtPassword.setText("");
                    }
                } catch (Exception e) {
                    showError("Login error: " + e.getMessage());
                    txtUsername.setEnabled(true);
                    txtPassword.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginUI loginUI = new LoginUI();
            loginUI.setVisible(true);
        });
    }
}