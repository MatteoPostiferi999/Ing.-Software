package core.dao.interfaces;

import java.util.List;
import core.model.Booking;

public interface BookingDAO {
    Booking getById(int travelerId, int tripId);
    List<Booking> getAll();
    List<Booking> getByTravelerId(int travelerId);
    void save(Booking booking);
    void cancel(int travelerId, int tripId);
}
