package core.model;
// Application.java
//package core.model;

/**
 * Rappresenta la candidatura di una Guide per un Trip.
 */
public class Application {
    private int idApplication;
    private int tripId;
    private int guideId;
    private String cv;
    private ApplicationStatus status;

    public Application(int idApplication, int tripId, int guideId, String cv, ApplicationStatus status) {
        this.idApplication = idApplication;
        this.tripId = tripId;
        this.guideId = guideId;
        this.cv = cv;
        this.status = status;
    }

    // Getters & Setters

    public int getIdApplication() {
        return idApplication;
    }

    public void setIdApplication(int idApplication) {
        this.idApplication = idApplication;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getGuideId() {
        return guideId;
    }

    public void setGuideId(int guideId) {
        this.guideId = guideId;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
}