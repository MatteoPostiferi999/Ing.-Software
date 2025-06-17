package business.service;

import dao.interfaces.TripDAO;
import model.trip.Trip;

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

    // Metodo per ottenere i dettagli di un viaggio tramite ID
    public Trip getTripById(int tripId) {
        return tripDAO.findById(tripId);
    }
}
