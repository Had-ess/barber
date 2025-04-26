package com.youssef.barber.models;

public class Appointment {
    private String id;
    private String userId;
    private String timeSlotId;
    private String status;
    private long timestamp;

    // Required empty constructor for Firebase
    public Appointment() {}

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTimeSlotId() { return timeSlotId; }
    public void setTimeSlotId(String timeSlotId) { this.timeSlotId = timeSlotId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}