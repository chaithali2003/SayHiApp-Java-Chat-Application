package client;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import model.User;

public class UserProfile extends JFrame {
    private User contact;
    private MainDashboard parent;

    public UserProfile(User contact, MainDashboard parent) {
        this.contact = contact;
        this.parent = parent;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("User Profile");
        setSize(400, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Profile Image (centered, no circular cropping)
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imagePanel.setBackground(new Color(245, 245, 245));
        
        try {
            ImageIcon icon = new ImageIcon(ImageIO.read(new File("images/default_profile.png")));
            JLabel profileImage = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
            imagePanel.add(profileImage);
        } catch (IOException e) {
            // Fallback to default icon if image not found
            JLabel profileImage = new JLabel("No Image");
            profileImage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            imagePanel.add(profileImage);
        }

        // User details (email, phone, bio)
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(new Color(245, 245, 245));

        // Email
        JPanel emailPanel = createDetailPanel("Email:", 
            contact.getEmail() != null ? contact.getEmail() : "Not provided");
        
        // Phone
        JPanel phonePanel = createDetailPanel("Phone:", 
            contact.getPhone() != null ? contact.getPhone() : "Not provided");
        
        // Bio
        JPanel bioPanel = new JPanel(new BorderLayout());
        bioPanel.setBackground(new Color(245, 245, 245));
        bioPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel bioLabel = new JLabel("Bio:");
        bioLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bioLabel.setForeground(new Color(70, 70, 70));
        
        JTextArea bioArea = new JTextArea(contact.getBio() != null ? contact.getBio() : "No bio available", 5, 30);
        bioArea.setEditable(false);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bioArea.setBackground(new Color(245, 245, 245));
        bioArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        bioPanel.add(bioLabel, BorderLayout.NORTH);
        bioPanel.add(new JScrollPane(bioArea), BorderLayout.CENTER);

        detailsPanel.add(emailPanel);
        detailsPanel.add(phonePanel);
        detailsPanel.add(bioPanel);

        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(245, 245, 245));
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        styleButton(closeButton, Color.BLACK, Color.BLACK, 14);
        buttonPanel.add(closeButton);

        // Main layout
        mainPanel.add(imagePanel, BorderLayout.NORTH);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createDetailPanel(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(70, 70, 70));

        JTextField valueField = new JTextField(value);
        valueField.setEditable(false);
        valueField.setBorder(BorderFactory.createEmptyBorder());
        valueField.setBackground(new Color(245, 245, 245));
        valueField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueField, BorderLayout.CENTER);

        return panel;
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor, int fontSize) {
        button.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
}