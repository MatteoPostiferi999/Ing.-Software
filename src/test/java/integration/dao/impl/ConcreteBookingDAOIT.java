package integration.dao.impl;

import dao.impl.ConcreteBookingDAO;
import dao.interfaces.BookingDAO;
import dao.interfaces.TravelerDAO;
import dao.interfaces.TripDAO;
import db.DBManager;
import model.booking.Booking;
import model.user.Traveler;
import model.user.User;
import model.notification.NotificationRegister;
import model.trip.Trip;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConcreteBookingDAOIT {

    private static final int TEST_USER_ID     = 1001;
    private static final int TEST_TRAVELER_ID = 2001;
    private static final int TEST_TRIP_ID     = 3001;

    private static DBManager dbManager;
    private static BookingDAO bookingDAO;

    @BeforeAll
    static void beforeAll() {
        dbManager   = DBManager.getInstance();
        bookingDAO  = new ConcreteBookingDAO();
        setupTestEntities();
    }

    @AfterAll
    static void afterAll() {
        cleanupData();
    }

    @BeforeEach
    void cleanBeforeEach() {
        cleanupData();
        setupTestEntities();  // re-insert the master records
    }

    @AfterEach
    void cleanAfterEach() {
        cleanupData();
    }

    private static void setupTestEntities() {
        try (Connection conn = dbManager.getConnection()) {
            // 1) utente
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (user_id, username, email, password) VALUES (?, 'u1','u1@test','pwd')"
            )) {
                ps.setInt(1, TEST_USER_ID);
                ps.executeUpdate();
            }
            // 2) traveler
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO travelers (traveler_id, user_id) VALUES (?, ?)"
            )) {
                ps.setInt(1, TEST_TRAVELER_ID);
                ps.setInt(2, TEST_USER_ID);
                ps.executeUpdate();
            }
            // 3) trip
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO trips (trip_id, title, description, price, date, min_travelers, max_travelers, max_guides) " +
                            "VALUES (?, 'T','D', 50.0, CURRENT_DATE,1,5,2)"
            )) {
                ps.setInt(1, TEST_TRIP_ID);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Setup test entities failed", ex);
        }
    }

    private static void cleanupData() {
        try (Connection conn = dbManager.getConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM bookings");
            st.executeUpdate("DELETE FROM travelers WHERE traveler_id = " + TEST_TRAVELER_ID);
            st.executeUpdate("DELETE FROM users     WHERE user_id     = " + TEST_USER_ID);
            st.executeUpdate("DELETE FROM trips     WHERE trip_id     = " + TEST_TRIP_ID);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Test @Order(1) @DisplayName("save() e getById()")
    void t1_save_and_getById() {
        Booking b = new Booking(0, TEST_TRAVELER_ID, TEST_TRIP_ID, LocalDate.now());
        bookingDAO.save(b);
        assertTrue(b.getBookingId() > 0, "ID generato");
        Booking fetched = bookingDAO.getById(b.getBookingId());
        assertNotNull(fetched);
        assertEquals(TEST_TRAVELER_ID, fetched.getTravelerId());
        assertEquals(TEST_TRIP_ID,     fetched.getTripId());
    }

    @Test @Order(2) @DisplayName("update()")
    void t2_update() {
        Booking b = new Booking(0, TEST_TRAVELER_ID, TEST_TRIP_ID, LocalDate.now());
        bookingDAO.save(b);
        int id = b.getBookingId();
        LocalDate newDate = LocalDate.now().plusDays(2);
        b.setDate(newDate);
        bookingDAO.save(b);
        Booking updated = bookingDAO.getById(id);
        assertEquals(newDate, updated.getDate());
    }

    @Test @Order(3) @DisplayName("getByTravelerAndTrip()")
    void t3_getByTravelerAndTrip() {
        Booking b = new Booking(0, TEST_TRAVELER_ID, TEST_TRIP_ID, LocalDate.now());
        bookingDAO.save(b);
        Booking found = bookingDAO.getByTravelerAndTrip(
                new Traveler(TEST_TRAVELER_ID, new User("x","y","z"), new NotificationRegister()),
                new Trip(TEST_TRIP_ID, "t","d",0.0, LocalDate.now(),1,5,2)
        );
        assertNotNull(found);
        assertEquals(b.getBookingId(), found.getBookingId());
    }

    @Test @Order(4) @DisplayName("getAll(), getByTraveler(), getByTripId()")
    void t4_queries_collections() {
        Booking b1 = new Booking(0, TEST_TRAVELER_ID, TEST_TRIP_ID, LocalDate.now());
        Booking b2 = new Booking(0, TEST_TRAVELER_ID, TEST_TRIP_ID, LocalDate.now().plusDays(1));
        bookingDAO.save(b1);
        bookingDAO.save(b2);

        List<Booking> all    = bookingDAO.getAll();
        List<Booking> byTrav = bookingDAO.getByTraveler(new Traveler(TEST_TRAVELER_ID, null, null));
        List<Booking> byTrip = bookingDAO.getByTripId(TEST_TRIP_ID);

        assertTrue(all.size() >= 2);
        assertTrue(byTrav.stream().allMatch(x -> x.getTravelerId() == TEST_TRAVELER_ID));
        assertTrue(byTrip.stream().allMatch(x -> x.getTripId() == TEST_TRIP_ID));
    }

    @Test @Order(5) @DisplayName("loadBookingsForTrip()")
    void t5_loadBookingsForTrip() {
        Booking b1 = new Booking(0, TEST_TRAVELER_ID, TEST_TRIP_ID, LocalDate.now());
        bookingDAO.save(b1);

        Trip trip = new Trip(TEST_TRIP_ID, "", "", 0.0, LocalDate.now(), 1, 5, 2);
        bookingDAO.loadBookingsForTrip(trip);

        assertEquals(1, trip.getBookingRegister().getBookings().size());
    }

    @Test @Order(6) @DisplayName("delete()")
    void t6_delete() {
        Booking b = new Booking(0, TEST_TRAVELER_ID, TEST_TRIP_ID, LocalDate.now());
        bookingDAO.save(b);
        bookingDAO.delete(b);
        assertNull(bookingDAO.getById(b.getBookingId()));
    }
}
