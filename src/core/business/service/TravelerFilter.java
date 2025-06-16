package core.business.service;

import core.model.Traveler;
import core.model.Trip;

import java.util.ArrayList;
import java.util.List;


public class TravelerFilter implements TripFilterStrategy {
    private Traveler traveler;

    public TravelerFilter(Traveler traveler) {
        this.traveler = traveler;
    }

    @Override
    public List<Trip> filterTrips(List<Trip> allTrips) { // per ora mostra i viaggi per cui c'è posto, ma ancora non applica filtri dinamici
        List<Trip> result = new ArrayList<>();
        for (Trip trip : allTrips) {
            if (trip.getBookingRegister().getBookings().size() < trip.getBookingRegister().getMaxTrav()) {
                result.add(trip);
            }
        }
        return result;
    }
}
