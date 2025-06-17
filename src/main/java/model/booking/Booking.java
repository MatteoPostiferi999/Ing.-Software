package model.booking;

import model.trip.Trip;
import model.user.Traveler;

import java.util.Date;

public class Booking {
    private Traveler traveler;
    private Trip trip;
    private Date date;
    private int bookingId;

    // Constructor for creating a new booking (ID will be assigned by the database)
    public Booking(Traveler traveler, Trip trip) {
        this.bookingId = 0; // Will be assigned by DB
        this.traveler = traveler;
        this.trip = trip;
        this.date = new Date();
    }

    public Booking(int bookingId, Traveler traveler, Trip trip, Date date) {
        this.bookingId = bookingId;
        this.traveler = traveler;
        this.trip = trip;
        this.date = date;
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

    public int getBookingId() {
        return bookingId;
    }
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }
    
}
