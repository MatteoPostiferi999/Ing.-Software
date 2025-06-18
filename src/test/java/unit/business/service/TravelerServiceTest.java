package unit.business.service;

import business.service.TravelerService;
import dao.interfaces.TravelerDAO;
import dao.interfaces.BookingDAO;
import dao.interfaces.AssignmentDAO;
import model.user.Traveler;
import model.user.Guide;
import model.booking.Booking;
import model.trip.Trip;
import model.assignment.AssignmentRegister;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TravelerServiceTest {

    @Mock
    private TravelerDAO travelerDAO;

    @Mock
    private BookingDAO bookingDAO;

    @Mock
    private AssignmentDAO assignmentDAO;

    @Mock
    private Traveler traveler;

    @Mock
    private Guide guide;

    @Mock
    private Trip trip;

    @Mock
    private Booking booking;

    @Mock
    private AssignmentRegister assignmentRegister;

    private TravelerService travelerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        travelerService = new TravelerService(travelerDAO, bookingDAO, assignmentDAO);
    }

    @Nested
    @DisplayName("CRUD Operations")
    class CrudOperations {

        @Test
        @DisplayName("Should add traveler successfully")
        void shouldAddTraveler() {
            // When
            travelerService.addTraveler(traveler);

            // Then
            verify(travelerDAO, times(1)).save(traveler);
        }

        @Test
        @DisplayName("Should get traveler by id")
        void shouldGetTravelerById() {
            // Given
            int travelerId = 1;
            when(travelerDAO.findById(travelerId)).thenReturn(traveler);

            // When
            Traveler result = travelerService.getTravelerById(travelerId);

            // Then
            assertEquals(traveler, result);
            verify(travelerDAO, times(1)).findById(travelerId);
        }

        @Test
        @DisplayName("Should update traveler successfully")
        void shouldUpdateTraveler() {
            // When
            travelerService.updateTraveler(traveler);

            // Then
            verify(travelerDAO, times(1)).update(traveler);
        }

        @Test
        @DisplayName("Should delete traveler by id")
        void shouldDeleteTravelerById() {
            // Given
            int travelerId = 1;

            // When
            travelerService.deleteTraveler(travelerId);

            // Then
            verify(travelerDAO, times(1)).delete(travelerId);
        }
    }

    @Nested
    @DisplayName("Get Bookings")
    class GetBookings {

        @Test
        @DisplayName("Should return bookings for valid traveler")
        void shouldReturnBookingsForValidTraveler() {
            // Given
            List<Booking> expectedBookings = Arrays.asList(booking);
            when(bookingDAO.getByTraveler(traveler)).thenReturn(expectedBookings);

            // When
            List<Booking> result = travelerService.getBookings(traveler);

            // Then
            assertEquals(expectedBookings, result);
            verify(bookingDAO, times(1)).getByTraveler(traveler);
        }

        @Test
        @DisplayName("Should return empty list for null traveler")
        void shouldReturnEmptyListForNullTraveler() {
            // When
            List<Booking> result = travelerService.getBookings(null);

            // Then
            assertTrue(result.isEmpty());
            verify(bookingDAO, never()).getByTraveler(any());
        }
    }

    @Nested
    @DisplayName("Has Completed Trip")
    class HasCompletedTrip {

        @Test
        @DisplayName("Should return true when traveler completed trip")
        void shouldReturnTrueWhenTravelerCompletedTrip() {
            // Given
            LocalDate pastDate = LocalDate.now().minusDays(5);
            int tripId = 1;

            when(trip.getDate()).thenReturn(pastDate);
            when(trip.getTripId()).thenReturn(tripId);
            when(booking.getTrip()).thenReturn(trip);
            when(bookingDAO.getByTraveler(traveler)).thenReturn(Arrays.asList(booking));

            // When
            boolean result = travelerService.hasCompletedTrip(traveler, trip);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when trip is in future")
        void shouldReturnFalseWhenTripIsInFuture() {
            // Given
            LocalDate futureDate = LocalDate.now().plusDays(5);
            when(trip.getDate()).thenReturn(futureDate);

            // When
            boolean result = travelerService.hasCompletedTrip(traveler, trip);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when traveler has no booking for trip")
        void shouldReturnFalseWhenTravelerHasNoBookingForTrip() {
            // Given
            LocalDate pastDate = LocalDate.now().minusDays(5);
            int tripId = 1;
            int differentTripId = 2;

            when(trip.getDate()).thenReturn(pastDate);
            when(trip.getTripId()).thenReturn(tripId);
            when(booking.getTrip()).thenReturn(trip);
            when(booking.getTrip().getTripId()).thenReturn(differentTripId);
            when(bookingDAO.getByTraveler(traveler)).thenReturn(Arrays.asList(booking));

            // When
            boolean result = travelerService.hasCompletedTrip(traveler, trip);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for null traveler")
        void shouldReturnFalseForNullTraveler() {
            // When
            boolean result = travelerService.hasCompletedTrip(null, trip);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for null trip")
        void shouldReturnFalseForNullTrip() {
            // When
            boolean result = travelerService.hasCompletedTrip(traveler, null);

            // Then
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Has Met Guide")
    class HasMetGuide {

        @Test
        @DisplayName("Should return true when traveler met guide in past trip")
        void shouldReturnTrueWhenTravelerMetGuideInPastTrip() {
            // Given
            LocalDate pastDate = LocalDate.now().minusDays(5);

            when(trip.getDate()).thenReturn(pastDate);
            when(trip.getAssignmentRegister()).thenReturn(assignmentRegister);
            when(assignmentRegister.hasGuide(guide)).thenReturn(true);
            when(booking.getTrip()).thenReturn(trip);
            when(bookingDAO.getByTraveler(traveler)).thenReturn(Arrays.asList(booking));

            // When
            boolean result = travelerService.hasMetGuide(traveler, guide);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when trip is in future")
        void shouldReturnFalseWhenTripIsInFuture() {
            // Given
            LocalDate futureDate = LocalDate.now().plusDays(5);

            when(trip.getDate()).thenReturn(futureDate);
            when(booking.getTrip()).thenReturn(trip);
            when(bookingDAO.getByTraveler(traveler)).thenReturn(Arrays.asList(booking));

            // When
            boolean result = travelerService.hasMetGuide(traveler, guide);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when guide was not assigned to trip")
        void shouldReturnFalseWhenGuideWasNotAssignedToTrip() {
            // Given
            LocalDate pastDate = LocalDate.now().minusDays(5);

            when(trip.getDate()).thenReturn(pastDate);
            when(trip.getAssignmentRegister()).thenReturn(assignmentRegister);
            when(assignmentRegister.hasGuide(guide)).thenReturn(false);
            when(booking.getTrip()).thenReturn(trip);
            when(bookingDAO.getByTraveler(traveler)).thenReturn(Arrays.asList(booking));

            // When
            boolean result = travelerService.hasMetGuide(traveler, guide);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for null traveler")
        void shouldReturnFalseForNullTraveler() {
            // When
            boolean result = travelerService.hasMetGuide(null, guide);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for null guide")
        void shouldReturnFalseForNullGuide() {
            // When
            boolean result = travelerService.hasMetGuide(traveler, null);

            // Then
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Get Upcoming Trips")
    class GetUpcomingTrips {

        @Test
        @DisplayName("Should return upcoming trips including today")
        void shouldReturnUpcomingTripsIncludingToday() {
            // Given
            LocalDate today = LocalDate.now();
            LocalDate futureDate = LocalDate.now().plusDays(5);

            Trip todayTrip = mock(Trip.class);
            Trip futureTrip = mock(Trip.class);
            Booking todayBooking = mock(Booking.class);
            Booking futureBooking = mock(Booking.class);

            when(todayTrip.getDate()).thenReturn(today);
            when(futureTrip.getDate()).thenReturn(futureDate);
            when(todayBooking.getTrip()).thenReturn(todayTrip);
            when(futureBooking.getTrip()).thenReturn(futureTrip);
            when(bookingDAO.getByTraveler(traveler)).thenReturn(Arrays.asList(todayBooking, futureBooking));

            // When
            List<Trip> result = travelerService.getUpcomingTrips(traveler);

            // Then
            assertEquals(2, result.size());
            assertTrue(result.contains(todayTrip));
            assertTrue(result.contains(futureTrip));
        }

        @Test
        @DisplayName("Should exclude past trips")
        void shouldExcludePastTrips() {
            // Given
            LocalDate pastDate = LocalDate.now().minusDays(5);
            LocalDate futureDate = LocalDate.now().plusDays(5);

            Trip pastTrip = mock(Trip.class);
            Trip futureTrip = mock(Trip.class);
            Booking pastBooking = mock(Booking.class);
            Booking futureBooking = mock(Booking.class);

            when(pastTrip.getDate()).thenReturn(pastDate);
            when(futureTrip.getDate()).thenReturn(futureDate);
            when(pastBooking.getTrip()).thenReturn(pastTrip);
            when(futureBooking.getTrip()).thenReturn(futureTrip);
            when(bookingDAO.getByTraveler(traveler)).thenReturn(Arrays.asList(pastBooking, futureBooking));

            // When
            List<Trip> result = travelerService.getUpcomingTrips(traveler);

            // Then
            assertEquals(1, result.size());
            assertTrue(result.contains(futureTrip));
            assertFalse(result.contains(pastTrip));
        }

        @Test
        @DisplayName("Should return empty list for null traveler")
        void shouldReturnEmptyListForNullTraveler() {
            // When
            List<Trip> result = travelerService.getUpcomingTrips(null);

            // Then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Get Past Trips")
    class GetPastTrips {

        @Test
        @DisplayName("Should return only past trips")
        void shouldReturnOnlyPastTrips() {
            // Given
            LocalDate pastDate = LocalDate.now().minusDays(5);
            LocalDate futureDate = LocalDate.now().plusDays(5);

            Trip pastTrip = mock(Trip.class);
            Trip futureTrip = mock(Trip.class);
            Booking pastBooking = mock(Booking.class);
            Booking futureBooking = mock(Booking.class);

            when(pastTrip.getDate()).thenReturn(pastDate);
            when(futureTrip.getDate()).thenReturn(futureDate);
            when(pastBooking.getTrip()).thenReturn(pastTrip);
            when(futureBooking.getTrip()).thenReturn(futureTrip);
            when(bookingDAO.getByTraveler(traveler)).thenReturn(Arrays.asList(pastBooking, futureBooking));

            // When
            List<Trip> result = travelerService.getPastTrips(traveler);

            // Then
            assertEquals(1, result.size());
            assertTrue(result.contains(pastTrip));
            assertFalse(result.contains(futureTrip));
        }

        @Test
        @DisplayName("Should exclude today's trips")
        void shouldExcludeTodaysTrips() {
            // Given
            LocalDate today = LocalDate.now();
            LocalDate pastDate = LocalDate.now().minusDays(5);

            Trip todayTrip = mock(Trip.class);
            Trip pastTrip = mock(Trip.class);
            Booking todayBooking = mock(Booking.class);
            Booking pastBooking = mock(Booking.class);

            when(todayTrip.getDate()).thenReturn(today);
            when(pastTrip.getDate()).thenReturn(pastDate);
            when(todayBooking.getTrip()).thenReturn(todayTrip);
            when(pastBooking.getTrip()).thenReturn(pastTrip);
            when(bookingDAO.getByTraveler(traveler)).thenReturn(Arrays.asList(todayBooking, pastBooking));

            // When
            List<Trip> result = travelerService.getPastTrips(traveler);

            // Then
            assertEquals(1, result.size());
            assertTrue(result.contains(pastTrip));
            assertFalse(result.contains(todayTrip));
        }

        @Test
        @DisplayName("Should return empty list for null traveler")
        void shouldReturnEmptyListForNullTraveler() {
            // When
            List<Trip> result = travelerService.getPastTrips(null);

            // Then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Can Cancel Booking")
    class CanCancelBooking {

        @Test
        @DisplayName("Should return true when booking can be cancelled")
        void shouldReturnTrueWhenBookingCanBeCancelled() {
            // Given
            int travelerId = 1;
            LocalDate futureDate = LocalDate.now().plusDays(5);

            when(traveler.getTravelerId()).thenReturn(travelerId);
            when(booking.getTraveler()).thenReturn(traveler);
            when(booking.getTraveler().getTravelerId()).thenReturn(travelerId);
            when(booking.getTrip()).thenReturn(trip);
            when(trip.getDate()).thenReturn(futureDate);

            // When
            boolean result = travelerService.canCancelBooking(traveler, booking);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when booking belongs to different traveler")
        void shouldReturnFalseWhenBookingBelongsToDifferentTraveler() {
            // Given
            int travelerId = 1;
            int differentTravelerId = 2;

            when(traveler.getTravelerId()).thenReturn(travelerId);
            when(booking.getTraveler()).thenReturn(traveler);
            when(booking.getTraveler().getTravelerId()).thenReturn(differentTravelerId);

            // When
            boolean result = travelerService.canCancelBooking(traveler, booking);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false when trip has already started")
        void shouldReturnFalseWhenTripHasAlreadyStarted() {
            // Given
            int travelerId = 1;
            LocalDate pastDate = LocalDate.now().minusDays(1);

            when(traveler.getTravelerId()).thenReturn(travelerId);
            when(booking.getTraveler()).thenReturn(traveler);
            when(booking.getTraveler().getTravelerId()).thenReturn(travelerId);
            when(booking.getTrip()).thenReturn(trip);
            when(trip.getDate()).thenReturn(pastDate);

            // When
            boolean result = travelerService.canCancelBooking(traveler, booking);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for null traveler")
        void shouldReturnFalseForNullTraveler() {
            // When
            boolean result = travelerService.canCancelBooking(null, booking);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for null booking")
        void shouldReturnFalseForNullBooking() {
            // When
            boolean result = travelerService.canCancelBooking(traveler, null);

            // Then
            assertFalse(result);
        }
    }
}