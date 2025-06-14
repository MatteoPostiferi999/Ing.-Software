package core.model;

import java.time.LocalDate;

public class Assignment {
    private int guideId;
    private int tripId;
    private LocalDate date;

    public Assignment(int guideId, int tripId, LocalDate date) {
        this.guideId = guideId;
        this.tripId = tripId;
        this.date = date;
    }

    // Getters e Setters
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
