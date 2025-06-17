package business.service;

import dao.interfaces.TripDAO;
import model.trip.Trip;
import java.util.List;
import business.service.TripService;

public class ViewTripsService {
    private final TripDAO tripDAO;
    private final TripService tripService;
    private TripFilterStrategy strategy;

    public ViewTripsService(TripDAO tripDAO, TripService tripService) {
        this.tripDAO = tripDAO;
        this.tripService = tripService;
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
        Trip trip = tripService.getTripById(tripId);
        if (trip == null) return null;

        List<Trip> visibleTrips = viewTrips();
        if (visibleTrips.contains(trip)) {
            return trip;
        }
        return null;
    }
}
