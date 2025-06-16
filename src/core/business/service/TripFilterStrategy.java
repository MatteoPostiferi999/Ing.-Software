package core.business.service;

import core.model.Trip;
import java.util.List;

public interface TripFilterStrategy {
    List<Trip> filterTrips(List<Trip> allTrips);
}
