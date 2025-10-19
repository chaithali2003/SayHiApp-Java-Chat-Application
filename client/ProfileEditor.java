package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import db.UserDAO;
import model.User;

public class ProfileEditor extends JFrame {
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(0, 122, 255);
    private final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TEXT_COLOR = new Color(33, 37, 41);

    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea bioArea;
    private JLabel profilePicLabel;
    private User currentUser;
    private MainDashboard parent;

    public ProfileEditor(User user, MainDashboard parent) {
        this.currentUser = user;
        this.parent = parent;
        
        initializeWindow();
        initializeUI();
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void initializeWindow() {
        setTitle("Edit Profile - " + currentUser.getName());
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void initializeUI() {
        // Main panel with background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header panel with window controls
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Edit Profile");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Window controls (close button)
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsPanel.setOpaque(false);

        headerPanel.add(controlsPanel, BorderLayout.EAST);

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);

        // Profile picture panel
        JPanel picPanel = new JPanel(new BorderLayout());
        picPanel.setOpaque(false);
        picPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        profilePicLabel = new JLabel() {
            private Image image;
            
            {
                // Load the image
                image = loadProfileImage();
                if (image == null) {
                    // Create placeholder if image not found
                    BufferedImage placeholder = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = placeholder.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(PRIMARY_COLOR);
                    g2d.fillOval(0, 0, 200, 200);
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 48));
                    String initial = currentUser.getName().isEmpty() ? "?" 
                        : currentUser.getName().substring(0, 1).toUpperCase();
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (200 - fm.stringWidth(initial)) / 2;
                    int y = (200 - fm.getHeight()) / 2 + fm.getAscent();
                    g2d.drawString(initial, x, y);
                    g2d.dispose();
                    image = placeholder;
                }
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(200, 200);
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int diameter = Math.min(getWidth(), getHeight());
                int x = (getWidth() - diameter) / 2;
                int y = (getHeight() - diameter) / 2;
                
                // Create circular clip
                Shape clip = new Ellipse2D.Float(x, y, diameter, diameter);
                g2.setClip(clip);
                
                // Calculate scaling to fit the image completely within the circle
                if (image != null) {
                    double scale = Math.min((double)diameter/image.getWidth(null), 
                                          (double)diameter/image.getHeight(null));
                    int scaledWidth = (int)(image.getWidth(null) * scale);
                    int scaledHeight = (int)(image.getHeight(null) * scale);
                    int imgX = x + (diameter - scaledWidth) / 2;
                    int imgY = y + (diameter - scaledHeight) / 2;
                    
                    g2.drawImage(image, imgX, imgY, scaledWidth, scaledHeight, null);
                }
            }
        };
        profilePicLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profilePicLabel.setVerticalAlignment(SwingConstants.CENTER);

        picPanel.add(profilePicLabel, BorderLayout.CENTER);

        // Details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        detailsPanel.add(createLabel("Name:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        nameField = createTextField(currentUser.getName());
        detailsPanel.add(nameField, gbc);

        // Email field (disabled)
        gbc.gridx = 0; gbc.gridy = 1;
        detailsPanel.add(createLabel("Email:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        emailField = createTextField(currentUser.getEmail());
        emailField.setEnabled(false);
        emailField.setBackground(new Color(240, 240, 240));
        detailsPanel.add(emailField, gbc);

        // Phone field (disabled)
        gbc.gridx = 0; gbc.gridy = 2;
        detailsPanel.add(createLabel("Phone:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        phoneField = createTextField(currentUser.getPhone() != null ? currentUser.getPhone() : "");
        phoneField.setEnabled(false);
        phoneField.setBackground(new Color(240, 240, 240));
        detailsPanel.add(phoneField, gbc);

        // Bio field
        gbc.gridx = 0; gbc.gridy = 3;
        detailsPanel.add(createLabel("Bio:"), gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.gridheight = 2;
        bioArea = new JTextArea(currentUser.getBio() != null ? currentUser.getBio() : "", 5, 20);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setFont(SUBTITLE_FONT);
        bioArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JScrollPane bioScroll = new JScrollPane(bioArea);
        bioScroll.setBorder(BorderFactory.createEmptyBorder());
        bioScroll.setOpaque(false);
        bioScroll.getViewport().setOpaque(false);
        detailsPanel.add(bioScroll, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton cancelButton = createStyledButton("Cancel", SECONDARY_COLOR);
        JButton saveButton = createStyledButton("Save Changes", PRIMARY_COLOR);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Add components to content panel
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.add(picPanel);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(detailsPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Event listeners
        saveButton.addActionListener(e -> saveProfile());
        cancelButton.addActionListener(e -> dispose());

        add(mainPanel);
    }

    private Image loadProfileImage() {
        try {
            // 1. Try loading from the images folder
            File imageFile = new File("images/default_profile.png");
            if (imageFile.exists()) {
                return ImageIO.read(imageFile);
            }
            
            // 2. Try loading as resource
            InputStream resourceStream = getClass().getResourceAsStream("/images/default_profile.png");
            if (resourceStream != null) {
                return ImageIO.read(resourceStream);
            }
        } catch (Exception e) {
            System.err.println("Error loading profile image: " + e.getMessage());
        }
        return null;
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
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                super.paintComponent(g);
            }
        };

        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void saveProfile() {
        String name = nameField.getText().trim();
        String bio = bioArea.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name is required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentUser.setName(name);
        currentUser.setBio(bio);

        try {
            UserDAO userDAO = new UserDAO();
            if (userDAO.updateProfile(currentUser)) {
                JOptionPane.showMessageDialog(this,
                        "Profile updated successfully", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                parent.refreshUserProfile(currentUser);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update profile", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}