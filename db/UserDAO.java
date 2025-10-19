package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.User;

public class UserDAO {
    public boolean registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, phone, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getPassword());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean isEmailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public boolean isPhoneExists(String phone) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE phone = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    public User login(String emailOrPhone, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE (email = ? OR phone = ?) AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, emailOrPhone);
            stmt.setString(2, emailOrPhone);
            stmt.setString(3, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getString("profile_pic"),
                        rs.getString("bio")
                    );
                }
            }
        }
        return null;
    }
    
    public boolean updateProfile(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, bio = ?, profile_pic = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getBio());
            stmt.setString(3, user.getProfilePic());
            stmt.setInt(4, user.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean setOTP(String email, String otp, Timestamp expiry) throws SQLException {
        String sql = "UPDATE users SET otp_code = ?, otp_expires = ? WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, otp);
            stmt.setTimestamp(2, expiry);
            stmt.setString(3, email);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean verifyOTP(String email, String otp) throws SQLException {
        String sql = "SELECT otp_expires FROM users WHERE email = ? AND otp_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, otp);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp expiry = rs.getTimestamp("otp_expires");
                    return expiry.after(new Timestamp(System.currentTimeMillis()));
                }
            }
        }
        return false;
    }
    
    public boolean updatePassword(String email, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ?, otp_code = NULL, otp_expires = NULL WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPassword);
            stmt.setString(2, email);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public User getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        null, // Don't return password
                        rs.getString("profile_pic"),
                        rs.getString("bio")
                    );
                }
            }
        }
        return null;
    }
    
    public List<User> searchUsers(String query, int excludeUserId) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE (name LIKE ? OR email LIKE ? OR phone LIKE ?) AND id != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchTerm = "%" + query + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);
            stmt.setInt(4, excludeUserId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        null,
                        rs.getString("profile_pic"),
                        rs.getString("bio")
                    ));
                }
            }
        }
        return users;
    }

    public User findUserByPhone(String phone) throws SQLException {
        String sql = "SELECT * FROM users WHERE phone = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getString("profile_pic"),
                        rs.getString("bio")
                    );
                }
            }
        }
        return null;
    }
}