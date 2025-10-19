package client;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.io.IOException;
import java.util.List;
import db.ContactDAO;
import model.Contact;
import model.User;
import util.Constants;

public class MainDashboard extends JFrame {
    private User currentUser;
    private DefaultListModel<Contact> contactsListModel;
    private JList<Contact> contactsList;
    private Socket socket;
    private ObjectOutputStream out;
    private JPanel rightPanel;
    private JButton addContactButton;
    private JButton editProfileButton;
    private JButton logoutButton;
    private JLabel profileLabel;

    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(0, 122, 255);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_COLOR = new Color(33, 37, 41);
    private final Color HIGHLIGHT_COLOR = new Color(231, 241, 255);
    // private final Color UNREAD_COLOR = new Color(0, 132, 255);

    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    // private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);

    // Image dimensions
    private final int PROFILE_IMAGE_SIZE = 80;
    private final int CONTACT_IMAGE_SIZE = 60;

    public MainDashboard(User user, ObjectOutputStream out, Socket socket) {
        this.currentUser = user;
        this.out = out;
        this.socket = socket;

        initializeUI();
        setupEventHandlers();
        loadContacts();
    }

    private void initializeUI() {
        setTitle("SayHi - " + currentUser.getName());
        setSize(1000, 800);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Main split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.3);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setDividerSize(3);

