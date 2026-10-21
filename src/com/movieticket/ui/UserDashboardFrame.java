package com.movieticket.ui;

import com.movieticket.model.Booking;
import com.movieticket.model.Movie;
import com.movieticket.model.User;
import com.movieticket.service.DataStore;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class UserDashboardFrame extends JFrame {
    private User currentUser;
    private DataStore dataStore = DataStore.getInstance();

    private JPanel moviesContainer;
    private JComboBox<String> genreFilterCombo;
    private JComboBox<String> languageFilterCombo;
    private JTextField searchField;

    private JTable bookingsTable;
    private DefaultTableModel bookingsTableModel;
    private JLabel bookingCountBadge;

    public UserDashboardFrame(User user) {
        super("CinePass Tamil Nadu - Movie Booking Experience");
        this.currentUser = user;

        setSize(1100, 780);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        // TOP NAVIGATION BAR
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(UITheme.BG_CARD);
        navBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(12, 24, 12, 24)
        ));

        // Brand Logo & Location badge
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandPanel.setOpaque(false);

        JLabel logoLbl = new JLabel("🎟 CINEPASS TN");
        logoLbl.setFont(UITheme.FONT_HEADER_LARGE);
        logoLbl.setForeground(UITheme.ACCENT_PRIMARY);

        JLabel locBadge = new JLabel(" 📍 Tamil Nadu (Chennai, CBE, MDU) ");
        locBadge.setFont(UITheme.FONT_SMALL_BOLD);
        locBadge.setOpaque(true);
        locBadge.setBackground(UITheme.BG_CARD_LIGHTER);
        locBadge.setForeground(UITheme.ACCENT_GOLD);
        locBadge.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        brandPanel.add(logoLbl);
        brandPanel.add(locBadge);

        // User profile & Logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel userLbl = new JLabel("👤 " + currentUser.getFullName() + " (" + currentUser.getUsername() + ")");
        userLbl.setFont(UITheme.FONT_BOLD);
        userLbl.setForeground(UITheme.TEXT_PRIMARY);

        JButton logoutBtn = UITheme.createSecondaryButton("Logout");
        logoutBtn.addActionListener(e -> logout());

        userPanel.add(userLbl);
        userPanel.add(logoutBtn);

        navBar.add(brandPanel, BorderLayout.WEST);
        navBar.add(userPanel, BorderLayout.EAST);
        add(navBar, BorderLayout.NORTH);

        // MAIN CONTENT TABBED PANE
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BOLD);
        tabbedPane.setBackground(UITheme.BG_CARD);
        tabbedPane.setForeground(UITheme.TEXT_PRIMARY);

        tabbedPane.addTab("  🎥 Movies Now Showing  ", createMoviesPanel());
        tabbedPane.addTab("  🎟 My Bookings & Tickets  ", createBookingsPanel());

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                loadUserBookings();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createMoviesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        // Filter and Search Toolbar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterBar.setBackground(UITheme.BG_CARD);
        filterBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        JLabel searchLbl = new JLabel("Search Movie:");
        searchLbl.setFont(UITheme.FONT_BOLD);
        searchLbl.setForeground(UITheme.TEXT_PRIMARY);

        searchField = UITheme.createTextField(14);
        searchField.putClientProperty("JTextField.placeholderText", "Title or actor...");

        JLabel genreLbl = new JLabel("Genre:");
        genreLbl.setFont(UITheme.FONT_BOLD);
        genreLbl.setForeground(UITheme.TEXT_PRIMARY);

        genreFilterCombo = UITheme.createComboBox(new String[]{"All Genres", "Action", "Drama", "Sci-Fi", "Fantasy"});

        JLabel langLbl = new JLabel("Language:");
        langLbl.setFont(UITheme.FONT_BOLD);
        langLbl.setForeground(UITheme.TEXT_PRIMARY);

        languageFilterCombo = UITheme.createComboBox(new String[]{"All Languages", "Tamil", "Tamil / Multi"});

        JButton searchBtn = UITheme.createPrimaryButton("Filter 🔍");
        searchBtn.addActionListener(e -> renderMoviesList());

        JButton resetBtn = UITheme.createSecondaryButton("Reset");
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            genreFilterCombo.setSelectedIndex(0);
            languageFilterCombo.setSelectedIndex(0);
            renderMoviesList();
        });

        filterBar.add(searchLbl);
        filterBar.add(searchField);
        filterBar.add(genreLbl);
        filterBar.add(genreFilterCombo);
        filterBar.add(langLbl);
        filterBar.add(languageFilterCombo);
        filterBar.add(searchBtn);
        filterBar.add(resetBtn);

        panel.add(filterBar, BorderLayout.NORTH);

        // Movies Grid Scrollable
        moviesContainer = new JPanel();
        moviesContainer.setLayout(new GridLayout(0, 3, 20, 20));
        moviesContainer.setBackground(UITheme.BG_DARK);

        JScrollPane scrollPane = new JScrollPane(moviesContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(UITheme.BG_DARK);
        scrollPane.getViewport().setBackground(UITheme.BG_DARK);

        panel.add(scrollPane, BorderLayout.CENTER);

        renderMoviesList();
        return panel;
    }

    private void renderMoviesList() {
        moviesContainer.removeAll();
        List<Movie> movies = dataStore.getAllMovies();

        String query = searchField.getText().trim().toLowerCase();
        String selectedGenre = (String) genreFilterCombo.getSelectedItem();
        String selectedLang = (String) languageFilterCombo.getSelectedItem();

        for (Movie m : movies) {
            // Check filters
            if (!query.isEmpty() && !m.getTitle().toLowerCase().contains(query) && !m.getDescription().toLowerCase().contains(query)) {
                continue;
            }
            if (selectedGenre != null && !selectedGenre.equals("All Genres") && !m.getGenre().contains(selectedGenre)) {
                continue;
            }
            if (selectedLang != null && !selectedLang.equals("All Languages") && !m.getLanguage().equalsIgnoreCase(selectedLang)) {
                continue;
            }

            MovieCardPanel card = new MovieCardPanel(m, e -> openSeatBooking(m));
            moviesContainer.add(card);
        }

        if (moviesContainer.getComponentCount() == 0) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setOpaque(false);
            JLabel emptyLbl = new JLabel("No movies match your filter criteria.");
            emptyLbl.setFont(UITheme.FONT_SUBHEADER);
            emptyLbl.setForeground(UITheme.TEXT_MUTED);
            emptyPanel.add(emptyLbl);
            moviesContainer.add(emptyPanel);
        }

        moviesContainer.revalidate();
        moviesContainer.repaint();
    }

    private void openSeatBooking(Movie movie) {
        SeatBookingDialog dialog = new SeatBookingDialog(this, movie, currentUser.getUsername());
        dialog.setVisible(true);
        // Refresh bookings after dialog closes
        loadUserBookings();
    }

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);

        JLabel headerLbl = new JLabel("🎟 Your Booked Movie Tickets");
        headerLbl.setFont(UITheme.FONT_HEADER);
        headerLbl.setForeground(Color.WHITE);

        bookingCountBadge = new JLabel("0 Bookings");
        bookingCountBadge.setFont(UITheme.FONT_BOLD);
        bookingCountBadge.setForeground(UITheme.ACCENT_GOLD);

        topHeader.add(headerLbl, BorderLayout.WEST);
        topHeader.add(bookingCountBadge, BorderLayout.EAST);
        panel.add(topHeader, BorderLayout.NORTH);

        String[] cols = {"Booking ID", "Movie Title", "Theatre", "Showtime", "Date", "Seats", "Total Paid (₹)", "Booked On"};
        bookingsTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(bookingsTableModel);
        bookingsTable.setFont(UITheme.FONT_REGULAR);
        bookingsTable.setRowHeight(32);
        bookingsTable.setBackground(UITheme.BG_CARD);
        bookingsTable.setForeground(UITheme.TEXT_PRIMARY);
        bookingsTable.setSelectionBackground(UITheme.BG_CARD_LIGHTER);
        bookingsTable.setSelectionForeground(Color.WHITE);
        bookingsTable.setGridColor(UITheme.BORDER_COLOR);

        JTableHeader th = bookingsTable.getTableHeader();
        th.setFont(UITheme.FONT_BOLD);
        th.setBackground(new Color(28, 34, 52));
        th.setForeground(UITheme.TEXT_PRIMARY);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 36));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        bookingsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        bookingsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        bookingsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        bookingsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        bookingsTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scrollPane.getViewport().setBackground(UITheme.BG_CARD);

        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions (e.g., View Ticket Details)
        JPanel bottomAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomAction.setOpaque(false);

        JButton viewTicketBtn = UITheme.createPrimaryButton("View Ticket Details 📄");
        viewTicketBtn.addActionListener(e -> showTicketDetailsDialog());

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadUserBookings());

        bottomAction.add(refreshBtn);
        bottomAction.add(viewTicketBtn);
        panel.add(bottomAction, BorderLayout.SOUTH);

        loadUserBookings();
        return panel;
    }

    private void showTicketDetailsDialog() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking from the table to view ticket details.", "Select Booking", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookingId = (String) bookingsTableModel.getValueAt(selectedRow, 0);
        String movieTitle = (String) bookingsTableModel.getValueAt(selectedRow, 1);
        String theatre = (String) bookingsTableModel.getValueAt(selectedRow, 2);
        String showtime = (String) bookingsTableModel.getValueAt(selectedRow, 3);
        String date = (String) bookingsTableModel.getValueAt(selectedRow, 4);
        String seats = (String) bookingsTableModel.getValueAt(selectedRow, 5);
        String totalPaid = String.valueOf(bookingsTableModel.getValueAt(selectedRow, 6));
        String bookedOn = (String) bookingsTableModel.getValueAt(selectedRow, 7);

        String ticketInfo = String.format(
            "=========================================\n" +
            "       🎟 CINEPASS TAMIL NADU E-TICKET    \n" +
            "=========================================\n" +
            "Booking ID   : %s\n" +
            "Customer     : %s\n" +
            "Movie        : %s\n" +
            "Theatre      : %s\n" +
            "Showtime     : %s (%s)\n" +
            "Seats Booked : %s\n" +
            "Total Amount : ₹%s\n" +
            "Booked On    : %s\n" +
            "Status       : CONFIRMED & VALID ENTRY\n" +
            "=========================================\n" +
            "Note: Please arrive 15 minutes before showtime.\n" +
            "Enjoy the movie!",
            bookingId, currentUser.getFullName(), movieTitle, theatre, showtime, date, seats, totalPaid, bookedOn
        );

        JTextArea textArea = new JTextArea(ticketInfo);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setBackground(new Color(24, 28, 42));
        textArea.setForeground(Color.WHITE);
        textArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "E-Ticket Preview - " + bookingId, JOptionPane.PLAIN_MESSAGE);
    }

    private void loadUserBookings() {
        bookingsTableModel.setRowCount(0);
        List<Booking> list = dataStore.getUserBookings(currentUser.getUsername());

        for (Booking b : list) {
            bookingsTableModel.addRow(new Object[]{
                b.getBookingId(),
                b.getMovieTitle(),
                b.getTheatre(),
                b.getShowtime(),
                b.getDate(),
                String.join(", ", b.getSeatNumbers()),
                String.format("%.2f", b.getTotalAmount()),
                b.getBookingTime()
            });
        }
        bookingCountBadge.setText(list.size() + " Total Bookings");
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
