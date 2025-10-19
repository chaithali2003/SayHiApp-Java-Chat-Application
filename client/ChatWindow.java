package client;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;
import java.io.*;
import java.time.format.*;
import db.*;
import model.*;

public class ChatWindow extends JPanel {
    private User currentUser;
    private User contact;
    private JTextPane chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private ObjectOutputStream out;
    private Timer messageCheckTimer;
    private Timestamp lastMessageTime;

    // Colors and fonts
    private final Font MESSAGE_FONT = new Font("Segoe UI", Font.PLAIN, 18);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 18);

    public ChatWindow(User currentUser, User contact, ObjectOutputStream out) {
        this.currentUser = currentUser;
        this.contact = contact;
        this.out = out;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        createChatArea();
        createInputPanel();
        loadChatHistory();
        startMessageCheckTimer();
    }

    private void createChatArea() {
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setContentType("text/html");
        chatArea.setText("<html><body style='font-family:Segoe UI; margin:0; padding:10px;'></body></html>");
        chatArea.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        
        DefaultCaret caret = (DefaultCaret) chatArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inputPanel.setBackground(Color.WHITE);

        messageField = new JTextField();
        styleMessageField();

        sendButton = new JButton("SEND");
        styleSendButton();
        sendButton.setEnabled(false);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(sendButton, BorderLayout.EAST);
        buttonPanel.setOpaque(false);

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);
    }

    private void styleMessageField() {
        messageField.setFont(MESSAGE_FONT);
        messageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        messageField.setPreferredSize(new Dimension(messageField.getWidth(), 50));
        
        messageField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateSendButton(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateSendButton(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateSendButton(); }
        });
        
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown() && sendButton.isEnabled()) {
                    sendMessage();
                }
            }
        });
    }

    private void styleSendButton() {
        sendButton.setFont(BUTTON_FONT);
        sendButton.setBackground(Color.BLACK);
        sendButton.setForeground(Color.BLACK);
        sendButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        sendButton.setFocusPainted(false);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(e -> sendMessage());
    }

    private void updateSendButton() {
        sendButton.setEnabled(!messageField.getText().trim().isEmpty());
    }

    private void sendMessage() {
    String text = messageField.getText().trim();
    if (text.isEmpty()) return;

    Message message = createMessage(text);
    appendMessage(message); // Display immediately
    messageField.setText("");

    try {
        int messageId = new MessageDAO().saveMessage(message);
        message.setId(messageId);
        
        if (out != null) {
            try {
                out.writeObject(message);
                out.flush();
                new MessageDAO().updateDeliveryStatus(messageId, true);
            } catch (IOException e) {
                System.err.println("Network error: " + e.getMessage());
                new MessageDAO().markForRetry(messageId);
            }
        }
    } catch (SQLException e) {
        showDetailedError("Database Error", 
                        "Failed to save message:\n" + e.getMessage(),
                        getDatabaseErrorDetails(e));
    }
}

private String getDatabaseErrorDetails(SQLException e) {
    return "SQL State: " + e.getSQLState() + "\n" +
           "Error Code: " + e.getErrorCode() + "\n" +
           "Message: " + e.getMessage();
}

private void showDetailedError(String title, String message, String details) {
    JTextArea textArea = new JTextArea(details);
    JScrollPane scrollPane = new JScrollPane(textArea);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    scrollPane.setPreferredSize(new Dimension(500, 150));
    JOptionPane.showMessageDialog(this, 
        new Object[]{message, scrollPane},
        title, 
        JOptionPane.ERROR_MESSAGE);
}

    private Message createMessage(String text) {
        Message message = new Message();
        message.setSenderId(currentUser.getId());
        message.setReceiverId(contact.getId());
        message.setMessage(text);
        message.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return message;
    }

    private void appendMessage(Message message) {
        SwingUtilities.invokeLater(() -> {
            String currentContent = chatArea.getText().replace("</body></html>", "");
            String formattedMessage = formatMessage(message);
            chatArea.setText(currentContent + formattedMessage + "</body></html>");
            
            // Auto-scroll to bottom
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            lastMessageTime = message.getTimestamp();
        });
    }

    private String formatMessage(Message message) {
        boolean isSent = message.getSenderId() == currentUser.getId();
        String timestamp = message.getTimestamp().toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("h:mm a, MMM d"));
        
        return String.format(
            "<div style='margin-%s:20%%; margin-bottom:15px; text-align:%s;'>" +
                "<div style='background-color:%s; color:%s; border-radius:15px; " +
                "padding:12px 15px; display:inline-block; max-width:80%%; " +
                "word-wrap:break-word; font-size:18px;'>" +
                    "%s" +
                "</div>" +
                "<div style='color:#777; font-size:14px; padding:4px 8px;'>" +
                    "%s" +
                "</div>" +
            "</div>",
            isSent ? "left" : "right",
            isSent ? "right" : "left", 
            isSent ? "#007AFF" : "#E6E6E6",
            isSent ? "white" : "black",
            message.getMessage(),
            timestamp
        );
    }

    private void loadChatHistory() {
        try {
            List<Message> messages = new MessageDAO().getMessagesBetweenUsers(
                currentUser.getId(), contact.getId());
            
            StringBuilder html = new StringBuilder("<html><body style='font-family:Segoe UI; margin:0; padding:10px;'>");
            for (Message message : messages) {
                html.append(formatMessage(message));
                lastMessageTime = message.getTimestamp();
            }
            html.append("</body></html>");
            chatArea.setText(html.toString());
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        } catch (SQLException e) {
            showError("Error loading chat history: " + e.getMessage());
        }
    }

    private void startMessageCheckTimer() {
        messageCheckTimer = new Timer(3000, e -> {
            try {
                List<Message> newMessages = new MessageDAO().getNewMessages(
                    currentUser.getId(), contact.getId(), lastMessageTime);
                for (Message message : newMessages) {
                    appendMessage(message);
                }
            } catch (SQLException ex) {
                System.err.println("Error checking new messages: " + ex.getMessage());
            }
        });
        messageCheckTimer.start();
    }

    

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (messageCheckTimer != null) {
            messageCheckTimer.stop();
        }
    }
}