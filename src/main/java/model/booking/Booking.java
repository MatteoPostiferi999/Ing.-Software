package model.booking;

import model.trip.Trip;
import model.user.Traveler;
import java.time.LocalDate;

public class Booking {
    private int bookingId;
    private Traveler traveler;
    private Trip trip;
    private LocalDate date;

    // ID per la persistenza
    private int travelerId;
    private int tripId;

    // Constructor for creating a new booking (ID will be assigned by the database)
    public Booking(Traveler traveler, Trip trip) {
        this.bookingId = 0; // Will be assigned by DB
        this.traveler = traveler;
        this.trip = trip;
        this.date = LocalDate.now(); // Current date

        // Estrai gli ID dagli oggetti
        if (traveler != null) {
            this.travelerId = traveler.getTravelerId();
        }
        if (trip != null) {
            this.tripId = trip.getTripId();
        }
    }

    public Booking(int bookingId, Traveler traveler, Trip trip, LocalDate date) {
        this.bookingId = bookingId;
        this.traveler = traveler;
        this.trip = trip;
        this.date = date;

        // Estrai gli ID dagli oggetti
        if (traveler != null) {
            this.travelerId = traveler.getTravelerId();
        }
        if (trip != null) {
            this.tripId = trip.getTripId();
        }
    }

    // Constructor con gli ID per il caricamento dal database
    public Booking(int bookingId, int travelerId, int tripId, LocalDate date) {
        this.bookingId = bookingId;
        this.travelerId = travelerId;
        this.tripId = tripId;
        this.date = date;
        this.traveler = null; // Sarà caricato successivamente dal DAO
        this.trip = null;     // Sarà caricato successivamente dal DAO
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public Traveler getTraveler() {
        return traveler;
    }

    public void setTraveler(Traveler traveler) {
        this.traveler = traveler;
        if (traveler != null) {
            this.travelerId = traveler.getTravelerId();
        }
    }

    public int getTravelerId() {
        return travelerId;
    }

    public void setTravelerId(int travelerId) {
        this.travelerId = travelerId;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
        if (trip != null) {
            this.tripId = trip.getTripId();
        }
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
