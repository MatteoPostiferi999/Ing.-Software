package business.service;

import dao.interfaces.TripDAO;
import model.trip.Trip;
import java.util.List;

public class ViewTripsService {
    private final TripDAO tripDAO;
    private TripFilterStrategy strategy;

    public ViewTripsService(TripDAO tripDAO) {
        this.tripDAO = tripDAO;
    }

    public void setStrategy(TripFilterStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Trip> viewTrips() {
        List<Trip> allTrips = tripDAO.getAll();  // carica tutti i viaggi
        if (strategy != null) {
            return strategy.filterTrips(allTrips);
        }
        return allTrips;
    }

    public Trip viewTripDetails(int tripId) {
        return tripDAO.getById(tripId);
    }
}
