package core.model;

import java.util.Date;

public class Booking {
    private Traveler traveler;
    private Trip trip;
    private Date date;

    public Booking(Traveler traveler, Trip trip) {
        this.traveler = traveler;
        this.trip = trip;
        this.date = new Date();
    }

    // Getters and Setters
    public Traveler getTraveler() {
        return traveler;
    }

    public void setTraveler(Traveler traveler) {
        this.traveler = traveler;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    
}
