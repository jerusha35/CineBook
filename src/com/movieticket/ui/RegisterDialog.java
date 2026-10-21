package com.movieticket.ui;

import com.movieticket.model.User;
import com.movieticket.service.DataStore;

import javax.swing.*;
import java.awt.*;

public class RegisterDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JTextField phoneField;
    private DataStore dataStore = DataStore.getInstance();

    public RegisterDialog(JFrame parent) {
        super(parent, "Create CinePass Account", true);
        setSize(450, 480);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UITheme.BG_CARD);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UITheme.BG_CARD);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel titleLbl = new JLabel("Join CinePass Tamil Nadu");
        titleLbl.setFont(UITheme.FONT_HEADER);
        titleLbl.setForeground(Color.WHITE);

        JLabel subLbl = new JLabel("Book first-day shows across Chennai, Coimbatore & Madurai");
        subLbl.setFont(UITheme.FONT_SMALL);
        subLbl.setForeground(UITheme.TEXT_MUTED);

        headerPanel.add(titleLbl);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subLbl);
        add(headerPanel, BorderLayout.NORTH);

        // Form fields
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 14));
        formPanel.setBackground(UITheme.BG_CARD);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        fullNameField = UITheme.createTextField(15);
        phoneField = UITheme.createTextField(15);
        usernameField = UITheme.createTextField(15);
        passwordField = UITheme.createPasswordField(15);
        confirmPasswordField = UITheme.createPasswordField(15);

        addField(formPanel, "Full Name:", fullNameField);
        addField(formPanel, "Mobile Number:", phoneField);
        addField(formPanel, "Choose Username:", usernameField);
        addField(formPanel, "Password:", passwordField);
        addField(formPanel, "Confirm Password:", confirmPasswordField);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        btnPanel.setOpaque(false);

        JButton registerBtn = UITheme.createPrimaryButton("Register Now");
        registerBtn.addActionListener(e -> handleRegister());

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(cancelBtn);
        btnPanel.add(registerBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void addField(JPanel panel, String label, JComponent comp) {
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_BOLD);
        l.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(l);
        panel.add(comp);
    }

    private void handleRegister() {
        String name = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all details.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Password Mismatch", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String status = dataStore.registerUserWithStatus(username, password, name, phone);
        if ("SUCCESS".equals(status)) {
            JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.", "Account Created", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, status, "Registration Status", JOptionPane.WARNING_MESSAGE);
        }
    }
}
