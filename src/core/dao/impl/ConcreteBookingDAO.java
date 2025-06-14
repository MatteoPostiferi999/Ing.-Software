package core.dao.impl;

import core.dao.interfaces.BookingDAO;
import core.model.Booking;
import java.util.*;

public class ConcreteBookingDAO implements BookingDAO {
    public Booking getById(int travelerId, int tripId) { return null; }
    public List<Booking> getAll() { return new ArrayList<>(); }
    public List<Booking> getByTravelerId(int travelerId) { return new ArrayList<>(); }
    public void save(Booking booking) {}
    public void cancel(int travelerId, int tripId) {}
}
