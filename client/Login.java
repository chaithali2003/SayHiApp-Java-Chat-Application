package client;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.IOException;
import db.UserDAO;
import model.User;
import java.util.ArrayList;
import java.util.Random;

public class Login extends JFrame {
    private JTextField emailOrPhoneField;
    private JPasswordField passwordField;
    private final int MIN_WIDTH = 1000;
    private final int MIN_HEIGHT = 800;
    private final ArrayList<Bubble> bubbles = new ArrayList<>();

    public Login() {
        setTitle("Login - SayHi");
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

        // Login panel
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

        JLabel titleLabel = new JLabel("Login to SayHi", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);

        JLabel emailLabel = new JLabel("Email / Phone:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        emailLabel.setForeground(Color.WHITE);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        passwordLabel.setForeground(Color.WHITE);

        emailOrPhoneField = new JTextField(20);
        emailOrPhoneField.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        JButton loginButton = new JButton("Login");
        styleButton(loginButton);

        // JButton forgotPasswordButton = new JButton("Forgot Password?");
        // styleButton(forgotPasswordButton);

        JButton backButton = new JButton("Back");
        styleButton(backButton);

        JLabel registerLabel = new JLabel("New to SayHi?");
        registerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        registerLabel.setForeground(Color.WHITE);

        JButton registerNowButton = new JButton("Register Now");
        styleButton(registerNowButton);

        // Add components to formPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(emailOrPhoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        formPanel.add(loginButton, gbc);

        // gbc.gridy++;
        // formPanel.add(forgotPasswordButton, gbc);

        gbc.gridy++;
        formPanel.add(backButton, gbc);

        gbc.gridy++;
        JPanel registerPanel = new JPanel();
        registerPanel.setOpaque(false);
        registerPanel.add(registerLabel);
        registerPanel.add(registerNowButton);
        formPanel.add(registerPanel, gbc);

        // Action Listeners
        loginButton.addActionListener(e -> handleLogin());
        // forgotPasswordButton.addActionListener(e -> new ForgotPassword().setVisible(true));
        registerNowButton.addActionListener(e -> {
            new Register().setVisible(true);
            dispose();
        });
        backButton.addActionListener(e -> {
            new HomePage().setVisible(true);
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

    private void handleLogin() {
        String emailOrPhone = emailOrPhoneField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (emailOrPhone.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Email/Phone and Password are required",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.login(emailOrPhone, password);

            if (user != null) {
                try {
                    Socket socket = new Socket("127.0.0.1", 5555);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                    JOptionPane.showMessageDialog(this,
                            "Login successful!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    new MainDashboard(user, out, socket).setVisible(true);
                    dispose();
                } catch (IOException ioEx) {
                    JOptionPane.showMessageDialog(this,
                            "Error connecting to server: " + ioEx.getMessage(),
                            "Connection Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid email/phone or password",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
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
