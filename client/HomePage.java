package client;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class HomePage extends JFrame {
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JButton registerButton, loginButton;

    private float hue = 0f;
    private int yOffset = 0;
    private boolean movingUp = false;

    public HomePage() {
        setTitle("SayHi. Stay Connected.");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 800));
        setLocationRelativeTo(null);
        setUndecorated(false);

        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                Color color1 = Color.getHSBColor(hue, 0.7f, 0.8f);
                Color color2 = Color.getHSBColor(hue + 0.1f, 0.7f, 0.6f);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(new Color(255, 255, 255, 20));
                for (int i = 0; i < 20; i++) {
                    int size = 50 + i * 10;
                    g2d.fillOval(i * 100 - yOffset, 100, size, size);
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        // Title
        titleLabel = new JLabel("SayHi", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 96));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Subtitle
        JLabel subtitleLabel = new JLabel("Stay Connected.", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));

        // Description
        JLabel descriptionLabel = new JLabel("Connect with friends and family, anytime, anywhere.", SwingConstants.CENTER);
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        descriptionLabel.setForeground(new Color(255, 255, 255, 180));
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));

        // Buttons
        registerButton = createStyledButton("Register", new Color(76, 175, 80));
        loginButton = createStyledButton("Login", new Color(33, 150, 243));

        registerButton.addActionListener(e -> {
            new Register().setVisible(true);
            dispose();
        });

        loginButton.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        JPanel titlePanel = new JPanel(new GridLayout(3, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        titlePanel.add(descriptionLabel);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(titlePanel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Background animations
        new Timer(30, e -> {
            if (movingUp) {
                yOffset--;
                if (yOffset < -50) movingUp = false;
            } else {
                yOffset++;
                if (yOffset > 50) movingUp = true;
            }
            mainPanel.repaint();
        }).start();

        new Timer(50, e -> {
            hue += 0.005f;
            if (hue > 1f) hue = 0f;
            mainPanel.repaint();
        }).start();
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(baseColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(baseColor.brighter());
                } else {
                    g2.setColor(baseColor);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();

                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {}
        };

        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button.setPreferredSize(new Dimension(200, 55));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            HomePage homePage = new HomePage();
            homePage.setVisible(true);
        });
    }
}
