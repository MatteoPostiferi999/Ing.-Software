package business.service;

import model.user.Traveler;
import model.trip.Trip;
import model.booking.BookingRegister;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class TravelerFilter implements TripFilterStrategy {
    private final Traveler traveler;

    private LocalDate minDate;
    private LocalDate maxDate;
    private Double minPrice;
    private Double maxPrice;

    public TravelerFilter(Traveler traveler) {
        this.traveler = traveler;
    }

    public void setMinDate(LocalDate minDate) {
        this.minDate = minDate;
    }

    public void setMaxDate(LocalDate maxDate) {
        this.maxDate = maxDate;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    @Override
    public List<Trip> filterTrips(List<Trip> allTrips) {
        List<Trip> result = new ArrayList<>();
        for (Trip trip : allTrips) {
            BookingRegister bookingRegister = trip.getBookingRegister();
            boolean hasFreeSpots = bookingRegister.getBookings().size() < bookingRegister.getMaxTrav();
            boolean dateOK = (minDate == null || !trip.getDate().isBefore(minDate)) &&
                             (maxDate == null || !trip.getDate().isAfter(maxDate));
            boolean priceOK = (minPrice == null || trip.getPrice() >= minPrice) &&
                              (maxPrice == null || trip.getPrice() <= maxPrice);

            if (hasFreeSpots && dateOK && priceOK) {
                result.add(trip);
            }
        }
        return result;
    }
}
