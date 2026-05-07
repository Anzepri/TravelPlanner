package com.travelplanner;

import java.util.ArrayList;
import java.util.List;

public class Trip {

    private int tripId;
    private String name;
    private String destination;
    private String startDate;
    private String endDate;
    private String ownerEmail;

    private List<ItineraryItem> itinerary = new ArrayList<>();

    public Trip(String name, String destination, String startDate, String endDate) {
        this.name = name;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getName() { return name; }
    public String getDestination() { return destination; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }

    public void setOwnerEmail(String email) { this.ownerEmail = email; }
    public String getOwnerEmail() { return ownerEmail; }

    public List<ItineraryItem> getItinerary() {
        return itinerary;
    }

    @Override
    public String toString() {
        return name + " - " + destination;
    }
}