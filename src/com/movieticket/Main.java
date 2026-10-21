package com.movieticket;

import com.movieticket.ui.LoginFrame;

import javax.swing.*;

//d:\Jerusha\aiasst\run.bat  (for running)

public class Main {
    public static void main(String[] args) {
        // Setup clean cross-platform or dark styling to avoid OS white button painting
        try {
            // Apply Nimbus or default cross platform with dark background overrides
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Button.opaque", Boolean.TRUE);
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
