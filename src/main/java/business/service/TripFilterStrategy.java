package business.service;

import model.trip.Trip;
import java.util.List;

public interface TripFilterStrategy {
    List<Trip> filterTrips(List<Trip> allTrips);
}
