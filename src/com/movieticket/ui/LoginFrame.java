package com.movieticket.ui;

import com.movieticket.model.User;
import com.movieticket.service.DataStore;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private DataStore dataStore = DataStore.getInstance();

    public LoginFrame() {
        super("CinePass Tamil Nadu - Sign In");
        setSize(850, 520);
        setMinimumSize(new Dimension(800, 480));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new GridLayout(1, 2));

        initUI();
    }

    private void initUI() {
        // LEFT BANNER PANEL
        JPanel leftBanner = new JPanel();
        leftBanner.setBackground(new Color(20, 24, 38));
        leftBanner.setLayout(new BoxLayout(leftBanner, BoxLayout.Y_AXIS));
        leftBanner.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel appTitle = new JLabel("🎟 CINEPASS TN");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        appTitle.setForeground(UITheme.ACCENT_PRIMARY);

        JLabel tagLine = new JLabel("Tamil Nadu Cinema Ticket Booking");
        tagLine.setFont(UITheme.FONT_SUBHEADER);
        tagLine.setForeground(Color.WHITE);

        JLabel desc = new JLabel("<html><body style='width: 250px; color: #9ca3af; font-size: 13px; line-height: 1.5;'>"
                + "<br>Experience fast, seamless seat reservation for all the latest Kollywood blockbusters across Chennai, Coimbatore, Madurai, and Trichy."
                + "<br><br><b>Default Credentials:</b>"
                + "<br>👑 <b>Admin:</b> admin / admin"
                + "<br>👤 <b>User:</b> user / user123"
                + "</body></html>");

        leftBanner.add(appTitle);
        leftBanner.add(Box.createVerticalStrut(6));
        leftBanner.add(tagLine);
        leftBanner.add(Box.createVerticalStrut(15));
        leftBanner.add(desc);

        // RIGHT LOGIN FORM PANEL
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(UITheme.BG_DARK);

        JPanel cardPanel = UITheme.createCardPanel();
        cardPanel.setPreferredSize(new Dimension(340, 380));
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));

        JLabel loginHeader = new JLabel("Welcome Back");
        loginHeader.setFont(UITheme.FONT_HEADER);
        loginHeader.setForeground(Color.WHITE);

        JLabel loginSub = new JLabel("Sign in to your account");
        loginSub.setFont(UITheme.FONT_SMALL);
        loginSub.setForeground(UITheme.TEXT_MUTED);

        JLabel uLbl = new JLabel("Username");
        uLbl.setFont(UITheme.FONT_SMALL_BOLD);
        uLbl.setForeground(UITheme.TEXT_PRIMARY);

        usernameField = UITheme.createTextField(20);
        usernameField.setText("admin"); // Default to easily test user's admin request

        JLabel pLbl = new JLabel("Password");
        pLbl.setFont(UITheme.FONT_SMALL_BOLD);
        pLbl.setForeground(UITheme.TEXT_PRIMARY);

        passwordField = UITheme.createPasswordField(20);
        passwordField.setText("admin"); // Default for quick admin entry

        JButton loginBtn = UITheme.createPrimaryButton("Sign In ➔");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.addActionListener(e -> handleLogin());

        JButton registerBtn = UITheme.createSecondaryButton("Register New Account");
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        registerBtn.addActionListener(e -> {
            RegisterDialog reg = new RegisterDialog(this);
            reg.setVisible(true);
        });

        // Quick Admin / User switch buttons for effortless UI demo
        JPanel quickSwitchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        quickSwitchPanel.setOpaque(false);

        JButton adminPrefill = UITheme.createPillButton("👑 Use Admin", UITheme.ACCENT_GOLD);
        adminPrefill.addActionListener(e -> {
            usernameField.setText("admin");
            passwordField.setText("admin");
        });

        JButton userPrefill = UITheme.createPillButton("👤 Use Customer", UITheme.ACCENT_CYAN);
        userPrefill.addActionListener(e -> {
            usernameField.setText("user");
            passwordField.setText("user123");
        });

        quickSwitchPanel.add(adminPrefill);
        quickSwitchPanel.add(userPrefill);

        cardPanel.add(loginHeader);
        cardPanel.add(Box.createVerticalStrut(2));
        cardPanel.add(loginSub);
        cardPanel.add(Box.createVerticalStrut(16));
        cardPanel.add(uLbl);
        cardPanel.add(Box.createVerticalStrut(4));
        cardPanel.add(usernameField);
        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(pLbl);
        cardPanel.add(Box.createVerticalStrut(4));
        cardPanel.add(passwordField);
        cardPanel.add(Box.createVerticalStrut(16));
        cardPanel.add(loginBtn);
        cardPanel.add(Box.createVerticalStrut(8));
        cardPanel.add(registerBtn);
        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(quickSwitchPanel);

        rightPanel.add(cardPanel);

        add(leftBanner);
        add(rightPanel);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Login Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = dataStore.authenticate(username, password);
        if (user != null) {
            dispose();
            if (user.isAdmin()) {
                new AdminDashboardFrame(user).setVisible(true);
            } else {
                new UserDashboardFrame(user).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials! Check username and password.\nAdmin: admin / admin", "Access Denied", JOptionPane.ERROR_MESSAGE);
        }
    }
}
