package core.model;

import java.util.Date;

public class Booking {
    private int travelerId;
    private int tripId;
    private int numberOfSlots;
    private Date bookingDate;
    private boolean isActive;

    public Booking(int travelerId, int tripId, int numberOfSlots) {
        this.travelerId = travelerId;
        this.tripId = tripId;
        this.numberOfSlots = numberOfSlots;
        this.bookingDate = new Date();
        this.isActive = true;
    }

    // Getters and Setters
    public int getTravelerId() {
        return travelerId;
    }

    public void setTravelerId(int travelerId) {
        this.travelerId = travelerId;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getNumberOfSlots() {
        return numberOfSlots;
    }

    public void setNumberOfSlots(int numberOfSlots) {
        this.numberOfSlots = numberOfSlots;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public boolean isActive() {
        return isActive;
    }
}
