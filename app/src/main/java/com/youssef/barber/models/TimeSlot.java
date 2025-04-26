package com.youssef.barber.models;

public class TimeSlot {
    private String id;
    private String time;
    private boolean available;

    // Required empty constructor for Firebase
    public TimeSlot() {}

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}