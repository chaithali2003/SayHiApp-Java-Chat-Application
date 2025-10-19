package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Contact;

public class ContactDAO {
    // Modified to only store display name (no registered_name)
    public boolean addContactWithDisplayName(int userId, int contactId, String displayName)
            throws SQLException {
        String sql = "INSERT INTO contacts (user_id, contact_id, display_name) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, contactId);
            stmt.setString(3, displayName);

            return stmt.executeUpdate() > 0;
        }
    }

    public List<Contact> getContacts(int userId) throws SQLException {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT u.id, u.name as registered_name, u.phone, u.email, u.bio, c.display_name " +
                "FROM users u JOIN contacts c ON u.id = c.contact_id " +
                "WHERE c.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contacts.add(new Contact(
                            rs.getInt("id"),
                            rs.getString("registered_name"),
                            rs.getString("phone"),
                            rs.getString("display_name"),
                            rs.getString("email"), // Added email
                            rs.getString("bio") // Added bio
                    ));
                }
            }
        }
        return contacts;
    }

    public boolean isContact(int userId, int contactId) throws SQLException {
        String sql = "SELECT 1 FROM contacts WHERE user_id = ? AND contact_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, contactId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}