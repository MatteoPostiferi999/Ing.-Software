package model.application;

import model.trip.Trip;
import model.user.Guide;

public class Application {
    private int applicationId;
    private String CV;
    private ApplicationStatus status;

    // Oggetti correlati per la logica di business
    private Guide guide;
    private Trip trip;

    // ID per la persistenza
    private int guideId;
    private int tripId;

    // Constructor for new Application (ID will be assigned by the database)
    public Application(String CV, Guide guide, Trip trip) {
        this.applicationId = 0; // will be set by DB
        this.CV = CV;
        this.guide = guide;
        this.trip = trip;
        this.status = ApplicationStatus.PENDING;

        // Estratti gli ID dagli oggetti
        if (guide != null) {
            this.guideId = guide.getGuideId();
        }
        if (trip != null) {
            this.tripId = trip.getTripId();
        }
    }

    // Constructor for reconstructing Application from DB
    public Application(int applicationId, String CV, Guide guide, Trip trip, ApplicationStatus status) {
        this.applicationId = applicationId;
        this.CV = CV;
        this.guide = guide;
        this.trip = trip;
        this.status = status;

        // Estratti gli ID dagli oggetti
        if (guide != null) {
            this.guideId = guide.getGuideId();
        }
        if (trip != null) {
            this.tripId = trip.getTripId();
        }
    }

    // Constructor con gli ID per il caricamento dal database
    public Application(int applicationId, String CV, int guideId, int tripId, ApplicationStatus status) {
        this.applicationId = applicationId;
        this.CV = CV;
        this.guideId = guideId;
        this.tripId = tripId;
        this.status = status;
        this.guide = null; // Sarà caricato successivamente dal DAO
        this.trip = null;  // Sarà caricato successivamente dal DAO
    }

    // Getters & Setters
    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getCV() {
        return CV;
    }

    public void setCV(String CV) {
        this.CV = CV;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
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

    public int getGuideId() {
        return guideId;
    }

    public void setGuideId(int guideId) {
        this.guideId = guideId;
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

    public void accept() {
        if (this.status == ApplicationStatus.PENDING) {
            this.status = ApplicationStatus.ACCEPTED;
        } else {
            throw new IllegalStateException("Cannot accept an application that is not pending.");
        }
    }

    public void reject() {
        if (this.status == ApplicationStatus.PENDING) {
            this.status = ApplicationStatus.REJECTED;
        } else {
            throw new IllegalStateException("Cannot reject an application that is not pending.");
        }
    }

    public boolean isPending() {
        return this.status == ApplicationStatus.PENDING;
    }

    public boolean isAccepted() {
        return this.status == ApplicationStatus.ACCEPTED;
    }

    public boolean isRejected() {
        return this.status == ApplicationStatus.REJECTED;
    }
}
