package core.business.service;

import core.dao.interfaces.TripDAO;
import core.model.Trip;

public class TripService {
    private final TripDAO tripDAO;

    public TripService(TripDAO tripDAO) {
        this.tripDAO = tripDAO;
    }

    // Metodo per creare un nuovo viaggio
    public void createTrip(Trip trip) {
        tripDAO.save(trip);
    }

    // Metodo per modificare un viaggio esistente
    public void editTrip(Trip updatedTrip) {
        tripDAO.update(updatedTrip);
    }
}
