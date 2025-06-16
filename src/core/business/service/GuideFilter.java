package core.business.service;

import core.model.Guide;
import core.model.Trip;
import java.util.ArrayList;
import java.util.List;

public class GuideFilter implements TripFilterStrategy {
    private Guide guide;

    public GuideFilter(Guide guide) {
        this.guide = guide;
    }

    @Override
    public List<Trip> filterTrips(List<Trip> allTrips) {
        List<Trip> result = new ArrayList<>();
        for (Trip trip : allTrips) {
            if (guide.getSkills().containsAll(trip.getRequiredSkills())) {
                result.add(trip);
            }
        }
        return result;
    }
}
