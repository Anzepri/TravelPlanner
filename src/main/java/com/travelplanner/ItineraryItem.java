package com.travelplanner;

public class ItineraryItem {
    private String title;
    private String date;
    private String time;
    private String location;

    public ItineraryItem(String title, String date, String time, String location) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getLocation() { return location; }

    public void setTitle(String title) { this.title = title; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setLocation(String location) { this.location = location; }

    @Override
    public String toString() {
        return date + " | " + title + " | " + time + " | " + location;
    }
}