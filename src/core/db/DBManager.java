package core.db;

import core.model.*;

import java.util.*;

/**
 * Simula una base dati in memoria contenente tutte le entità principali.
 */
public class DBManager {
    private static DBManager instance;

    private Map<Integer, Trip> trips = new HashMap<>();
    private Map<Integer, Guide> guides = new HashMap<>();
    private Map<Integer, Traveler> travelers = new HashMap<>();
    private Map<Integer, Booking> bookings = new HashMap<>();
    private Map<Integer, Application> applications = new HashMap<>();
    private Map<Integer, Review> reviews = new HashMap<>();

    private DBManager() {}

    public static DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    public Map<Integer, Trip> getTrips() {
        return trips;
    }

    public Map<Integer, Guide> getGuides() {
        return guides;
    }

    public Map<Integer, Traveler> getTravelers() {
        return travelers;
    }

    public Map<Integer, Booking> getBookings() {
        return bookings;
    }

    public Map<Integer, Application> getApplications() {
        return applications;
    }

    public Map<Integer, Review> getReviews() {
        return reviews;
    }
}
