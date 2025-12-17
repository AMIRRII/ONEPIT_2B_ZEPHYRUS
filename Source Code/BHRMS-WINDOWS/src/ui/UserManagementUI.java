package bhrms.ui;

import bhrms.dao.UserDAO;
import bhrms.models.User;
import bhrms.DatabaseConnection;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.text.SimpleDateFormat;
import java.sql.*;

public class UserManagementUI extends JDialog {
    
    // Colors
    private final Color PRIMARY_COLOR = new Color(66, 133, 244);
    private final Color PRIMARY_COLOR3 = new Color(118, 157, 250); 
    private final Color PRIMARY_COLOR2 = new Color(230, 230, 230);
    private final Color SECONDARY_COLOR = new Color(249, 250, 251);
    private final Color CARD_COLOR = PRIMARY_COLOR2;
    private final Color TEXT_PRIMARY = new Color(32, 33, 36);
    private final Color TEXT_SECONDARY = new Color(95, 99, 104);
    private final Color SUCCESS_COLOR = new Color(52, 168, 83);
    private final Color WARNING_COLOR = new Color(251, 188, 5);
    private final Color DANGER_COLOR = new Color(219, 68, 55);
    private final Color INFO_COLOR = new Color(66, 133, 244);
    
    // Components
    private JTextField txtSearch, txtUsername, txtFullName, txtEmail, txtPhone;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JComboBox<String> cmbRole, cmbBarangay, cmbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnClose;
    private JTable userTable;
    private DefaultTableModel tableModel;
    
    private User currentUser;
    private User selectedUser = null;
    
    public UserManagementUI(Frame parent, User currentUser) {
        super(parent, "User Management", true);
        this.currentUser = currentUser;
        
        setSize(1200, 700);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        initializeUI();
        loadUsers();
    }
    
    private void initializeUI() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(PRIMARY_COLOR2);
        
