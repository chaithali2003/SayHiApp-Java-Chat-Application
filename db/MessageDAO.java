package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Message;

public class MessageDAO {
    public int saveMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages (sender_id, receiver_id, message, timestamp, delivered) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Set parameters
            stmt.setInt(1, message.getSenderId());
            stmt.setInt(2, message.getReceiverId());
            stmt.setString(3, message.getMessage());
            stmt.setTimestamp(4, message.getTimestamp());
            stmt.setBoolean(5, true); // Default to not delivered

            stmt.executeUpdate();

            // Get the generated ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new SQLException("Creating message failed, no ID obtained.");
        }
    }

    public List<Message> getMessagesBetweenUsers(int user1, int user2) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT id, sender_id, receiver_id, message, seen_status, delivered, timestamp " +
                "FROM messages WHERE (sender_id = ? AND receiver_id = ?) OR " +
                "(sender_id = ? AND receiver_id = ?) ORDER BY timestamp";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user1);
            stmt.setInt(2, user2);
            stmt.setInt(3, user2);
            stmt.setInt(4, user1);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(new Message(
                            rs.getInt("id"),
                            rs.getInt("sender_id"),
                            rs.getInt("receiver_id"),
                            rs.getString("message"),
                            rs.getBoolean("seen_status"),
                            rs.getBoolean("delivered"),
                            rs.getTimestamp("timestamp")));
                }
            }
        }
        return messages;
    }

    public boolean markMessagesAsSeen(int senderId, int receiverId) throws SQLException {
        String sql = "UPDATE messages SET seen_status = TRUE WHERE sender_id = ? AND receiver_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateDeliveryStatus(int messageId, boolean delivered) throws SQLException {
        String sql = "UPDATE messages SET delivered = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, delivered);
            stmt.setInt(2, messageId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Message> getNewMessages(int receiverId, int senderId, Timestamp lastChecked) throws SQLException {
        if (lastChecked == null) {
            lastChecked = new Timestamp(0);
        }

        String sql = "SELECT * FROM messages WHERE receiver_id = ? AND sender_id = ? AND timestamp > ? ORDER BY timestamp";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, receiverId);
            stmt.setInt(2, senderId);
            stmt.setTimestamp(3, lastChecked);

            List<Message> messages = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(new Message(
                            rs.getInt("id"),
                            rs.getInt("sender_id"),
                            rs.getInt("receiver_id"),
                            rs.getString("message"),
                            rs.getBoolean("seen_status"),
                            rs.getBoolean("delivered"),
                            rs.getTimestamp("timestamp")));
                }
            }
            return messages;
        }
    }

    public List<Message> getMessagesNeedingRetry(int senderId) throws SQLException {
        String sql = "SELECT * FROM messages WHERE sender_id = ? AND delivered = FALSE";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, senderId);
            List<Message> messages = new ArrayList<>();

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(new Message(
                            rs.getInt("id"),
                            rs.getInt("sender_id"),
                            rs.getInt("receiver_id"),
                            rs.getString("message"),
                            rs.getBoolean("seen_status"),
                            rs.getBoolean("delivered"),
                            rs.getTimestamp("timestamp")));
                }
            }
            return messages;
        }
    }

    public boolean markForRetry(int messageId) throws SQLException {
        String sql = "UPDATE messages SET delivered = FALSE WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean getMessageDeliveryStatus(int messageId) throws SQLException {
        String sql = "SELECT delivered FROM messages WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, messageId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("delivered");
                }
            }
        }
        return false; // Default to false if message not found
    }

    public List<Message> getUndeliveredMessages(int senderId) throws SQLException {
        String sql = "SELECT * FROM messages WHERE sender_id = ? AND delivered = FALSE";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, senderId);
            List<Message> messages = new ArrayList<>();

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Message msg = new Message();
                    msg.setId(rs.getInt("id"));
                    msg.setSenderId(rs.getInt("sender_id"));
                    msg.setReceiverId(rs.getInt("receiver_id"));
                    msg.setMessage(rs.getString("message"));
                    msg.setTimestamp(rs.getTimestamp("timestamp"));
                    messages.add(msg);
                }
            }
            return messages;
        }
    }
}