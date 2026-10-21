package com.movieticket.service;

import com.movieticket.model.Booking;
import com.movieticket.model.Movie;
import com.movieticket.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataStore {
    private static DataStore instance;

    private DataStore() {
        initDatabaseTables();
        seedDefaultDataIfEmpty();
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    private void initDatabaseTables() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Table: USERS
            try {
                stmt.execute(
                    "CREATE TABLE cinepass_users (" +
                    "  username VARCHAR2(50) PRIMARY KEY," +
                    "  password VARCHAR2(50) NOT NULL," +
                    "  full_name VARCHAR2(100) NOT NULL," +
                    "  phone VARCHAR2(20)," +
                    "  is_admin NUMBER(1) DEFAULT 0" +
                    ")"
                );
            } catch (SQLException e) {
                // Ignore ORA-00955: name is already used by an existing object
                if (e.getErrorCode() != 955) System.err.println("Users table create notice: " + e.getMessage());
            }

            // Table: MOVIES
            try {
                stmt.execute(
                    "CREATE TABLE cinepass_movies (" +
                    "  id VARCHAR2(20) PRIMARY KEY," +
                    "  title VARCHAR2(100) NOT NULL," +
                    "  language VARCHAR2(50)," +
                    "  genre VARCHAR2(100)," +
                    "  duration VARCHAR2(20)," +
                    "  rating NUMBER(3,1)," +
                    "  certificate VARCHAR2(10)," +
                    "  description VARCHAR2(500)," +
                    "  ticket_price NUMBER(8,2)" +
                    ")"
                );
            } catch (SQLException e) {
                if (e.getErrorCode() != 955) System.err.println("Movies table create notice: " + e.getMessage());
            }

            // Table: BOOKINGS
            try {
                stmt.execute(
                    "CREATE TABLE cinepass_bookings (" +
                    "  booking_id VARCHAR2(30) PRIMARY KEY," +
                    "  username VARCHAR2(50) NOT NULL," +
                    "  movie_title VARCHAR2(100) NOT NULL," +
                    "  theatre VARCHAR2(150) NOT NULL," +
                    "  showtime VARCHAR2(30) NOT NULL," +
                    "  show_date VARCHAR2(30) NOT NULL," +
                    "  seat_numbers VARCHAR2(200) NOT NULL," +
                    "  total_amount NUMBER(10,2) NOT NULL," +
                    "  booking_time VARCHAR2(50) NOT NULL" +
                    ")"
                );
            } catch (SQLException e) {
                if (e.getErrorCode() != 955) System.err.println("Bookings table create notice: " + e.getMessage());
            }

            // Table: BOOKED_SEATS (for fast seat occupancy checks)
            try {
                stmt.execute(
                    "CREATE TABLE cinepass_booked_seats (" +
                    "  movie_title VARCHAR2(100) NOT NULL," +
                    "  theatre VARCHAR2(150) NOT NULL," +
                    "  showtime VARCHAR2(30) NOT NULL," +
                    "  show_date VARCHAR2(30) NOT NULL," +
                    "  seat_id VARCHAR2(10) NOT NULL," +
                    "  booking_id VARCHAR2(30)," +
                    "  CONSTRAINT pk_booked_seats PRIMARY KEY (movie_title, theatre, showtime, show_date, seat_id)" +
                    ")"
                );
            } catch (SQLException e) {
                if (e.getErrorCode() != 955) System.err.println("Booked seats table create notice: " + e.getMessage());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void seedDefaultDataIfEmpty() {
        try (Connection conn = DBConnection.getConnection()) {
            // Check users
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM cinepass_users")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    // Seed admin & user
                    try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO cinepass_users (username, password, full_name, phone, is_admin) VALUES (?, ?, ?, ?, ?)")) {
                        ps.setString(1, "admin");
                        ps.setString(2, "admin");
                        ps.setString(3, "System Administrator");
                        ps.setString(4, "9840123456");
                        ps.setInt(5, 1);
                        ps.executeUpdate();

                        ps.setString(1, "user");
                        ps.setString(2, "user123");
                        ps.setString(3, "Karthik Rajan (Chennai)");
                        ps.setString(4, "9884198765");
                        ps.setInt(5, 0);
                        ps.executeUpdate();
                    }
                }
            }

            // Check movies
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM cinepass_movies")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    insertMovie(conn, new Movie("M001", "GOAT - The Greatest of All Time", "Tamil", "Action / Sci-Fi / Thriller", "2h 59m", 8.6, "U/A", "Thalapathy Vijay dual-action spectacle directed by Venkat Prabhu with Yuvan Shankar Raja musical.", null, null, 190.00));
                    insertMovie(conn, new Movie("M002", "Vettaiyan", "Tamil", "Action / Police Drama", "2h 45m", 8.4, "U/A", "Superstar Rajinikanth & Amitabh Bachchan powerhouse thriller directed by T.J. Gnanavel.", null, null, 200.00));
                    insertMovie(conn, new Movie("M003", "Amaran", "Tamil", "Biographical / Action / War", "2h 49m", 9.1, "U/A", "Sivakarthikeyan and Sai Pallavi in Major Mukund Varadarajan AC biopic. Inspiring blockbuster.", null, null, 180.00));
                    insertMovie(conn, new Movie("M004", "Kanguva", "Tamil / Multi", "Period Action / Fantasy", "2h 34m", 8.1, "U/A", "Suriya in a prehistoric epic dual avatar directed by Siva with DSP musical blast.", null, null, 190.00));
                    insertMovie(conn, new Movie("M005", "Meiyazhagan", "Tamil", "Heartwarming Drama / Family", "2h 40m", 8.9, "U", "Karthi & Arvind Swami in a soul-stirring tale set in Thanjavur, directed by C. Prem Kumar.", null, null, 160.00));
                    insertMovie(conn, new Movie("M006", "Leo (Special Show Re-Release)", "Tamil", "Action / LCU", "2h 44m", 8.8, "U/A", "Lokesh Kanagaraj cinematic universe riot featuring Thalapathy Vijay and Anirudh beats.", null, null, 220.00));

                    // Seed pre-booked seats
                    String[] defaultSeats = {"D4", "D5", "D6", "E4", "E5", "E6", "F7", "F8"};
                    String seedSql = "INSERT INTO cinepass_booked_seats (movie_title, theatre, showtime, show_date, seat_id, booking_id) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(seedSql)) {
                        for (String seat : defaultSeats) {
                            ps.setString(1, "GOAT - The Greatest of All Time");
                            ps.setString(2, "SPI Sathyam Cinemas - Royapettah, Chennai");
                            ps.setString(3, "06:30 PM");
                            ps.setString(4, "Today");
                            ps.setString(5, seat);
                            ps.setString(6, "SEED01");
                            ps.executeUpdate();
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertMovie(Connection conn, Movie m) throws SQLException {
        String sql = "INSERT INTO cinepass_movies (id, title, language, genre, duration, rating, certificate, description, ticket_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getId());
            ps.setString(2, m.getTitle());
            ps.setString(3, m.getLanguage());
            ps.setString(4, m.getGenre());
            ps.setString(5, m.getDuration());
            ps.setDouble(6, m.getRating());
            ps.setString(7, m.getCertificate());
            ps.setString(8, m.getDescription());
            ps.setDouble(9, m.getTicketPrice());
            ps.executeUpdate();
        }
    }

    public synchronized User authenticate(String username, String password) {
        String cleanUser = username.trim();
        String sql = "SELECT username, password, full_name, phone, is_admin FROM cinepass_users WHERE LOWER(username) = LOWER(?) AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cleanUser);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Authentication SUCCESS for user: " + rs.getString("username"));
                    return new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getInt("is_admin") == 1
                    );
                } else {
                    System.out.println("Authentication FAILED: No match for user: '" + cleanUser + "'");
                }
            }
        } catch (SQLException e) {
            System.err.println("Authentication DB error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public synchronized String registerUserWithStatus(String username, String password, String fullName, String phone) {
        String cleanUser = username.trim();
        if (cleanUser.isEmpty()) return "Username cannot be empty.";
        if (password.isEmpty()) return "Password cannot be empty.";

        // 1. Explicitly check if username already exists in Oracle DB
        String checkSql = "SELECT COUNT(*) FROM cinepass_users WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
            psCheck.setString(1, cleanUser);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Registration rejected: Username '" + cleanUser + "' already exists in DB.");
                    return "Username '" + cleanUser + "' is already registered in Oracle DB. Please choose another username.";
                }
            }
        } catch (SQLException e) {
            System.err.println("Database check error: " + e.getMessage());
            return "Database Error: " + e.getMessage();
        }

        // 2. Insert new user into Oracle DB
        String insertSql = "INSERT INTO cinepass_users (username, password, full_name, phone, is_admin) VALUES (?, ?, ?, ?, 0)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, cleanUser);
            ps.setString(2, password);
            ps.setString(3, fullName);
            ps.setString(4, phone);
            int rows = ps.executeUpdate();
            System.out.println("User '" + cleanUser + "' registered successfully into Oracle DB.");
            return rows > 0 ? "SUCCESS" : "Failed to insert user.";
        } catch (SQLException e) {
            System.err.println("Database insert error: " + e.getMessage());
            return "Database Insert Error: " + e.getMessage();
        }
    }

    public synchronized boolean registerUser(String username, String password, String fullName, String phone) {
        return "SUCCESS".equals(registerUserWithStatus(username, password, fullName, phone));
    }

    public synchronized List<Movie> getAllMovies() {
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT id, title, language, genre, duration, rating, certificate, description, ticket_price FROM cinepass_movies ORDER BY id ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<String> defaultShowtimes = Arrays.asList("10:30 AM", "02:15 PM", "06:30 PM", "10:00 PM");
            List<String> defaultTheatres = Arrays.asList(
                "SPI Sathyam Cinemas - Royapettah, Chennai",
                "Rohini Silver Screens - Koyambedu, Chennai",
                "Vettri Theatres - Chromepet, Chennai",
                "PVR Inox Luxe - Phoenix Marketcity, Velachery",
                "Broadway Cinemas - KMCH, Coimbatore",
                "Vetri Cinemas - Madurai"
            );

            while (rs.next()) {
                list.add(new Movie(
                    rs.getString("id"),
                    rs.getString("title"),
                    rs.getString("language"),
                    rs.getString("genre"),
                    rs.getString("duration"),
                    rs.getDouble("rating"),
                    rs.getString("certificate"),
                    rs.getString("description"),
                    defaultShowtimes,
                    defaultTheatres,
                    rs.getDouble("ticket_price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public synchronized void addMovie(Movie movie) {
        try (Connection conn = DBConnection.getConnection()) {
            insertMovie(conn, movie);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean deleteMovie(String movieId) {
        String sql = "DELETE FROM cinepass_movies WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, movieId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized Set<String> getBookedSeats(String movieTitle, String theatre, String showtime, String date) {
        Set<String> set = new HashSet<>();
        String sql = "SELECT seat_id FROM cinepass_booked_seats WHERE movie_title = ? AND theatre = ? AND showtime = ? AND show_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, movieTitle);
            ps.setString(2, theatre);
            ps.setString(3, showtime);
            ps.setString(4, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    set.add(rs.getString("seat_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return set;
    }

    public synchronized boolean bookSeats(String movieTitle, String theatre, String showtime, String date, 
                                          List<String> seatsToBook, String username, double pricePerTicket) {
        String countSql = "SELECT COUNT(*) FROM cinepass_bookings";
        int count = 0;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String bookingId = "TN" + (100000 + count + 1);
        String bookingTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a"));
        double total = seatsToBook.size() * pricePerTicket;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Check collisions
            String checkSql = "SELECT seat_id FROM cinepass_booked_seats WHERE movie_title = ? AND theatre = ? AND showtime = ? AND show_date = ? AND seat_id = ?";
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                for (String seat : seatsToBook) {
                    psCheck.setString(1, movieTitle);
                    psCheck.setString(2, theatre);
                    psCheck.setString(3, showtime);
                    psCheck.setString(4, date);
                    psCheck.setString(5, seat);
                    try (ResultSet rs = psCheck.executeQuery()) {
                        if (rs.next()) {
                            conn.rollback();
                            return false; // Already booked
                        }
                    }
                }
            }

            // 2. Insert into cinepass_booked_seats
            String seatInsertSql = "INSERT INTO cinepass_booked_seats (movie_title, theatre, showtime, show_date, seat_id, booking_id) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psSeat = conn.prepareStatement(seatInsertSql)) {
                for (String seat : seatsToBook) {
                    psSeat.setString(1, movieTitle);
                    psSeat.setString(2, theatre);
                    psSeat.setString(3, showtime);
                    psSeat.setString(4, date);
                    psSeat.setString(5, seat);
                    psSeat.setString(6, bookingId);
                    psSeat.executeUpdate();
                }
            }

            // 3. Insert into cinepass_bookings
            String bookingInsertSql = "INSERT INTO cinepass_bookings (booking_id, username, movie_title, theatre, showtime, show_date, seat_numbers, total_amount, booking_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psBooking = conn.prepareStatement(bookingInsertSql)) {
                psBooking.setString(1, bookingId);
                psBooking.setString(2, username);
                psBooking.setString(3, movieTitle);
                psBooking.setString(4, theatre);
                psBooking.setString(5, showtime);
                psBooking.setString(6, date);
                psBooking.setString(7, String.join(", ", seatsToBook));
                psBooking.setDouble(8, total);
                psBooking.setString(9, bookingTime);
                psBooking.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized List<Booking> getUserBookings(String username) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT booking_id, username, movie_title, theatre, showtime, show_date, seat_numbers, total_amount, booking_time FROM cinepass_bookings WHERE LOWER(username) = ? ORDER BY booking_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    List<String> seats = Arrays.asList(rs.getString("seat_numbers").split(",\\s*"));
                    list.add(new Booking(
                        rs.getString("booking_id"),
                        rs.getString("username"),
                        rs.getString("movie_title"),
                        rs.getString("theatre"),
                        rs.getString("showtime"),
                        rs.getString("show_date"),
                        seats,
                        rs.getDouble("total_amount"),
                        rs.getString("booking_time")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public synchronized List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT booking_id, username, movie_title, theatre, showtime, show_date, seat_numbers, total_amount, booking_time FROM cinepass_bookings ORDER BY booking_id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                List<String> seats = Arrays.asList(rs.getString("seat_numbers").split(",\\s*"));
                list.add(new Booking(
                    rs.getString("booking_id"),
                    rs.getString("username"),
                    rs.getString("movie_title"),
                    rs.getString("theatre"),
                    rs.getString("showtime"),
                    rs.getString("show_date"),
                    seats,
                    rs.getDouble("total_amount"),
                    rs.getString("booking_time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
