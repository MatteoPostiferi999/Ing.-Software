package dao.impl;

import dao.interfaces.BookingDAO;
import model.booking.Booking;
import model.user.Traveler;
import model.trip.Trip;

import java.util.ArrayList;
import java.util.List;

public class ConcreteBookingDAO implements BookingDAO {

    @Override
    public Booking getByTravelerAndTrip(Traveler traveler, Trip trip) {
        return null;
    }

    @Override
    public List<Booking> getAll() {
        return new ArrayList<>();
    }

    @Override
    public List<Booking> getByTraveler(Traveler traveler) {
        return new ArrayList<>();
    }

    @Override
    public void save(Booking booking) {
        // logica di salvataggio
    }

    @Override
    public void cancel(Traveler traveler, Trip trip) {
        // logica di cancellazione
    }
}
