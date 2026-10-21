package com.movieticket.model;

public class User {
    private String username;
    private String password;
    private String fullName;
    private String phone;
    private boolean isAdmin;

    public User(String username, String password, String fullName, String phone, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.isAdmin = isAdmin;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public boolean isAdmin() { return isAdmin; }

    public void setPassword(String password) { this.password = password; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
}
