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
import java.util.Arrays;
import java.util.List;

public class AdminDashboardFrame extends JFrame {
    private User adminUser;
    private DataStore dataStore = DataStore.getInstance();

    private JTable moviesTable;
    private DefaultTableModel moviesTableModel;

    private JTable allBookingsTable;
    private DefaultTableModel allBookingsTableModel;

    private JLabel totalRevenueLabel;
    private JLabel totalBookingsLabel;
    private JLabel totalMoviesLabel;

    public AdminDashboardFrame(User adminUser) {
        super("CinePass Tamil Nadu - Administrator Control Panel");
        this.adminUser = adminUser;

        setSize(1100, 750);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        // TOP ADMIN HEADER
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(UITheme.BG_CARD);
        navBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(12, 24, 12, 24)
        ));

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandPanel.setOpaque(false);

        JLabel logoLbl = new JLabel("⚙️ CINEPASS ADMIN CONSOLE");
        logoLbl.setFont(UITheme.FONT_HEADER);
        logoLbl.setForeground(UITheme.ACCENT_PRIMARY);

        JLabel roleBadge = new JLabel(" SUPER ADMIN PRIVILEGES ");
        roleBadge.setFont(UITheme.FONT_SMALL_BOLD);
        roleBadge.setOpaque(true);
        roleBadge.setBackground(UITheme.ACCENT_PRIMARY);
        roleBadge.setForeground(Color.WHITE);

        brandPanel.add(logoLbl);
        brandPanel.add(roleBadge);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel userLbl = new JLabel("Logged in as: " + adminUser.getFullName());
        userLbl.setFont(UITheme.FONT_BOLD);
        userLbl.setForeground(UITheme.TEXT_PRIMARY);

        JButton logoutBtn = UITheme.createSecondaryButton("Logout");
        logoutBtn.addActionListener(e -> logout());

        userPanel.add(userLbl);
        userPanel.add(logoutBtn);

        navBar.add(brandPanel, BorderLayout.WEST);
        navBar.add(userPanel, BorderLayout.EAST);
        add(navBar, BorderLayout.NORTH);

        // TABBED PANE FOR MANAGEMENT
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BOLD);
        tabbedPane.setBackground(UITheme.BG_CARD);
        tabbedPane.setForeground(UITheme.TEXT_PRIMARY);

        tabbedPane.addTab("  🎬 Manage Tamil Nadu Movies  ", createManageMoviesPanel());
        tabbedPane.addTab("  📊 All Customer Bookings & Sales  ", createManageBookingsPanel());

        tabbedPane.addChangeListener(e -> {
            loadMovies();
            loadAllBookings();
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createManageMoviesPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLbl = new JLabel("Now Showing Catalogue Management");
        titleLbl.setFont(UITheme.FONT_HEADER);
        titleLbl.setForeground(Color.WHITE);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnGroup.setOpaque(false);

        JButton addMovieBtn = UITheme.createPrimaryButton("+ Add New Movie");
        addMovieBtn.addActionListener(e -> showAddMovieDialog());

        JButton deleteMovieBtn = UITheme.createSecondaryButton("Delete Selected");
        deleteMovieBtn.addActionListener(e -> deleteSelectedMovie());

        btnGroup.add(addMovieBtn);
        btnGroup.add(deleteMovieBtn);

        headerPanel.add(titleLbl, BorderLayout.WEST);
        headerPanel.add(btnGroup, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Movie Table
        String[] cols = {"ID", "Movie Title", "Language", "Genre", "Duration", "Rating", "Certificate", "Price (₹)"};
        moviesTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        moviesTable = new JTable(moviesTableModel);
        moviesTable.setFont(UITheme.FONT_REGULAR);
        moviesTable.setRowHeight(32);
        moviesTable.setBackground(UITheme.BG_CARD);
        moviesTable.setForeground(UITheme.TEXT_PRIMARY);
        moviesTable.setSelectionBackground(UITheme.BG_CARD_LIGHTER);
        moviesTable.setSelectionForeground(Color.WHITE);
        moviesTable.setGridColor(UITheme.BORDER_COLOR);

        JTableHeader th = moviesTable.getTableHeader();
        th.setFont(UITheme.FONT_BOLD);
        th.setBackground(new Color(28, 34, 52));
        th.setForeground(UITheme.TEXT_PRIMARY);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 36));

        JScrollPane scrollPane = new JScrollPane(moviesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        panel.add(scrollPane, BorderLayout.CENTER);

        loadMovies();
        return panel;
    }

    private void showAddMovieDialog() {
        JDialog dialog = new JDialog(this, "Add New Tamil Nadu Movie", true);
        dialog.setSize(520, 600);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BG_CARD);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 12));
        formPanel.setBackground(UITheme.BG_CARD);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JTextField idField = UITheme.createTextField(10);
        idField.setText("M00" + (dataStore.getAllMovies().size() + 1));
        JTextField titleField = UITheme.createTextField(15);
        JTextField langField = UITheme.createTextField(15);
        langField.setText("Tamil");
        JTextField genreField = UITheme.createTextField(15);
        genreField.setText("Action / Thriller");
        JTextField durationField = UITheme.createTextField(15);
        durationField.setText("2h 30m");
        JTextField ratingField = UITheme.createTextField(15);
        ratingField.setText("8.5");
        JTextField certField = UITheme.createTextField(15);
        certField.setText("U/A");
        JTextField priceField = UITheme.createTextField(15);
        priceField.setText("190.00");

        addFormField(formPanel, "Movie ID:", idField);
        addFormField(formPanel, "Movie Title:", titleField);
        addFormField(formPanel, "Language:", langField);
        addFormField(formPanel, "Genre:", genreField);
        addFormField(formPanel, "Duration:", durationField);
        addFormField(formPanel, "Rating (1-10):", ratingField);
        addFormField(formPanel, "Certificate (U/UA/A):", certField);
        addFormField(formPanel, "Ticket Price (₹):", priceField);

        JPanel bottomBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottomBtnPanel.setOpaque(false);

        JButton saveBtn = UITheme.createPrimaryButton("Save Movie");
        saveBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            String lang = langField.getText().trim();
            String genre = genreField.getText().trim();
            String duration = durationField.getText().trim();
            String ratingStr = ratingField.getText().trim();
            String cert = certField.getText().trim();
            String priceStr = priceField.getText().trim();

            if (id.isEmpty() || title.isEmpty() || lang.isEmpty() || ratingStr.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double rating = Double.parseDouble(ratingStr);
                double price = Double.parseDouble(priceStr);

                List<String> defaultShowtimes = Arrays.asList("10:30 AM", "02:15 PM", "06:30 PM", "10:00 PM");
                List<String> defaultTheatres = Arrays.asList(
                    "SPI Sathyam Cinemas - Chennai",
                    "Rohini Silver Screens - Chennai",
                    "Broadway Cinemas - Coimbatore"
                );

                Movie newMovie = new Movie(id, title, lang, genre, duration, rating, cert, 
                    "Latest Tamil Nadu release currently running in top theatres.", 
                    defaultShowtimes, defaultTheatres, price);

                dataStore.addMovie(newMovie);
                JOptionPane.showMessageDialog(dialog, "Movie added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMovies();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid rating or price number format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        bottomBtnPanel.add(cancelBtn);
        bottomBtnPanel.add(saveBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(bottomBtnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addFormField(JPanel panel, String labelText, JComponent component) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(lbl);
        panel.add(component);
    }

    private void deleteSelectedMovie() {
        int selectedRow = moviesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a movie to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String movieId = (String) moviesTableModel.getValueAt(selectedRow, 0);
        String movieTitle = (String) moviesTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to remove '" + movieTitle + "' from listing?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            dataStore.deleteMovie(movieId);
            loadMovies();
            JOptionPane.showMessageDialog(this, "Movie deleted successfully.", "Removed", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadMovies() {
        moviesTableModel.setRowCount(0);
        List<Movie> list = dataStore.getAllMovies();
        for (Movie m : list) {
            moviesTableModel.addRow(new Object[]{
                m.getId(),
                m.getTitle(),
                m.getLanguage(),
                m.getGenre(),
                m.getDuration(),
                m.getRating(),
                m.getCertificate(),
                String.format("%.2f", m.getTicketPrice())
            });
        }
    }

    private JPanel createManageBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Analytics KPI Cards Row
        JPanel kpiContainer = new JPanel(new GridLayout(1, 3, 20, 0));
        kpiContainer.setOpaque(false);

        totalRevenueLabel = new JLabel("₹0.00");
        totalBookingsLabel = new JLabel("0");
        totalMoviesLabel = new JLabel("0");

        kpiContainer.add(createKpiCard("💰 Total Revenue", totalRevenueLabel, UITheme.ACCENT_GREEN));
        kpiContainer.add(createKpiCard("🎟 Bookings Recorded", totalBookingsLabel, UITheme.ACCENT_GOLD));
        kpiContainer.add(createKpiCard("🎥 Active Movies", totalMoviesLabel, new Color(59, 130, 246)));

        panel.add(kpiContainer, BorderLayout.NORTH);

        // Bookings Table
        String[] cols = {"Booking ID", "Customer User", "Movie Title", "Theatre", "Showtime", "Date", "Seats", "Amount (₹)", "Timestamp"};
        allBookingsTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        allBookingsTable = new JTable(allBookingsTableModel);
        allBookingsTable.setFont(UITheme.FONT_REGULAR);
        allBookingsTable.setRowHeight(32);
        allBookingsTable.setBackground(UITheme.BG_CARD);
        allBookingsTable.setForeground(UITheme.TEXT_PRIMARY);
        allBookingsTable.setSelectionBackground(UITheme.BG_CARD_LIGHTER);
        allBookingsTable.setSelectionForeground(Color.WHITE);
        allBookingsTable.setGridColor(UITheme.BORDER_COLOR);

        JTableHeader th = allBookingsTable.getTableHeader();
        th.setFont(UITheme.FONT_BOLD);
        th.setBackground(new Color(28, 34, 52));
        th.setForeground(UITheme.TEXT_PRIMARY);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 36));

        JScrollPane scrollPane = new JScrollPane(allBookingsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        panel.add(scrollPane, BorderLayout.CENTER);

        loadAllBookings();
        return panel;
    }

    private JPanel createKpiCard(String title, JLabel valueLbl, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SMALL_BOLD);
        titleLbl.setForeground(UITheme.TEXT_MUTED);

        valueLbl.setFont(UITheme.FONT_HEADER_LARGE);
        valueLbl.setForeground(accentColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        return card;
    }

    private void loadAllBookings() {
        allBookingsTableModel.setRowCount(0);
        List<Booking> list = dataStore.getAllBookings();
        double totalRev = 0;

        for (Booking b : list) {
            totalRev += b.getTotalAmount();
            allBookingsTableModel.addRow(new Object[]{
                b.getBookingId(),
                b.getUsername(),
                b.getMovieTitle(),
                b.getTheatre(),
                b.getShowtime(),
                b.getDate(),
                String.join(", ", b.getSeatNumbers()),
                String.format("%.2f", b.getTotalAmount()),
                b.getBookingTime()
            });
        }

        totalRevenueLabel.setText("₹" + String.format("%.2f", totalRev));
        totalBookingsLabel.setText(String.valueOf(list.size()));
        totalMoviesLabel.setText(String.valueOf(dataStore.getAllMovies().size()));
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out of Admin Console?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
