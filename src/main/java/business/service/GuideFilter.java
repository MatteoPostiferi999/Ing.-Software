package business.service;

import model.user.Guide;
import model.trip.Trip;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class GuideFilter implements TripFilterStrategy {
    private final Guide guide;
    private LocalDate minDate;
    private LocalDate maxDate;

    public GuideFilter(Guide guide, LocalDate minDate, LocalDate maxDate) {
        this.guide = guide;
        this.minDate = minDate;
        this.maxDate = maxDate;
    }

    public void setMinDate(LocalDate minDate) {
        this.minDate = minDate;
    }

    public void setMaxDate(LocalDate maxDate) {
        this.maxDate = maxDate;
    }

    @Override
    public List<Trip> filterTrips(List<Trip> allTrips) {
        List<Trip> result = new ArrayList<>();
        for (Trip trip : allTrips) {
            boolean hasRequiredSkills = guide.getSkills().containsAll(trip.getRequiredSkills());
            boolean dateOK = (minDate == null || !trip.getDate().isBefore(minDate)) &&
                             (maxDate == null || !trip.getDate().isAfter(maxDate));

            if (hasRequiredSkills && dateOK) {
                result.add(trip);
            }
        }
        return result;
    }
}