        JPanel leftPanel = createLeftPanel();
        rightPanel = createRightPanel();

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        add(splitPane);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BACKGROUND_COLOR);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Profile panel
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setOpaque(false);
        profilePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Profile image with fixed size
        ImageIcon profileIcon = new ImageIcon(Constants.DEFAULT_PROFILE_PIC);
        Image scaledProfile = profileIcon.getImage().getScaledInstance(
            PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, Image.SCALE_SMOOTH);
        JLabel profileImage = new JLabel(new ImageIcon(scaledProfile));
        profileImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileImage.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        profileImage.setPreferredSize(new Dimension(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE));
        profileImage.setMaximumSize(new Dimension(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE));
        profileImage.setMinimumSize(new Dimension(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE));

        // Profile name
        profileLabel = new JLabel(currentUser.getName());
        profileLabel.setFont(TITLE_FONT);
        profileLabel.setForeground(TEXT_COLOR);
        profileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        profilePanel.add(profileImage);
        profilePanel.add(profileLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        editProfileButton = createAnimatedButton("Edit Profile", new Color(46, 204, 113));
        logoutButton = createAnimatedButton("Logout", new Color(220, 53, 69));

        buttonPanel.add(editProfileButton);
        buttonPanel.add(logoutButton);

        // Contacts list
        contactsListModel = new DefaultListModel<>();
        contactsList = new JList<>(contactsListModel);
        contactsList.setCellRenderer(new ContactListRenderer());
        contactsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactsList.setFixedCellHeight(80);
        contactsList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JScrollPane contactsScrollPane = new JScrollPane(contactsList);
        contactsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        contactsScrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        // Add contact button
        addContactButton = createAnimatedButton("+ Add Contact", PRIMARY_COLOR);
        addContactButton.setFont(BUTTON_FONT);
        addContactButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 0, 5, 0),
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200, 100))));

        // Add components to left panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(profilePanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        leftPanel.add(topPanel, BorderLayout.NORTH);
        leftPanel.add(contactsScrollPane, BorderLayout.CENTER);
        leftPanel.add(addContactButton, BorderLayout.SOUTH);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        
        // Create a more attractive default right panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Chat icon
        ImageIcon chatIcon = new ImageIcon("images/chat_icon.png"); 
        Image scaledIcon = chatIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(scaledIcon));
        centerPanel.add(iconLabel, gbc);
        
        // Welcome text
        JLabel welcomeLabel = new JLabel("Welcome to SayHi");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(100, 100, 100));
        centerPanel.add(welcomeLabel, gbc);
        
        // Instruction text
        JLabel instructionLabel = new JLabel("Select a contact to start chatting");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        instructionLabel.setForeground(new Color(150, 150, 150));
        centerPanel.add(instructionLabel, gbc);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private void setupEventHandlers() {
        contactsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Contact selectedContact = contactsList.getSelectedValue();
                if (selectedContact != null) {
                    openChatWindow(selectedContact);
                }
            }
        });

        contactsList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    Contact selectedContact = contactsList.getSelectedValue();
                    if (selectedContact != null) {
                        openUserProfile(selectedContact);
                    }
                }
            }
        });

        addContactButton.addActionListener(e -> {
            ContactImporter importer = new ContactImporter(currentUser, MainDashboard.this);
            importer.setVisible(true);
        });

        editProfileButton.addActionListener(e -> {
            ProfileEditor editor = new ProfileEditor(currentUser, MainDashboard.this);
            editor.setVisible(true);
        });

        logoutButton.addActionListener(e -> logout());
    }

    private void openChatWindow(Contact contact) {
        rightPanel.removeAll();
        // Create a User object from the Contact for ChatWindow
        User contactUser = new User(
            contact.getId(),
            contact.getRegisteredName(),
            null, // email
            contact.getPhone(),
            null, // password
            null, // profile_pic
            null  // bio
        );
        rightPanel.add(new ChatWindow(currentUser, contactUser, out), BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void openUserProfile(Contact contact) {
    // Create a User object from the Contact for UserProfile
    User contactUser = new User(
        contact.getId(),
        contact.getRegisteredName(),
        contact.getEmail(), // Use actual email if available
        contact.getPhone(),
        null, // password (not needed for display)
        null, // profile_pic (can keep null if not used)
        contact.getBio()    // Use actual bio if available
    );
    
    UserProfile profile = new UserProfile(contactUser, this);
    profile.setVisible(true);
}

    private void logout() {
        int response = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException ex) {
                System.err.println("Error closing socket: " + ex.getMessage());
            }
            new HomePage().setVisible(true);
            dispose();
        }
    }

    private void loadContacts() {
        SwingWorker<List<Contact>, Void> worker = new SwingWorker<List<Contact>, Void>() {
            @Override
            protected List<Contact> doInBackground() throws Exception {
                ContactDAO contactDAO = new ContactDAO();
                return contactDAO.getContacts(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    List<Contact> contacts = get();
                    contactsListModel.clear();
                    for (Contact contact : contacts) {
                        contactsListModel.addElement(contact);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainDashboard.this,
                            "Error loading contacts: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    public void refreshUserProfile(User updatedUser) {
        this.currentUser = updatedUser;
        profileLabel.setText(updatedUser.getName());
        setTitle("SayHi - " + updatedUser.getName());
        revalidate();
        repaint();
    }

    public void refreshContacts() {
        loadContacts();
    }

    private class ContactListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Contact) {
                Contact contact = (Contact) value;
                
                String displayText = contact.getEffectiveName();
                String lastMessage = "Click to chat"; 
                
                setText("<html><div style='padding:5px;'>" +
                        "<b style='font-size:16px;'>" + displayText + "</b>" +
                        "<br><span style='color:#6c757d; font-size:10px;'>" + lastMessage + "</span>" +
                        "</div></html>");

                ImageIcon icon = new ImageIcon(Constants.DEFAULT_PROFILE_PIC);
                Image scaledImage = icon.getImage().getScaledInstance(
                    CONTACT_IMAGE_SIZE, CONTACT_IMAGE_SIZE, Image.SCALE_SMOOTH);
                setIcon(new ImageIcon(scaledImage));

                setIconTextGap(20);
                setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                if (isSelected) {
                    setBackground(HIGHLIGHT_COLOR);
                    setForeground(TEXT_COLOR);
                } else {
                    setBackground(CARD_COLOR);
                    setForeground(TEXT_COLOR);
                }
            }
            return this;
        }
    }

    private JButton createAnimatedButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(baseColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(baseColor.brighter());
                } else {
                    g2.setColor(baseColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
            }
        };

        button.setFont(BUTTON_FONT);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

    class ModernScrollBarUI extends BasicScrollBarUI {
        private final int SCROLL_BAR_ALPHA_ROLLOVER = 150;
        private final int SCROLL_BAR_ALPHA = 100;
        private final int THUMB_SIZE = 8;
        private final Color THUMB_COLOR = Color.GRAY;

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createInvisibleButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createInvisibleButton();
        }

        private JButton createInvisibleButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            // No track painting
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            int alpha = isThumbRollover() ? SCROLL_BAR_ALPHA_ROLLOVER : SCROLL_BAR_ALPHA;
            int orientation = scrollbar.getOrientation();
            int x = thumbBounds.x;
            int y = thumbBounds.y;

            int width = orientation == JScrollBar.VERTICAL ? THUMB_SIZE : thumbBounds.width;
            width = Math.max(width, THUMB_SIZE);

            int height = orientation == JScrollBar.VERTICAL ? thumbBounds.height : THUMB_SIZE;
            height = Math.max(height, THUMB_SIZE);

            Graphics2D graphics2D = (Graphics2D) g.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(new Color(THUMB_COLOR.getRed(), THUMB_COLOR.getGreen(), THUMB_COLOR.getBlue(), alpha));
            graphics2D.fillRoundRect(x, y, width, height, 10, 10);
            graphics2D.dispose();
        }
    }
}