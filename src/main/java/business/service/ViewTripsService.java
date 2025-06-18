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

    /**
     * Visualizza i viaggi applicando la strategia di filtro corrente.
     * Se non è stata impostata alcuna strategia, restituisce tutti i viaggi.
     * @return lista di viaggi che soddisfano i criteri di filtro
     */
    public List<Trip> viewTrips() {
        List<Trip> allTrips = tripDAO.findAll();  // Usa findAll() invece di getAll()
        if (strategy != null) {
            return strategy.filterTrips(allTrips);
        }
        return allTrips;
    }

    /**
     * Visualizza i dettagli di un viaggio specifico, ma solo se il viaggio
     * è visibile secondo la strategia di filtro corrente.
     * @param tripId ID del viaggio
     * @return il viaggio con i dettagli o null se non trovato o non visibile
     */
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
