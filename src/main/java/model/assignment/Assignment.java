package model.assignment;

import model.trip.Trip;
import model.user.Guide;
import java.time.LocalDate;

public class Assignment {
    private int assignmentId;
    private Guide guide;
    private Trip trip;
    private LocalDate date;

    // ID per la persistenza
    private int guideId;
    private int tripId;

    // Constructor for new assignment (ID will be set by DB, date is now)
    public Assignment(Guide guide, Trip trip) {
        this.assignmentId = 0;
        this.guide = guide;
        this.trip = trip;
        this.date = LocalDate.now();

        // Estratti gli ID dagli oggetti
        if (guide != null) {
            this.guideId = guide.getGuideId();
        }
        if (trip != null) {
            this.tripId = trip.getTripId();
        }
    }

    // Constructor for reconstruction from DB
    public Assignment(int assignmentId, Guide guide, Trip trip, LocalDate date) {
        this.assignmentId = assignmentId;
        this.guide = guide;
        this.trip = trip;
        this.date = date;

        // Estratti gli ID dagli oggetti
        if (guide != null) {
            this.guideId = guide.getGuideId();
        }
        if (trip != null) {
            this.tripId = trip.getTripId();
        }
    }

    // Constructor con gli ID per il caricamento dal database
    public Assignment(int assignmentId, int guideId, int tripId, LocalDate date) {
        this.assignmentId = assignmentId;
        this.guideId = guideId;
        this.tripId = tripId;
        this.date = date;
        this.guide = null; // Sarà caricato successivamente dal DAO
        this.trip = null;  // Sarà caricato successivamente dal DAO
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Guide getGuide() {
        return guide;
    }

    public void setGuide(Guide guide) {
        this.guide = guide;
        if (guide != null) {
            this.guideId = guide.getGuideId();
        }
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getGuideId() {
        return guideId;
    }

    public void setGuideId(int guideId) {
        this.guideId = guideId;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }
}
