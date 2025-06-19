package dao.impl;

import dao.interfaces.BookingDAO;
import dao.interfaces.TravelerDAO;
import dao.interfaces.TripDAO;
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
    private TripDAO tripDAO;

    public ConcreteBookingDAO() {
        // Constructor
    }

    public ConcreteBookingDAO(TravelerDAO travelerDAO, TripDAO tripDAO) {
        this.travelerDAO = travelerDAO;
        this.tripDAO = tripDAO;
    }

    public void setTravelerDAO(TravelerDAO travelerDAO) {
        this.travelerDAO = travelerDAO;
    }

    public void setTripDAO(TripDAO tripDAO) {
        this.tripDAO = tripDAO;
    }

    @Override
    public Booking getById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
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
        String sql = "SELECT * FROM bookings WHERE traveler_id = ? AND trip_id = ?";
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
        String sql = "SELECT * FROM bookings";
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
        String sql = "SELECT * FROM bookings WHERE traveler_id = ?";
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
        String sql = "SELECT * FROM bookings WHERE trip_id = ?";
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
            // Carica il viaggiatore se necessario
            if (booking.getTraveler() == null && travelerDAO != null) {
                Traveler traveler = travelerDAO.findById(booking.getTravelerId());
                booking.setTraveler(traveler);
            }

            // Imposta il riferimento al viaggio
            booking.setTrip(trip);

            // Aggiungi la prenotazione al registro del viaggio
            trip.getBookingRegister().addBooking(booking);
        }
    }

    @Override
    public void save(Booking booking) {
        if (booking.getBookingId() == 0) {
            // Nuova prenotazione
            insertBooking(booking);
        } else {
            // Aggiornamento
            updateBooking(booking);
        }
    }

    private void insertBooking(Booking booking) {
        String sql = "INSERT INTO bookings (traveler_id, trip_id, booking_date) VALUES (?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, booking.getTravelerId());
            stmt.setInt(2, booking.getTripId());
            stmt.setDate(3, Date.valueOf(booking.getDate()));
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    booking.setBookingId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBooking(Booking booking) {
        String sql = "UPDATE bookings SET traveler_id = ?, trip_id = ?, booking_date = ? WHERE booking_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, booking.getTravelerId());
            stmt.setInt(2, booking.getTripId());
            stmt.setDate(3, Date.valueOf(booking.getDate()));
            stmt.setInt(4, booking.getBookingId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Booking booking) {
        String sql = "DELETE FROM bookings WHERE booking_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, booking.getBookingId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo helper per creare un oggetto Booking dal ResultSet
    private Booking createBookingFromResultSet(ResultSet rs) throws SQLException {
        int bookingId = rs.getInt("booking_id");
        int travelerId = rs.getInt("traveler_id");
        int tripId = rs.getInt("trip_id");
        Date bookingDate = rs.getDate("booking_date");

        // Crea il booking con gli ID
        Booking booking = new Booking(
                bookingId,
                travelerId,
                tripId,
                bookingDate.toLocalDate()
        );

        // Se disponibili, carica gli oggetti correlati
        if (travelerDAO != null) {
            Traveler traveler = travelerDAO.findById(travelerId);
            if (traveler != null) {
                booking.setTraveler(traveler);
            }
        }

        if (tripDAO != null) {
            Trip trip = tripDAO.findById(tripId);
            if (trip != null) {
                booking.setTrip(trip);
            }
        }

        return booking;
    }
}