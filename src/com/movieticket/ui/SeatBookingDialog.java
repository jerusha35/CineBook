package com.movieticket.ui;

import com.movieticket.model.Movie;
import com.movieticket.service.DataStore;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SeatBookingDialog extends JDialog {
    private Movie movie;
    private String currentUsername;
    private DataStore dataStore = DataStore.getInstance();

    private JComboBox<String> theatreCombo;
    private JComboBox<String> showtimeCombo;
    private JComboBox<String> dateCombo;
    private JPanel seatGridPanel;
    private JLabel totalAmountLabel;
    private JLabel selectedSeatsLabel;
    private JButton confirmBookingBtn;

    // Grid configuration: 6 rows (A-F), 10 columns (1-10) with an aisle between 5 and 6
    private static final int ROWS = 6;
    private static final int COLS = 10;
    private static final char[] ROW_LETTERS = {'A', 'B', 'C', 'D', 'E', 'F'};

    private Set<String> currentBookedSeats = new HashSet<>();
    private Set<String> currentlySelectedSeats = new LinkedHashSet<>();
    private Map<String, JButton> seatButtonMap = new HashMap<>();

    public SeatBookingDialog(JFrame parent, Movie movie, String currentUsername) {
        super(parent, "Select Seats - " + movie.getTitle(), true);
        this.movie = movie;
        this.currentUsername = currentUsername;

        setSize(850, 720);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        initUI();
        refreshSeatMap();
    }

    private void initUI() {
        // TOP: Movie Header & Selection Filters
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(UITheme.BG_CARD);
        topContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLbl = new JLabel("🎬 " + movie.getTitle() + " (" + movie.getLanguage() + " • " + movie.getCertificate() + ")");
        titleLbl.setFont(UITheme.FONT_HEADER);
        titleLbl.setForeground(Color.WHITE);

        JLabel priceLbl = new JLabel("Ticket Price: ₹" + (int)movie.getTicketPrice() + " per seat | Tamil Nadu Multiplex Experience");
        priceLbl.setFont(UITheme.FONT_SMALL);
        priceLbl.setForeground(UITheme.ACCENT_GOLD);

        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filtersPanel.setOpaque(false);

        // Theatre Selector
        JLabel thLbl = new JLabel("Theatre:");
        thLbl.setFont(UITheme.FONT_BOLD);
        thLbl.setForeground(UITheme.TEXT_PRIMARY);
        theatreCombo = new JComboBox<>(movie.getTheatres().toArray(new String[0]));
        theatreCombo.setFont(UITheme.FONT_REGULAR);
        theatreCombo.setBackground(UITheme.BG_CARD_LIGHTER);
        theatreCombo.setForeground(UITheme.TEXT_PRIMARY);

        // Date Selector
        JLabel dtLbl = new JLabel("Date:");
        dtLbl.setFont(UITheme.FONT_BOLD);
        dtLbl.setForeground(UITheme.TEXT_PRIMARY);
        String[] dates = {"Today", "Tomorrow", "Day After Tomorrow"};
        dateCombo = new JComboBox<>(dates);
        dateCombo.setFont(UITheme.FONT_REGULAR);
        dateCombo.setBackground(UITheme.BG_CARD_LIGHTER);
        dateCombo.setForeground(UITheme.TEXT_PRIMARY);

        // Showtime Selector
        JLabel stLbl = new JLabel("Showtime:");
        stLbl.setFont(UITheme.FONT_BOLD);
        stLbl.setForeground(UITheme.TEXT_PRIMARY);
        showtimeCombo = new JComboBox<>(movie.getShowtimes().toArray(new String[0]));
        showtimeCombo.setFont(UITheme.FONT_REGULAR);
        showtimeCombo.setBackground(UITheme.BG_CARD_LIGHTER);
        showtimeCombo.setForeground(UITheme.TEXT_PRIMARY);

        filtersPanel.add(thLbl);
        filtersPanel.add(theatreCombo);
        filtersPanel.add(dtLbl);
        filtersPanel.add(dateCombo);
        filtersPanel.add(stLbl);
        filtersPanel.add(showtimeCombo);

        // Listener to refresh seat occupancy when criteria changes
        theatreCombo.addActionListener(e -> refreshSeatMap());
        dateCombo.addActionListener(e -> refreshSeatMap());
        showtimeCombo.addActionListener(e -> refreshSeatMap());

        topContainer.add(titleLbl);
        topContainer.add(Box.createVerticalStrut(4));
        topContainer.add(priceLbl);
        topContainer.add(Box.createVerticalStrut(8));
        topContainer.add(filtersPanel);

        add(topContainer, BorderLayout.NORTH);

        // CENTER: Seat Grid & Cinema Screen Visualizer
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(UITheme.BG_DARK);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        // Screen Curve Graphic Banner
        JPanel screenBanner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 140, 230));
                g2.setStroke(new BasicStroke(4));
                int w = getWidth();
                g2.drawArc(40, 10, w - 80, 40, 20, 140);

                g2.setColor(new Color(60, 140, 230, 40));
                g2.fillArc(40, 10, w - 80, 40, 20, 140);
            }
        };
        screenBanner.setPreferredSize(new Dimension(600, 50));
        screenBanner.setOpaque(false);
        screenBanner.setLayout(new BorderLayout());

        JLabel screenText = new JLabel("CINEMA SCREEN (EYES THIS WAY)", SwingConstants.CENTER);
        screenText.setFont(UITheme.FONT_SMALL_BOLD);
        screenText.setForeground(new Color(147, 197, 253));
        screenBanner.add(screenText, BorderLayout.CENTER);

        centerPanel.add(screenBanner, BorderLayout.NORTH);

        // Seat Grid Panel
        seatGridPanel = new JPanel(new GridLayout(ROWS, COLS + 1, 8, 8)); // +1 for aisle gap
        seatGridPanel.setBackground(UITheme.BG_DARK);
        seatGridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        buildSeatButtons();
        centerPanel.add(seatGridPanel, BorderLayout.CENTER);

        // Seat Legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 5));
        legendPanel.setOpaque(false);
        legendPanel.add(createLegendItem(UITheme.BG_SEAT_AVAILABLE, "Available"));
        legendPanel.add(createLegendItem(UITheme.ACCENT_GREEN, "Selected"));
        legendPanel.add(createLegendItem(UITheme.ACCENT_BOOKED, "Sold Out / Booked"));

        centerPanel.add(legendPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // BOTTOM: Booking Summary & Action
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UITheme.BG_CARD);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setOpaque(false);

        selectedSeatsLabel = new JLabel("Selected Seats: None");
        selectedSeatsLabel.setFont(UITheme.FONT_BOLD);
        selectedSeatsLabel.setForeground(UITheme.TEXT_PRIMARY);

        totalAmountLabel = new JLabel("Total Amount: ₹0.00");
        totalAmountLabel.setFont(UITheme.FONT_HEADER);
        totalAmountLabel.setForeground(UITheme.ACCENT_GOLD);

        summaryPanel.add(selectedSeatsLabel);
        summaryPanel.add(Box.createVerticalStrut(4));
        summaryPanel.add(totalAmountLabel);

        confirmBookingBtn = UITheme.createPrimaryButton("Confirm & Pay 💳");
        confirmBookingBtn.setPreferredSize(new Dimension(180, 44));
        confirmBookingBtn.setEnabled(false);
        confirmBookingBtn.addActionListener(e -> processBooking());

        bottomPanel.add(summaryPanel, BorderLayout.WEST);
        bottomPanel.add(confirmBookingBtn, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createLegendItem(Color color, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(16, 16));
        box.setBackground(color);
        box.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_MUTED);
        p.add(box);
        p.add(lbl);
        return p;
    }

    private void buildSeatButtons() {
        seatGridPanel.removeAll();
        seatButtonMap.clear();

        for (int r = 0; r < ROWS; r++) {
            char rowChar = ROW_LETTERS[r];
            for (int c = 1; c <= COLS; c++) {
                if (c == 6) {
                    // Visual walkway aisle
                    JLabel aisle = new JLabel("", SwingConstants.CENTER);
                    seatGridPanel.add(aisle);
                }

                String seatId = rowChar + String.valueOf(c);
                JButton seatBtn = new JButton(seatId);
                seatBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                seatBtn.setContentAreaFilled(false);
                seatBtn.setOpaque(true);
                seatBtn.setFocusPainted(false);
                seatBtn.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));
                seatBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                seatBtn.addActionListener(e -> toggleSeatSelection(seatId, seatBtn));
                seatButtonMap.put(seatId, seatBtn);
                seatGridPanel.add(seatBtn);
            }
        }
    }

    private void refreshSeatMap() {
        currentlySelectedSeats.clear();
        String theatre = (String) theatreCombo.getSelectedItem();
        String showtime = (String) showtimeCombo.getSelectedItem();
        String date = (String) dateCombo.getSelectedItem();

        if (theatre == null || showtime == null || date == null) return;

        currentBookedSeats = dataStore.getBookedSeats(movie.getTitle(), theatre, showtime, date);

        for (Map.Entry<String, JButton> entry : seatButtonMap.entrySet()) {
            String seatId = entry.getKey();
            JButton btn = entry.getValue();

            if (currentBookedSeats.contains(seatId)) {
                btn.setBackground(UITheme.ACCENT_BOOKED);
                btn.setForeground(Color.LIGHT_GRAY);
                btn.setEnabled(false);
                btn.setToolTipText(seatId + " (Already Booked)");
            } else {
                btn.setBackground(UITheme.BG_SEAT_AVAILABLE);
                btn.setForeground(Color.WHITE);
                btn.setEnabled(true);
                btn.setToolTipText(seatId + " (Available - ₹" + (int)movie.getTicketPrice() + ")");
            }
        }

        updateSummary();
    }

    private void toggleSeatSelection(String seatId, JButton btn) {
        if (currentlySelectedSeats.contains(seatId)) {
            currentlySelectedSeats.remove(seatId);
            btn.setBackground(UITheme.BG_SEAT_AVAILABLE);
            btn.setForeground(Color.WHITE);
        } else {
            currentlySelectedSeats.add(seatId);
            btn.setBackground(UITheme.ACCENT_GREEN);
            btn.setForeground(Color.BLACK);
        }
        updateSummary();
    }

    private void updateSummary() {
        if (currentlySelectedSeats.isEmpty()) {
            selectedSeatsLabel.setText("Selected Seats: None");
            totalAmountLabel.setText("Total Amount: ₹0.00");
            confirmBookingBtn.setEnabled(false);
        } else {
            selectedSeatsLabel.setText("Selected (" + currentlySelectedSeats.size() + "): " + String.join(", ", currentlySelectedSeats));
            double total = currentlySelectedSeats.size() * movie.getTicketPrice();
            totalAmountLabel.setText("Total Amount: ₹" + String.format("%.2f", total));
            confirmBookingBtn.setEnabled(true);
        }
    }

    private void processBooking() {
        String theatre = (String) theatreCombo.getSelectedItem();
        String showtime = (String) showtimeCombo.getSelectedItem();
        String date = (String) dateCombo.getSelectedItem();

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Confirm booking for " + currentlySelectedSeats.size() + " seat(s) at " + theatre + "?\n" +
            "Movie: " + movie.getTitle() + "\n" +
            "Seats: " + String.join(", ", currentlySelectedSeats) + "\n" +
            "Total: " + totalAmountLabel.getText(),
            "Confirm Booking",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = dataStore.bookSeats(
                movie.getTitle(),
                theatre,
                showtime,
                date,
                new ArrayList<>(currentlySelectedSeats),
                currentUsername,
                movie.getTicketPrice()
            );

            if (success) {
                JOptionPane.showMessageDialog(
                    this,
                    "🎉 Booking Confirmed!\n" +
                    "Your e-Ticket has been generated in your account.\n" +
                    "Enjoy the show at " + theatre + "!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "One or more selected seats were just taken! Please reselect seats.",
                    "Booking Error",
                    JOptionPane.ERROR_MESSAGE
                );
                refreshSeatMap();
            }
        }
    }
}
