package dao.interfaces;

import java.util.List;
import model.booking.Booking;
import model.user.Traveler;
import model.trip.Trip;

public interface BookingDAO {
    Booking getById(int bookingId);
    Booking getByTravelerAndTrip(Traveler traveler, Trip trip);
    List<Booking> getAll();
    List<Booking> getByTraveler(Traveler traveler);
    void save(Booking booking);
    void delete(Booking booking);
}
