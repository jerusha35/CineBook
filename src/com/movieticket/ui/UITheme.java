package com.movieticket.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class UITheme {
    // Elegant Dark Cinema Theme Palette
    public static final Color BG_DARK = new Color(15, 17, 26);
    public static final Color BG_CARD = new Color(24, 28, 42);
    public static final Color BG_CARD_LIGHTER = new Color(38, 45, 68);
    public static final Color BG_INPUT = new Color(20, 24, 36);

    public static final Color BG_SEAT_AVAILABLE = new Color(45, 55, 78);
    public static final Color BG_SEAT_HOVER = new Color(60, 75, 105);
    public static final Color ACCENT_PRIMARY = new Color(229, 9, 20); // Cinema Red / Crimson
    public static final Color ACCENT_PRIMARY_HOVER = new Color(255, 35, 45);
    public static final Color ACCENT_GOLD = new Color(245, 197, 24);   // Rating / Highlights
    public static final Color ACCENT_CYAN = new Color(56, 189, 248);   // Customer info
    public static final Color ACCENT_GREEN = new Color(46, 204, 113);  // Selected / Success
    public static final Color ACCENT_BOOKED = new Color(70, 78, 95);   // Disabled/Booked

    public static final Color TEXT_PRIMARY = new Color(245, 245, 250);
    public static final Color TEXT_MUTED = new Color(156, 163, 175);
    public static final Color BORDER_COLOR = new Color(55, 65, 90);

    // Modern Fonts - with fallback safely supporting Unicode emojis
    public static final Font FONT_HEADER_LARGE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 12);

    /**
     * Primary action button with Crimson Red background, pure white bold text, and hover effect.
     */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT_PRIMARY);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 24, 40));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT_PRIMARY_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT_PRIMARY);
            }
        });
        return btn;
    }

    /**
     * Secondary button with rich dark-slate background, crisp white text, and clear border.
     */
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(BG_CARD_LIGHTER);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(55, 65, 95));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(BG_CARD_LIGHTER);
            }
        });
        return btn;
    }

    /**
     * Custom badge/action button for pills, quick switches and chips.
     */
    public static JButton createPillButton(String text, Color textColor) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_SMALL_BOLD);
        btn.setForeground(textColor);
        btn.setBackground(new Color(30, 36, 54));
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 72, 100), 1),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(45, 54, 80));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(30, 36, 54));
            }
        });
        return btn;
    }

    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_REGULAR);
        tf.setForeground(TEXT_PRIMARY);
        tf.setBackground(BG_INPUT);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return tf;
    }

    public static JPasswordField createPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setFont(FONT_REGULAR);
        pf.setForeground(TEXT_PRIMARY);
        pf.setBackground(BG_INPUT);
        pf.setCaretColor(TEXT_PRIMARY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return pf;
    }

    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setFont(FONT_REGULAR);
        cb.setBackground(BG_CARD_LIGHTER);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFocusable(false);
        return cb;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }
}
