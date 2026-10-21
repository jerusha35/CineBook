package com.movieticket.model;

import java.util.List;

public class Movie {
    private String id;
    private String title;
    private String language;
    private String genre;
    private String duration;
    private double rating;
    private String certificate; // U, U/A, A
    private String description;
    private List<String> showtimes;
    private List<String> theatres; // e.g., Sathyam Cinemas Chennai, Rohini Silver Screens, etc.
    private double ticketPrice;

    public Movie(String id, String title, String language, String genre, String duration, 
                 double rating, String certificate, String description, 
                 List<String> showtimes, List<String> theatres, double ticketPrice) {
        this.id = id;
        this.title = title;
        this.language = language;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.certificate = certificate;
        this.description = description;
        this.showtimes = showtimes;
        this.theatres = theatres;
        this.ticketPrice = ticketPrice;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getLanguage() { return language; }
    public String getGenre() { return genre; }
    public String getDuration() { return duration; }
    public double getRating() { return rating; }
    public String getCertificate() { return certificate; }
    public String getDescription() { return description; }
    public List<String> getShowtimes() { return showtimes; }
    public List<String> getTheatres() { return theatres; }
    public double getTicketPrice() { return ticketPrice; }

    public void setTitle(String title) { this.title = title; }
    public void setLanguage(String language) { this.language = language; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setRating(double rating) { this.rating = rating; }
    public void setCertificate(String certificate) { this.certificate = certificate; }
    public void setDescription(String description) { this.description = description; }
    public void setShowtimes(List<String> showtimes) { this.showtimes = showtimes; }
    public void setTheatres(List<String> theatres) { this.theatres = theatres; }
    public void setTicketPrice(double ticketPrice) { this.ticketPrice = ticketPrice; }

    @Override
    public String toString() {
        return title + " (" + language + " - " + certificate + ")";
    }
}
