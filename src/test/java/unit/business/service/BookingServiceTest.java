package unit.business.service;

import business.service.BookingService;
import business.service.NotificationService;
import dao.interfaces.BookingDAO;
import dao.interfaces.TravelerDAO;
import dao.interfaces.TripDAO;
import model.booking.Booking;
import model.booking.BookingRegister;
import model.trip.Trip;
import model.user.Traveler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock private BookingDAO bookingDAO;
    @Mock private TripDAO tripDAO;
    @Mock private TravelerDAO travelerDAO;
    @Mock private NotificationService notificationService;
    @Mock private Trip trip;
    @Mock private Traveler traveler;
    @Mock private BookingRegister bookingRegister;
    @Mock private Booking booking;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingDAO, tripDAO, travelerDAO, notificationService);
    }

    @Test
    void testBookTrip_Success() {
        // Arrange
        when(trip.getBookingRegister()).thenReturn(bookingRegister);
        when(bookingRegister.getAvailableSpots()).thenReturn(1);
        when(bookingRegister.hasBooking(traveler)).thenReturn(false);
        when(trip.getTitle()).thenReturn("Test Trip");

        // Act
        boolean result = bookingService.bookTrip(traveler, trip);

        // Assert
        assertTrue(result);
        verify(bookingDAO).save(any(Booking.class));
        verify(bookingRegister).addBooking(any(Booking.class));
        verify(notificationService).sendNotification(eq(traveler), contains("confermata"));
    }

    @Test
    void testBookTrip_NoAvailableSpots() {
        // Arrange
        when(trip.getBookingRegister()).thenReturn(bookingRegister);
        when(bookingRegister.getAvailableSpots()).thenReturn(0);

        // Act
        boolean result = bookingService.bookTrip(traveler, trip);

        // Assert
        assertFalse(result);
        verify(bookingDAO, never()).save(any(Booking.class));
        verify(notificationService, never()).sendNotification(any(), any());
    }

    @Test
    void testBookTrip_AlreadyBooked() {
        // Arrange
        when(trip.getBookingRegister()).thenReturn(bookingRegister);
        when(bookingRegister.getAvailableSpots()).thenReturn(1);
        when(bookingRegister.hasBooking(traveler)).thenReturn(true);

        // Act
        boolean result = bookingService.bookTrip(traveler, trip);

        // Assert
        assertFalse(result);
        verify(bookingDAO, never()).save(any(Booking.class));
        verify(notificationService, never()).sendNotification(any(), any());
    }

    @Test
    void testBookTripById_Success() {
        // Arrange
        when(travelerDAO.findById(1)).thenReturn(traveler);
        when(tripDAO.findById(1)).thenReturn(trip);
        when(trip.getBookingRegister()).thenReturn(bookingRegister);
        when(bookingRegister.getAvailableSpots()).thenReturn(1);
        when(bookingRegister.hasBooking(traveler)).thenReturn(false);

        // Act
        boolean result = bookingService.bookTripById(1, 1);

        // Assert
        assertTrue(result);
        verify(bookingDAO).save(any(Booking.class));
    }

    @Test
    void testCancelBooking_Success() {
        // Arrange
        when(trip.getBookingRegister()).thenReturn(bookingRegister);
        when(bookingRegister.getBookingByTraveler(traveler)).thenReturn(booking);
        when(trip.getTitle()).thenReturn("Test Trip");

        // Act
        boolean result = bookingService.cancelBooking(traveler, trip);

        // Assert
        assertTrue(result);
        verify(bookingDAO).delete(booking);
        verify(bookingRegister).removeBooking(booking);
        verify(notificationService).sendNotification(eq(traveler), contains("cancellata"));
    }

    @Test
    void testCancelBooking_NoBookingFound() {
        // Arrange
        when(trip.getBookingRegister()).thenReturn(bookingRegister);
        when(bookingRegister.getBookingByTraveler(traveler)).thenReturn(null);

        // Act
        boolean result = bookingService.cancelBooking(traveler, trip);

        // Assert
        assertFalse(result);
        verify(bookingDAO, never()).delete(any(Booking.class));
        verify(notificationService, never()).sendNotification(any(), any());
    }

    @Test
    void testGetBookingsForTrip() {
        // Arrange
        List<Booking> expectedBookings = Arrays.asList(booking);
        when(trip.getBookingRegister()).thenReturn(bookingRegister);
        when(bookingRegister.getBookings()).thenReturn(expectedBookings);

        // Act
        List<Booking> result = bookingService.getBookingsForTrip(trip);

        // Assert
        assertEquals(expectedBookings, result);
    }

    @Test
    void testLoadBookingsForTrip() {
        // Arrange
        List<Booking> expectedBookings = Arrays.asList(booking);
        when(trip.getBookingRegister()).thenReturn(bookingRegister);
        when(bookingRegister.getBookings()).thenReturn(expectedBookings);

        // Act
        List<Booking> result = bookingService.loadBookingsForTrip(trip);

        // Assert
        assertEquals(expectedBookings, result);
        verify(bookingDAO).loadBookingsForTrip(trip);
    }

    @Test
    void testGetBookingsForTraveler() {
        // Arrange
        List<Booking> expectedBookings = Arrays.asList(booking);
        when(bookingDAO.getByTraveler(traveler)).thenReturn(expectedBookings);

        // Act
        List<Booking> result = bookingService.getBookingsForTraveler(traveler);

        // Assert
        assertEquals(expectedBookings, result);
    }

    @Test
    void testHasMinimumParticipants() {
        // Arrange
        when(trip.getBookingRegister()).thenReturn(bookingRegister);
        when(bookingRegister.getBookings()).thenReturn(new ArrayList<>());
        when(bookingRegister.hasMinimumTravelers()).thenReturn(true);

        // Act
        boolean result = bookingService.hasMinimumParticipants(trip);

        // Assert
        assertTrue(result);
        verify(bookingDAO).loadBookingsForTrip(trip);
    }

    @Test
    void testGetTripsBookedByTraveler() {
        // Arrange
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingDAO.getByTraveler(traveler)).thenReturn(bookings);
        when(booking.getTrip()).thenReturn(trip);

        // Act
        List<Trip> result = bookingService.getTripsBookedByTraveler(traveler);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(trip, result.get(0));
    }
}