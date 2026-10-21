package com.movieticket.model;

import java.util.List;

public class Booking {
    private String bookingId;
    private String username;
    private String movieTitle;
    private String theatre;
    private String showtime;
    private String date;
    private List<String> seatNumbers;
    private double totalAmount;
    private String bookingTime;

    public Booking(String bookingId, String username, String movieTitle, String theatre, 
                   String showtime, String date, List<String> seatNumbers, double totalAmount, String bookingTime) {
        this.bookingId = bookingId;
        this.username = username;
        this.movieTitle = movieTitle;
        this.theatre = theatre;
        this.showtime = showtime;
        this.date = date;
        this.seatNumbers = seatNumbers;
        this.totalAmount = totalAmount;
        this.bookingTime = bookingTime;
    }

    public String getBookingId() { return bookingId; }
    public String getUsername() { return username; }
    public String getMovieTitle() { return movieTitle; }
    public String getTheatre() { return theatre; }
    public String getShowtime() { return showtime; }
    public String getDate() { return date; }
    public List<String> getSeatNumbers() { return seatNumbers; }
    public double getTotalAmount() { return totalAmount; }
    public String getBookingTime() { return bookingTime; }
}
