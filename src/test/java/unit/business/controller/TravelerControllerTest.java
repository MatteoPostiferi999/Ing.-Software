package unit.business.controller;

import business.controller.TravelerController;
import business.service.*;
import model.booking.Booking;
import model.notification.Notification;
import model.trip.Trip;
import model.user.Guide;
import model.user.Traveler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TravelerControllerTest {

    @Mock
    private Traveler traveler;

    @Mock
    private ViewTripsService viewTripsService;

    @Mock
    private BookingService bookingService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ReviewService reviewService;

    @Mock
    private TravelerService travelerService;

    private TravelerController travelerController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        travelerController = new TravelerController(traveler, viewTripsService, bookingService, 
                                                   notificationService, reviewService, travelerService);
    }

    @Test
    public void testViewAvailableTrips() {
        // Arrange
        LocalDate minDate = LocalDate.of(2023, 1, 1);
        LocalDate maxDate = LocalDate.of(2023, 12, 31);
        Double maxPrice = 1500.0;
        List<Trip> mockTrips = Arrays.asList(new Trip("Viaggio a Roma", "Descrizione", 1000.0, LocalDate.of(2023, 6, 1), 5, 20, 2));
        when(viewTripsService.viewTrips()).thenReturn(mockTrips);

        // Act
        List<Trip> result = travelerController.viewAvailableTrips(minDate, maxDate, maxPrice);

        // Assert
        assertEquals(mockTrips, result);
        verify(viewTripsService).setStrategy(any(TravelerFilter.class));
        verify(viewTripsService).viewTrips();
    }

    @Test
    public void testViewTripDetails() {
        // Arrange
        int tripId = 1;
        Trip mockTrip = new Trip("Viaggio a Roma", "Descrizione", 1000.0, LocalDate.of(2023, 6, 1), 5, 20, 2);
        when(viewTripsService.viewTripDetails(tripId)).thenReturn(mockTrip);

        // Act
        Trip result = travelerController.viewTripDetails(tripId);

        // Assert
        assertEquals(mockTrip, result);
        verify(viewTripsService).viewTripDetails(tripId);
    }

    @Test
    public void testBookTrip_Success() {
        // Arrange
        Trip trip = new Trip("Viaggio a Roma", "Descrizione", 1000.0, LocalDate.of(2023, 6, 1), 5, 20, 2);

        // Act
        boolean result = travelerController.bookTrip(trip);

        // Assert
        assertTrue(result);
        verify(bookingService).bookTrip(traveler, trip);
    }

    @Test
    public void testBookTrip_Failure() {
        // Arrange
        Trip trip = new Trip("Viaggio a Roma", "Descrizione", 1000.0, LocalDate.of(2023, 6, 1), 5, 20, 2);
        doThrow(new RuntimeException("Error")).when(bookingService).bookTrip(traveler, trip);

        // Act
        boolean result = travelerController.bookTrip(trip);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testViewBookedTrips() {
        // Arrange
        List<Booking> mockBookings = new ArrayList<>();
        when(travelerService.getBookings(traveler)).thenReturn(mockBookings);

        // Act
        List<Booking> result = travelerController.viewBookedTrips();

        // Assert
        assertEquals(mockBookings, result);
        verify(travelerService).getBookings(traveler);
    }

    @Test
    public void testCancelBooking_Success() {
        // Arrange
        Trip trip = new Trip("Viaggio a Roma", "Descrizione", 1000.0, LocalDate.now().plusDays(10), 5, 20, 2);
        Booking booking = mock(Booking.class);
        when(booking.getTrip()).thenReturn(trip);

        // Act
        boolean result = travelerController.cancelBooking(booking);

        // Assert
        assertTrue(result);
        verify(bookingService).cancelBooking(traveler, trip);
    }

    @Test
    public void testCancelBooking_PastTripFailure() {
        // Arrange
        Trip trip = new Trip("Viaggio a Roma", "Descrizione", 1000.0, LocalDate.now().minusDays(1), 5, 20, 2);
        Booking booking = mock(Booking.class);
        when(booking.getTrip()).thenReturn(trip);

        // Act
        boolean result = travelerController.cancelBooking(booking);

        // Assert
        assertFalse(result);
        verify(bookingService, never()).cancelBooking(any(), any());
    }

    @Test
    public void testCancelBooking_ServiceFailure() {
        // Arrange
        Trip trip = new Trip("Viaggio a Roma", "Descrizione", 1000.0, LocalDate.now().plusDays(10), 5, 20, 2);
        Booking booking = mock(Booking.class);
        when(booking.getTrip()).thenReturn(trip);
        doThrow(new RuntimeException("Error")).when(bookingService).cancelBooking(traveler, trip);

        // Act
        boolean result = travelerController.cancelBooking(booking);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testLeaveReviewForTrip_Success() {
        // Arrange
        Trip trip = new Trip("Viaggio a Roma", "Descrizione", 1000.0, LocalDate.of(2023, 6, 1), 5, 20, 2);
        when(travelerService.hasCompletedTrip(traveler, trip)).thenReturn(true);

        // Act
        boolean result = travelerController.leaveReviewForTrip(trip, 5, "Ottimo viaggio");

        // Assert
        assertTrue(result);
        verify(reviewService).createAndAddReview(5, "Ottimo viaggio", trip, traveler);
    }

    @Test
    public void testLeaveReviewForTrip_NotCompletedTrip() {
        // Arrange
        Trip trip = new Trip("Viaggio a Roma", "Descrizione", 1000.0, LocalDate.of(2023, 6, 1), 5, 20, 2);
        when(travelerService.hasCompletedTrip(traveler, trip)).thenReturn(false);

        // Act
        boolean result = travelerController.leaveReviewForTrip(trip, 5, "Ottimo viaggio");

        // Assert
        assertFalse(result);
        verify(reviewService, never()).createAndAddReview(anyInt(), anyString(), any(), any());
    }

    @Test
    public void testLeaveReviewForGuide_Success() {
        // Arrange
        Guide guide = mock(Guide.class);
        when(travelerService.hasMetGuide(traveler, guide)).thenReturn(true);

        // Act
        boolean result = travelerController.leaveReviewForGuide(guide, 5, "Ottima guida");

        // Assert
        assertTrue(result);
        verify(reviewService).createAndAddReview(5, "Ottima guida", guide, traveler);
    }

    @Test
    public void testLeaveReviewForGuide_NoInteraction() {
        // Arrange
        Guide guide = mock(Guide.class);
        when(travelerService.hasMetGuide(traveler, guide)).thenReturn(false);

        // Act
        boolean result = travelerController.leaveReviewForGuide(guide, 5, "Ottima guida");

        // Assert
        assertFalse(result);
        verify(reviewService, never()).createAndAddReview(anyInt(), anyString(), any(), any());
    }

    @Test
    public void testGetNotifications() {
        // Arrange
        List<Notification> mockNotifications = new ArrayList<>();
        when(notificationService.getNotificationsByRecipient(traveler)).thenReturn(mockNotifications);

        // Act
        List<Notification> result = travelerController.getNotifications();

        // Assert
        assertEquals(mockNotifications, result);
        verify(notificationService).getNotificationsByRecipient(traveler);
    }

    @Test
    public void testMarkNotificationAsRead() {
        // Arrange
        int notificationId = 1;
        when(notificationService.markAsReadById(notificationId)).thenReturn(true);

        // Act
        boolean result = travelerController.markNotificationAsRead(notificationId);

        // Assert
        assertTrue(result);
        verify(notificationService).markAsReadById(notificationId);
    }

    @Test
    public void testMarkAllNotificationsAsRead() {
        // Act
        travelerController.markAllNotificationsAsRead();

        // Assert
        verify(notificationService).markAllAsRead(traveler);
    }
}
