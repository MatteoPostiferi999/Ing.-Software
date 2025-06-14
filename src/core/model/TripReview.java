package core.model;
// TripReview.java
// package core.model;

/**
 * Recensione dedicata a un Trip.
 */
public class TripReview extends Review {
    private int tripId;

    public TripReview(int idReview, int rating, String text, int tripId) {
        super(idReview, rating, text);
        this.tripId = tripId;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }
}