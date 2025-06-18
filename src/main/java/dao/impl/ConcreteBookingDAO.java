package dao.impl;

import dao.interfaces.BookingDAO;
import dao.interfaces.TravelerDAO;
import db.DBManager;
import model.booking.Booking;
import model.user.Traveler;
import model.trip.Trip;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConcreteBookingDAO implements BookingDAO {
    private final DBManager dbManager = DBManager.getInstance();
    private TravelerDAO travelerDAO;

    public ConcreteBookingDAO() {
        // Constructor
    }

    public ConcreteBookingDAO(TravelerDAO travelerDAO) {
        this.travelerDAO = travelerDAO;
    }

    public void setTravelerDAO(TravelerDAO travelerDAO) {
        this.travelerDAO = travelerDAO;
    }

    @Override
    public Booking getById(int bookingId) {
        String sql = "SELECT * FROM booking WHERE booking_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return createBookingFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Booking getByTravelerAndTrip(Traveler traveler, Trip trip) {
        String sql = "SELECT * FROM booking WHERE traveler_id = ? AND trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, traveler.getTravelerId());
            stmt.setInt(2, trip.getTripId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return createBookingFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Booking> getAll() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM booking";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                bookings.add(createBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    @Override
    public List<Booking> getByTraveler(Traveler traveler) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM booking WHERE traveler_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, traveler.getTravelerId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(createBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    @Override
    public List<Booking> getByTripId(int tripId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM booking WHERE trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tripId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(createBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    @Override
    public void loadBookingsForTrip(Trip trip) {
        List<Booking> bookings = getByTripId(trip.getTripId());
        for (Booking booking : bookings) {
            trip.getBookingRegister().addBooking(booking);
        }
    }

    @Override
    public void save(Booking booking) {
        if (booking.getBookingId() == 0) {
            // Nuovo booking
            insertBooking(booking);
        } else {
            // Aggiornamento
            updateBooking(booking);
        }
    }

    private void insertBooking(Booking booking) {
        String sql = "INSERT INTO booking (traveler_id, trip_id, status) VALUES (?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, booking.getTraveler().getTravelerId());
            stmt.setInt(2, booking.getTrip().getTripId());
            stmt.setString(3, booking.getStatus());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                booking.setBookingId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBooking(Booking booking) {
        String sql = "UPDATE booking SET traveler_id = ?, trip_id = ?, status = ? WHERE booking_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, booking.getTraveler().getTravelerId());
            stmt.setInt(2, booking.getTrip().getTripId());
            stmt.setString(3, booking.getStatus());
            stmt.setInt(4, booking.getBookingId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Booking booking) {
        String sql = "DELETE FROM booking WHERE booking_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, booking.getBookingId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo di supporto per creare un oggetto Booking dal ResultSet
    private Booking createBookingFromResultSet(ResultSet rs) throws SQLException {
        int bookingId = rs.getInt("booking_id");
        int travelerId = rs.getInt("traveler_id");
        int tripId = rs.getInt("trip_id");
        String status = rs.getString("status");

        // Questo è un esempio base. In una implementazione completa,
        // dovresti utilizzare TravelerDAO e TripDAO per caricare questi oggetti
        // Per ora, creiamo oggetti minimi per evitare NullPointerException
        Traveler traveler = travelerDAO != null ? travelerDAO.findById(travelerId) : new Traveler();
        traveler.setTravelerId(travelerId);

        Trip trip = new Trip("", "", 0, null, 0, 0, 0);
        trip.setTripId(tripId);

        Booking booking = new Booking(traveler, trip);
        booking.setBookingId(bookingId);
        booking.setStatus(status);

        return booking;
    }
}
