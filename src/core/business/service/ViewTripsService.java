package core.business.service;

import core.dao.interfaces.TripDAO;
import core.model.Trip;
import java.util.List;

public class ViewTripsService {
    private TripDAO tripDAO;
    private TripFilterStrategy strategy;

    public ViewTripsService(TripDAO tripDAO) {
        this.tripDAO = tripDAO;
    }

    public void setStrategy(TripFilterStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Trip> viewTrips() {
        List<Trip> allTrips = tripDAO.getAll();
        if (strategy == null) {
            throw new IllegalStateException("Strategy not set");
        }
        return strategy.filterTrips(allTrips);
    }
}
