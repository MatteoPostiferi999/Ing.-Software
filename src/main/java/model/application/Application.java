package model.application;

import model.trip.Trip;
import model.user.Guide;

public class Application {
    private int applicationId;
    private String CV;
    private ApplicationStatus status;

    private Guide guide;
    private Trip trip;

    // Constructor for new Application (ID will be assigned by the database)
    public Application(String CV, Guide guide, Trip trip) {
        this.applicationId = 0; // will be set by DB
        this.CV = CV;
        this.guide = guide;
        this.trip = trip;
        this.status = ApplicationStatus.PENDING;
    }

    // Constructor for reconstructing Application from DB
    public Application(int applicationId, String CV, Guide guide, Trip trip, ApplicationStatus status) {
        this.applicationId = applicationId;
        this.CV = CV;
        this.guide = guide;
        this.trip = trip;
        this.status = status;
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
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}
