package com.movieticket.ui;

import com.movieticket.model.Movie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MovieCardPanel extends JPanel {
    private Movie movie;

    public MovieCardPanel(Movie movie, ActionListener onBookAction) {
        this.movie = movie;
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        // Poster Simulation Header Banner
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(new Color(30, 36, 54));
        bannerPanel.setPreferredSize(new Dimension(240, 110));
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel titleLbl = new JLabel("<html><body style='width: 200px;'><b>" + movie.getTitle() + "</b></body></html>");
        titleLbl.setFont(UITheme.FONT_SUBHEADER);
        titleLbl.setForeground(Color.WHITE);

        JLabel certBadge = new JLabel(" " + movie.getCertificate() + " ");
        certBadge.setFont(UITheme.FONT_SMALL_BOLD);
        certBadge.setOpaque(true);
        certBadge.setBackground(UITheme.ACCENT_PRIMARY);
        certBadge.setForeground(Color.WHITE);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(certBadge, BorderLayout.EAST);

        bannerPanel.add(topRow, BorderLayout.NORTH);
        bannerPanel.add(titleLbl, BorderLayout.CENTER);

        // Details Panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JLabel ratingLbl = new JLabel("⭐ " + movie.getRating() + "/10  •  " + movie.getLanguage());
        ratingLbl.setFont(UITheme.FONT_SMALL_BOLD);
        ratingLbl.setForeground(UITheme.ACCENT_GOLD);

        JLabel genreLbl = new JLabel("🎭 " + movie.getGenre());
        genreLbl.setFont(UITheme.FONT_SMALL);
        genreLbl.setForeground(UITheme.TEXT_MUTED);

        JLabel durationLbl = new JLabel("⏱ " + movie.getDuration() + "  •  ₹" + (int)movie.getTicketPrice() + "/seat");
        durationLbl.setFont(UITheme.FONT_SMALL);
        durationLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel descLbl = new JLabel("<html><body style='width: 220px; color: #9ca3af; font-size: 11px;'>" 
                                    + movie.getDescription() + "</body></html>");

        detailsPanel.add(ratingLbl);
        detailsPanel.add(Box.createVerticalStrut(4));
        detailsPanel.add(genreLbl);
        detailsPanel.add(Box.createVerticalStrut(4));
        detailsPanel.add(durationLbl);
        detailsPanel.add(Box.createVerticalStrut(8));
        detailsPanel.add(descLbl);

        // Action Panel
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);

        JButton bookBtn = UITheme.createPrimaryButton("Book Ticket 🎟");
        bookBtn.addActionListener(onBookAction);

        actionPanel.add(bookBtn, BorderLayout.CENTER);

        add(bannerPanel, BorderLayout.NORTH);
        add(detailsPanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    public Movie getMovie() {
        return movie;
    }
}
