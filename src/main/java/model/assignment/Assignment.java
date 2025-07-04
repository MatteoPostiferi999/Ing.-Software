package model.assignment;

import model.trip.Trip;
import model.user.Guide;
import java.time.LocalDate;
import java.util.Date;

public class Assignment {
    private int assignmentId;
    private Guide guide;
    private Trip trip;
    private LocalDate date;

    // Constructor for new assignment (ID will be set by DB, date is now)
    public Assignment(Guide guide, Trip trip) {
        this.assignmentId = 0;
        this.guide = guide;
        this.trip = trip;
        this.date = LocalDate.now();
    }

    // Constructor for reconstruction from DB
    public Assignment(int assignmentId, Guide guide, Trip trip, LocalDate date) {
        this.assignmentId = assignmentId;
        this.guide = guide;
        this.trip = trip;
        this.date = date;
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
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
