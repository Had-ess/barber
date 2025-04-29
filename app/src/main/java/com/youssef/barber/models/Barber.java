package com.youssef.barber.models;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Barber {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String specialty;
    private float rating;
    private String photoUrl;
    private boolean isAvailable;
    private Map<String, Boolean> workingDays; // Key: day (e.g., "Monday"), Value: true if working

    // Required empty constructor for Firebase
    public Barber() {
    }

    public Barber(String id, String name, String email, String phone, String specialty) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.specialty = specialty;
        this.rating = 0.0f;
        this.isAvailable = true;
        this.workingDays = new HashMap<>();
        initializeDefaultWorkingDays();
    }

    private void initializeDefaultWorkingDays() {
        workingDays.put("Monday", true);
        workingDays.put("Tuesday", true);
        workingDays.put("Wednesday", true);
        workingDays.put("Thursday", true);
        workingDays.put("Friday", true);
        workingDays.put("Saturday", true);
        workingDays.put("Sunday", true);
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public Map<String, Boolean> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(Map<String, Boolean> workingDays) {
        this.workingDays = workingDays;
    }

    // Utility methods
    public boolean isWorkingDay(String day) {
        return workingDays.containsKey(day) && workingDays.get(day);
    }

    public String getFormattedRating() {
        return String.format(Locale.getDefault(), "%.1f", rating);
    }
}
