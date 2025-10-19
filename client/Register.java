package client;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import db.UserDAO;
import model.User;
import java.util.ArrayList;
import java.util.Random;

public class Register extends JFrame {
    private JTextField nameField, emailField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;
    private final int MIN_WIDTH = 1000;
    private final int MIN_HEIGHT = 800;
    private final ArrayList<Bubble> bubbles = new ArrayList<>();

    public Register() {
        setTitle("Register - SayHi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());
        add(mainPanel);

        // Create bubbles for animation
        Timer bubbleTimer = new Timer(30, e -> {
            for (Bubble bubble : bubbles) {
                bubble.move();
            }
            mainPanel.repaint();
        });
        bubbleTimer.start();

        // Create bubbles
        for (int i = 0; i < 20; i++) {
            bubbles.add(new Bubble());
        }

        // Register panel
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
            }
        };
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2, true), // Rounded border
                BorderFactory.createEmptyBorder(30, 30, 30, 30) // Padding
        ));
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Create an Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setForeground(Color.WHITE);
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        // limitPhoneField(phoneField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setForeground(Color.WHITE);
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        phoneField = new JTextField(20);
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        JButton registerButton = new JButton("Register");
        styleButton(registerButton);

        JLabel loginLabel = new JLabel("Registered already?");
        loginLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        loginLabel.setForeground(Color.WHITE);

        JButton loginButton = new JButton("Login");
        styleButton(loginButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        formPanel.add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(registerButton, gbc);

        gbc.gridy++;
        JPanel loginPanel = new JPanel();
        loginPanel.setOpaque(false);
        loginPanel.add(loginLabel);
        loginPanel.add(loginButton);
        formPanel.add(loginPanel, gbc);

        // Button Actions
        registerButton.addActionListener(e -> registerUser());
        loginButton.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });

        mainPanel.add(formPanel, new GridBagConstraints());
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(0, 102, 204));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void registerUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, Email, and Password are required", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address", "Invalid Email",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (phone.length() != 10 || !phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone number must be exactly 10 digits", "Invalid Phone",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            if (userDAO.isEmailExists(email)) {
                JOptionPane.showMessageDialog(this, "Email already registered!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (userDAO.isPhoneExists(phone)) {
                JOptionPane.showMessageDialog(this, "Phone number already registered!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = new User(0, name, email, phone, password, null, null);
            if (userDAO.registerUser(user)) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please login.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                new Login().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Gradient panel with animated bubbles
    class GradientPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            int width = getWidth();
            int height = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, new Color(0, 0, 50), 0, height, new Color(0, 128, 255));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);

            for (Bubble bubble : bubbles) {
                g2d.setColor(new Color(255, 255, 255, 80));
                g2d.fillOval(bubble.x, bubble.y, bubble.size, bubble.size);
            }
        }
    }

    class Bubble {
        int x, y, size, speed;
        Random rand = new Random();

        Bubble() {
            reset();
        }

        void move() {
            y -= speed;
            if (y + size < 0) {
                reset();
            }
        }

        void reset() {
            x = rand.nextInt(getWidth());
            y = getHeight() + rand.nextInt(300);
            size = 10 + rand.nextInt(40);
            speed = 1 + rand.nextInt(3);
        }
    }
}
