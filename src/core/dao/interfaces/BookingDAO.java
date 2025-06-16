package core.dao.interfaces;

import java.util.List;
import core.model.Booking;
import core.model.Traveler;
import core.model.Trip;

public interface BookingDAO {
    Booking getByTravelerAndTrip(Traveler traveler, Trip trip);
    List<Booking> getAll();
    List<Booking> getByTraveler(Traveler traveler);
    void save(Booking booking);
    void cancel(Traveler traveler, Trip trip);
}
