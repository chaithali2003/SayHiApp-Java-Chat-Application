package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import db.ContactDAO;
import db.UserDAO;
import model.User;

public class ContactImporter extends JFrame {
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(0, 122, 255);
    private final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TEXT_COLOR = new Color(33, 37, 41);
    private final Color DISABLED_COLOR = new Color(240, 240, 240);

    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 18);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 18);

    private User currentUser;
    private MainDashboard parent;
    private JTextField nameField;
    private JTextField phoneField;
    private JLabel registeredNameLabel;
    private User foundUser;

    public ContactImporter(User currentUser, MainDashboard parent) {
        this.currentUser = currentUser;
        this.parent = parent;

        initializeWindow();
        initializeUI();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void initializeWindow() {
        setTitle("Add Contact");
        setSize(650, 350); // Reduced height from 400 to 350
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setUndecorated(false);
    }

    private void initializeUI() {
        // Main panel with background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15)); // Reduced padding

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0)); // Reduced bottom padding

        JLabel titleLabel = new JLabel("Add New Contact");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(15, 10, 15, 10)); // Reduced padding

        // Phone number field
        JPanel phonePanel = new JPanel(new BorderLayout(10, 0));
        phonePanel.setOpaque(false);
        phonePanel.add(createLabel("Phone Number:"), BorderLayout.WEST);
        phoneField = createTextField("");
        phoneField.setMaximumSize(new Dimension(Short.MAX_VALUE, 30)); // Reduced height to 30
        phoneField.setDocument(new JTextFieldLimit(10));
        phoneField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!(Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
                    evt.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (phoneField.getText().length() == 10) {
                    lookupRegisteredName();
                } else {
                    resetNameField();
                }
            }
        });
        phonePanel.add(phoneField, BorderLayout.CENTER);
        formPanel.add(phonePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Reduced spacer

        // Display name field
        JPanel namePanel = new JPanel(new BorderLayout(10, 0));
        namePanel.setOpaque(false);
        namePanel.add(createLabel("Name:"), BorderLayout.WEST);
        nameField = createTextField("");
        nameField.setMaximumSize(new Dimension(Short.MAX_VALUE, 30)); // Reduced height to 30
        nameField.setEnabled(false);
        nameField.setBackground(DISABLED_COLOR);
        namePanel.add(nameField, BorderLayout.CENTER);
        formPanel.add(namePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Reduced spacer

        // Registered name label
        registeredNameLabel = new JLabel(" ");
        registeredNameLabel.setFont(SUBTITLE_FONT);
        registeredNameLabel.setForeground(SECONDARY_COLOR);
        registeredNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(registeredNameLabel);

        // Button panel
        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new EmptyBorder(15, 0, 0, 0)); // Reduced top padding

        JButton addButton = createStyledButton("Add Contact", PRIMARY_COLOR);
        JButton cancelButton = createStyledButton("Cancel", SECONDARY_COLOR);

        actionPanel.add(addButton);
        actionPanel.add(Box.createRigidArea(new Dimension(15, 0))); // Reduced spacer
        actionPanel.add(cancelButton);

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        // Event listeners
        addButton.addActionListener(e -> addContact());
        cancelButton.addActionListener(e -> dispose());

        add(mainPanel);
    }

    private void lookupRegisteredName() {
        String phone = phoneField.getText().trim();
        if (phone.length() != 10)
            return;

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                UserDAO userDAO = new UserDAO();
                return userDAO.findUserByPhone(phone);
            }

            @Override
            protected void done() {
                try {
                    foundUser = get();
                    if (foundUser != null) {
                        registeredNameLabel.setText("Registered Name: " + foundUser.getName());
                        nameField.setText(foundUser.getName());
                        nameField.setEnabled(true);
                        nameField.setBackground(Color.WHITE);
                    } else {
                        registeredNameLabel.setText("User not found");
                        resetNameField();
                    }
                } catch (Exception e) {
                    registeredNameLabel.setText("Error looking up user");
                    resetNameField();
                }
            }
        };
        worker.execute();
    }

    private void resetNameField() {
        nameField.setText("");
        nameField.setEnabled(false);
        nameField.setBackground(DISABLED_COLOR);
        foundUser = null;
    }

    private void addContact() {
        String displayName = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (displayName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (phone.length() != 10) {
            JOptionPane.showMessageDialog(this,
                    "Phone number must be 10 digits", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (phone.equals(currentUser.getPhone())) {
            JOptionPane.showMessageDialog(this,
                    "You cannot add yourself as a contact", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (foundUser == null) {
            JOptionPane.showMessageDialog(this,
                    "Please verify phone number first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ContactDAO contactDAO = new ContactDAO();

            if (contactDAO.isContact(currentUser.getId(), foundUser.getId())) {
                JOptionPane.showMessageDialog(this,
                        "This contact already exists in your list", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Updated to use the new method signature (without registered_name)
            if (contactDAO.addContactWithDisplayName(
                    currentUser.getId(),
                    foundUser.getId(),
                    nameField.getText().trim())) { // Only passing display name

                JOptionPane.showMessageDialog(this,
                        "Contact added successfully", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                parent.refreshContacts();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add contact", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper class to limit text field length
    class JTextFieldLimit extends javax.swing.text.PlainDocument {
        private int limit;

        JTextFieldLimit(int limit) {
            super();
            this.limit = limit;
        }

        public void insertString(int offset, String str, javax.swing.text.AttributeSet attr)
                throws javax.swing.text.BadLocationException {
            if (str == null)
                return;

            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createTextField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(SUBTITLE_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10))); // Reduced padding
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                super.paintComponent(g);
            }
        };

        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 35, 15, 35));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }
}