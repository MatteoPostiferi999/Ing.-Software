package core.model;

import java.util.Date;

public class Assignment {
    private Guide guide;
    private Trip trip;
    private Date date;

    public Assignment(Guide guide, Trip trip) { // trip e guide passati da fuori perchè c'è composizione
        this.guide = guide;
        this.trip = trip;
        this.date = new Date();
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
