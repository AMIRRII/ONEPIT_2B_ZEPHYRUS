package bhrms.dao;

import bhrms.DatabaseConnection;
import bhrms.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND is_active = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getString("role"));
                user.setBarangay(rs.getString("barangay"));
                user.setActive(rs.getBoolean("is_active"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                
                // Update last login timestamp
                updateLastLogin(user.getUserId());
                
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public User getUserById(int userId) {
    String sql = "SELECT * FROM users WHERE user_id = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, userId);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            User user = new User();
            user.setUserId(rs.getInt("user_id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setFullName(rs.getString("full_name"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));
            user.setRole(rs.getString("role"));
            user.setBarangay(rs.getString("barangay"));
            user.setActive(rs.getBoolean("is_active"));
            user.setCreatedAt(rs.getTimestamp("created_at"));
            user.setLastLogin(rs.getTimestamp("last_login"));
            return user;
        }
    } catch (SQLException e) {
        System.err.println("Error getting user by ID: " + e.getMessage());
        e.printStackTrace();
    }
    return null;
}

    private void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, full_name, email, phone, role, barangay) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getRole());
            pstmt.setString(7, user.getBarangay());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ?, role = ?, barangay = ?, is_active = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getRole());
            pstmt.setString(5, user.getBarangay());
            pstmt.setBoolean(6, user.isActive());
            pstmt.setInt(7, user.getUserId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean changePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public int getActiveStaffCount() {
        String sql = "SELECT COUNT(*) as total FROM users WHERE is_active = TRUE AND role IN ('health_worker', 'admin')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting active staff count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Add these methods to UserDAO.java:

public List<User> getAllUsers() {
    List<User> users = new ArrayList<>();
    String sql = "SELECT * FROM users ORDER BY full_name";
    
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            users.add(mapResultSetToUser(rs));
        }
    } catch (SQLException e) {
        System.err.println("Error getting all users: " + e.getMessage());
        e.printStackTrace();
    }
    return users;
}

public List<User> searchUsers(String searchTerm) {
    List<User> users = new ArrayList<>();
    String sql = "SELECT * FROM users WHERE username LIKE ? OR full_name LIKE ? OR email LIKE ? OR role LIKE ? ORDER BY full_name";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        String searchPattern = "%" + searchTerm + "%";
        pstmt.setString(1, searchPattern);
        pstmt.setString(2, searchPattern);
        pstmt.setString(3, searchPattern);
        pstmt.setString(4, searchPattern);
        
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            users.add(mapResultSetToUser(rs));
        }
    } catch (SQLException e) {
        System.err.println("Error searching users: " + e.getMessage());
        e.printStackTrace();
    }
    return users;
}

public boolean permanentlyDeleteUser(int userId, int currentUserId) {
    // Don't delete the current user or admin
    if (userId == 1 || userId == currentUserId) {
        System.err.println("Cannot delete primary administrator or yourself!");
        return false;
    }
    
    String sql = "DELETE FROM users WHERE user_id = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, userId);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error deleting user: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

// Also fix the deleteUser method to not use currentUser:
public boolean deleteUser(int userId) {
    // Don't delete the primary admin (ID 1)
    if (userId == 1) {
        System.err.println("Cannot delete primary administrator!");
        return false;
    }
    
    String sql = "UPDATE users SET is_active = FALSE WHERE user_id = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, userId);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error deactivating user: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

public boolean checkUsernameExists(String username, int excludeUserId) {
    String sql = "SELECT COUNT(*) as count FROM users WHERE username = ? AND user_id != ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, username);
        pstmt.setInt(2, excludeUserId);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            return rs.getInt("count") > 0;
        }
    } catch (SQLException e) {
        System.err.println("Error checking username: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
}

// Helper method to map ResultSet to User
private User mapResultSetToUser(ResultSet rs) throws SQLException {
    User user = new User();
    user.setUserId(rs.getInt("user_id"));
    user.setUsername(rs.getString("username"));
    user.setPassword(rs.getString("password"));
    user.setFullName(rs.getString("full_name"));
    user.setEmail(rs.getString("email"));
    user.setPhone(rs.getString("phone"));
    user.setRole(rs.getString("role"));
    user.setBarangay(rs.getString("barangay"));
    user.setActive(rs.getBoolean("is_active"));
    user.setCreatedAt(rs.getTimestamp("created_at"));
    user.setLastLogin(rs.getTimestamp("last_login"));
    return user;
}

}

