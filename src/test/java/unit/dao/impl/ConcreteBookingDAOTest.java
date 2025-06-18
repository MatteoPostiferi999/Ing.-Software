// ConcreteBookingDAOTest.java
package unit.dao.impl;

import dao.impl.ConcreteBookingDAO;
import dao.interfaces.TravelerDAO;
import dao.interfaces.TripDAO;
import model.booking.Booking;
import model.booking.BookingRegister;
import model.user.Traveler;
import model.trip.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConcreteBookingDAOTest {

    @Mock private TravelerDAO travelerDAO;
    @Mock private TripDAO tripDAO;

    private ConcreteBookingDAO dao;

    @Mock private Trip trip;
    @Mock private Booking booking;
    @Mock private Traveler traveler;

    @BeforeEach
    void setUp() {
        // Create a spy so we can stub getByTripId(...)
        dao = spy(new ConcreteBookingDAO(travelerDAO, tripDAO));
    }

    @Test
    void loadBookingsForTrip_shouldPopulateRegisterAndTraveler() {
        // Arrange
        when(trip.getTripId()).thenReturn(5);
        // Stub the DAO method to return our booking
        doReturn(List.of(booking)).when(dao).getByTripId(5);

        // Booking initially has no traveler
        when(booking.getTraveler()).thenReturn(null);
        when(booking.getTravelerId()).thenReturn(11);
        // travelerDAO returns our traveler
        when(travelerDAO.findById(11)).thenReturn(traveler);

        // Mock the BookingRegister on the Trip
        BookingRegister register = mock(BookingRegister.class);
        when(trip.getBookingRegister()).thenReturn(register);

        // Act
        dao.loadBookingsForTrip(trip);

        // Assert
        verify(booking).setTraveler(traveler);
        verify(booking).setTrip(trip);
        verify(register).addBooking(booking);
    }
}
