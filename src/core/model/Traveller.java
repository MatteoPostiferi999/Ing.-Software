package core.model;
// package core.model;

import java.util.List;

public class Traveller {
    private int idTraveller;
    private List<Trip> bookedTrips;

    // Constructor
    public Traveller(int idTraveller, List<Trip> bookedTrips) {
        this.idTraveller = idTraveller;
        this.bookedTrips = bookedTrips;
    }

    // Getters and Setters
    public int getIdTraveller() {
        return idTraveller;
    }

    public void setIdTraveller(int idTraveller) {
        this.idTraveller = idTraveller;
    }

    public List<Trip> getBookedTrips() {
        return bookedTrips;
    }

    public void setBookedTrips(List<Trip> bookedTrips) {
        this.bookedTrips = bookedTrips;
    }
}