        // Header
        JPanel headerPanel = createCard("User Management System", PRIMARY_COLOR2);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(getWidth(), 70));
        
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR3);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Close button in header
        JButton btnHeaderClose = new JButton("X");
        btnHeaderClose.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnHeaderClose.setForeground(PRIMARY_COLOR2);
        btnHeaderClose.setContentAreaFilled(false);
        btnHeaderClose.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnHeaderClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHeaderClose.addActionListener(e -> dispose());
        
        btnHeaderClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnHeaderClose.setForeground(PRIMARY_COLOR);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnHeaderClose.setForeground(Color.BLACK);
            }
        });
        
        headerPanel.add(btnHeaderClose, BorderLayout.EAST);
        
        // Content panel (split left-right)
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        contentPanel.setBackground(PRIMARY_COLOR);
        
        // Left panel - User list
        JPanel leftPanel = createCard("User List", PRIMARY_COLOR3);
        leftPanel.setLayout(new BorderLayout(10, 10));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(PRIMARY_COLOR3);
        
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        searchIcon.setBackground(PRIMARY_COLOR);

        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(new RoundedBorder(8));
        txtSearch.setPreferredSize(new Dimension(200, 35));
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchUsers(txtSearch.getText());
            }
        });
        
        JButton btnRefresh = createMaterialButton("Refresh", PRIMARY_COLOR, 12);
        btnRefresh.addActionListener(e -> loadUsers());
        
        searchPanel.add(searchIcon);
        searchPanel.add(txtSearch);
        searchPanel.add(btnRefresh);
        
        // User table
        String[] columns = {"ID", "Username", "Full Name", "Role", "Barangay", "Status", "Last Login"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userTable.setRowHeight(35);
        userTable.setSelectionBackground(new Color(66, 133, 244, 50));
        userTable.setSelectionForeground(Color.BLACK);
        
        // Style table header
        JTableHeader header = userTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY_COLOR3);
        header.setForeground(PRIMARY_COLOR);
        
        // Selection listener
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int userId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                    loadUserData(userId);
                }
            }
        });
        
        JScrollPane tableScroll = new JScrollPane(userTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(tableScroll, BorderLayout.CENTER);
        
        // Right panel - User form
        JPanel rightPanel = createCard("User Details", CARD_COLOR);
        rightPanel.setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // User Information section
        formPanel.add(createFormSection("User Information", new String[]{
            "Username:*", "Password:", "Confirm Password:", "Full Name:*", "Email:", "Phone:"
        }));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Role and Access section
        JPanel rolePanel = new JPanel(new GridLayout(1, 3, 10, 10));
        rolePanel.setBackground(PRIMARY_COLOR2);
        rolePanel.setBorder(BorderFactory.createTitledBorder("Role & Access"));
        
        JPanel roleField = new JPanel(new BorderLayout(5, 5));
        roleField.setBackground(PRIMARY_COLOR3);
        roleField.add(new JLabel("Role:*"), BorderLayout.NORTH);
        cmbRole = new JComboBox<>(new String[]{"admin", "health_worker", "staff", "viewer"});
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleField.add(cmbRole, BorderLayout.CENTER);
        
        JPanel barangayField = new JPanel(new BorderLayout(5, 5));
        barangayField.setBackground(PRIMARY_COLOR3);
        barangayField.add(new JLabel("Barangay:"), BorderLayout.NORTH);
        cmbBarangay = new JComboBox<>(new String[]{
            "All", "Purok 1", "Purok 2", "Purok 3", "Purok 4", "Purok 5",
            "Purok 6", "Purok 7", "Purok 8", "Purok 9", "Purok 10"
        });
        cmbBarangay.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        barangayField.add(cmbBarangay, BorderLayout.CENTER);
        
        JPanel statusField = new JPanel(new BorderLayout(5, 5));
        statusField.setBackground(PRIMARY_COLOR3);
        statusField.add(new JLabel("Status:"), BorderLayout.NORTH);
        cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusField.add(cmbStatus, BorderLayout.CENTER);
        
        rolePanel.add(roleField);
        rolePanel.add(barangayField);
        rolePanel.add(statusField);
        
        formPanel.add(rolePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBackground(CARD_COLOR);
        
        btnAdd = createMaterialButton("Add User", Color.BLACK, 12);
        btnUpdate = createMaterialButton("Update User", Color.BLACK, 12);
        btnDelete = createMaterialButton("Deactivate", Color.BLACK, 12);
        btnClear = createMaterialButton("Clear Form", Color.BLACK, 12);
        
        btnAdd.addActionListener(e -> addUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnClear.addActionListener(e -> clearForm());
        
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        
        formPanel.add(buttonPanel);
        
        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        rightPanel.add(formScroll, BorderLayout.CENTER);
        
        // Add panels to content
        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        
        // Footer with close button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(PRIMARY_COLOR3);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnClose = createMaterialButton("Close", DANGER_COLOR, 14);
        btnClose.addActionListener(e -> dispose());
        
        footerPanel.add(btnClose);
        
        // Add all to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createCard(String title, Color titleColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(new RoundedBorder(12));
        
        // Card header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(titleColor);
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        header.add(titleLabel, BorderLayout.WEST);
        card.add(header, BorderLayout.NORTH);
        
        return card;
    }
    
    private JButton createMaterialButton(String text, Color color, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new RoundedBorder(8));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private JPanel createFormSection(String title, String[] fields) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(PRIMARY_COLOR2);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(title),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        for (String fieldLabel : fields) {
            JPanel fieldPanel = new JPanel(new BorderLayout(10, 5));
            fieldPanel.setBackground(CARD_COLOR);
            fieldPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            
            JLabel label = new JLabel(fieldLabel);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setForeground(TEXT_SECONDARY);
            
            JComponent inputField;
            if (fieldLabel.contains("Password")) {
                JPasswordField passwordField = new JPasswordField();
                passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                passwordField.setBorder(new RoundedBorder(8));
                
                if (fieldLabel.equals("Password:")) {
                    txtPassword = passwordField;
                } else {
                    txtConfirmPassword = passwordField;
                }
                inputField = passwordField;
            } else {
                JTextField textField = new JTextField();
                textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                textField.setBorder(new RoundedBorder(8));
                
                switch (fieldLabel) {
                    case "Username:*": txtUsername = textField; break;
                    case "Full Name:*": txtFullName = textField; break;
                    case "Email:": txtEmail = textField; break;
                    case "Phone:": txtPhone = textField; break;
                }
                inputField = textField;
            }
            
            fieldPanel.add(label, BorderLayout.WEST);
            fieldPanel.add(inputField, BorderLayout.CENTER);
            section.add(fieldPanel);
        }
        
        return section;
    }
    
    // Custom Rounded Border Class
    class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color color;
        
        public RoundedBorder(int radius) {
            this(radius, new Color(218, 220, 224));
        }
        
        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 1, this.radius + 1);
        }
    }
    
    private void loadUsers() {
        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.getAllUsers();
        updateUserTable(users);
    }
    
    private void searchUsers(String searchTerm) {
        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.searchUsers(searchTerm);
        updateUserTable(users);
    }
    
    private void updateUserTable(List<User> users) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (User user : users) {
            Object[] row = {
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole(),
                user.getBarangay() != null ? user.getBarangay() : "All",
                user.isActive() ? "🟢 Active" : "🔴 Inactive",
                user.getLastLogin() != null ? sdf.format(user.getLastLogin()) : "Never"
            };
            tableModel.addRow(row);
        }
    }
    
    private User getUserById(int userId) {
        UserDAO userDAO = new UserDAO();
        return userDAO.getUserById(userId);
    }
    
    private void loadUserData(int userId) {
        User user = getUserById(userId);
        
        if (user != null) {
            selectedUser = user;
            
            txtUsername.setText(user.getUsername());
            txtFullName.setText(user.getFullName());
            txtEmail.setText(user.getEmail() != null ? user.getEmail() : "");
            txtPhone.setText(user.getPhone() != null ? user.getPhone() : "");
            
            if (cmbRole != null) cmbRole.setSelectedItem(user.getRole());
            if (cmbBarangay != null) {
                String barangay = user.getBarangay() != null ? user.getBarangay() : "All";
                cmbBarangay.setSelectedItem(barangay);
            }
            if (cmbStatus != null) {
                cmbStatus.setSelectedItem(user.isActive() ? "Active" : "Inactive");
            }
            
            // Clear password fields for security
            if (txtPassword != null) txtPassword.setText("");
            if (txtConfirmPassword != null) txtConfirmPassword.setText("");
            
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        }
    }
    
    private void addUser() {
        if (!validateUserForm(true)) {
            return;
        }
        
        try {
            User user = new User();
            user.setUsername(txtUsername.getText().trim());
            user.setFullName(txtFullName.getText().trim());
            user.setEmail(txtEmail.getText().trim());
            user.setPhone(txtPhone.getText().trim());
            user.setRole(cmbRole.getSelectedItem().toString());
            user.setBarangay(cmbBarangay.getSelectedItem().equals("All") ? 
                null : cmbBarangay.getSelectedItem().toString());
            user.setActive(cmbStatus.getSelectedItem().equals("Active"));
            
            // Set password
            String password = new String(txtPassword.getPassword());
            user.setPassword(password);
            
            UserDAO userDAO = new UserDAO();
            if (userDAO.addUser(user)) {
                JOptionPane.showMessageDialog(this, "User added successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add user!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateUser() {
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to update!", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateUserForm(false)) {
            return;
        }
        
        try {
            selectedUser.setUsername(txtUsername.getText().trim());
            selectedUser.setFullName(txtFullName.getText().trim());
            selectedUser.setEmail(txtEmail.getText().trim());
            selectedUser.setPhone(txtPhone.getText().trim());
            selectedUser.setRole(cmbRole.getSelectedItem().toString());
            selectedUser.setBarangay(cmbBarangay.getSelectedItem().equals("All") ? 
                null : cmbBarangay.getSelectedItem().toString());
            selectedUser.setActive(cmbStatus.getSelectedItem().equals("Active"));
            
            // Update password only if provided
            String password = new String(txtPassword.getPassword());
            if (!password.trim().isEmpty()) {
                selectedUser.setPassword(password);
            }
            
            UserDAO userDAO = new UserDAO();
            if (userDAO.updateUser(selectedUser)) {
                JOptionPane.showMessageDialog(this, "User updated successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update user!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteUser() {
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user!", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Don't allow deleting yourself
        if (selectedUser.getUserId() == currentUser.getUserId()) {
            JOptionPane.showMessageDialog(this, "You cannot deactivate your own account!", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Don't allow deleting admin (ID 1)
        if (selectedUser.getUserId() == 1) {
            JOptionPane.showMessageDialog(this, "Cannot deactivate the primary administrator!", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to deactivate user:\n" +
            selectedUser.getFullName() + " (" + selectedUser.getUsername() + ")?\n\n" +
            "The user will no longer be able to login.",
            "Confirm Deactivation",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            UserDAO userDAO = new UserDAO();
            if (userDAO.deleteUser(selectedUser.getUserId())) {
                JOptionPane.showMessageDialog(this, "User deactivated successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to deactivate user!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateUserForm(boolean isNewUser) {
        // Validate required fields
        if (txtUsername.getText().trim().isEmpty() ||
            txtFullName.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "Please fill in all required fields (marked with *)!", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Validate username uniqueness for new users
        if (isNewUser) {
            UserDAO userDAO = new UserDAO();
            if (userDAO.checkUsernameExists(txtUsername.getText().trim(), 0)) {
                JOptionPane.showMessageDialog(this, 
                    "Username already exists! Please choose a different username.", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        
        // For new users or password change, validate passwords
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        
        if (isNewUser && password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Password is required for new users!", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (!password.trim().isEmpty()) {
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this, 
                    "Password must be at least 6 characters!", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, 
                    "Passwords do not match!", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        
        // Validate email format if provided
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid email address!", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        if (txtUsername != null) txtUsername.setText("");
        if (txtPassword != null) txtPassword.setText("");
        if (txtConfirmPassword != null) txtConfirmPassword.setText("");
        if (txtFullName != null) txtFullName.setText("");
        if (txtEmail != null) txtEmail.setText("");
        if (txtPhone != null) txtPhone.setText("");
        
        if (cmbRole != null) cmbRole.setSelectedIndex(0);
        if (cmbBarangay != null) cmbBarangay.setSelectedIndex(0);
        if (cmbStatus != null) cmbStatus.setSelectedIndex(0);
        
        if (userTable != null) userTable.clearSelection();
        
        selectedUser = null;
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
}