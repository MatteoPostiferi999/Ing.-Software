package unit.business.service;

import business.service.TripService;
import dao.interfaces.*;
import model.trip.Trip;
import model.trip.Activity;
import model.assignment.AssignmentRegister;
import model.booking.BookingRegister;
import model.booking.Booking;
import model.assignment.Assignment;
import model.user.Guide;
import model.user.Traveler;
import business.service.NotificationService;

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

class TripServiceTest {

    @Mock
    private TripDAO tripDAO;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ActivityDAO activityDAO;

    @Mock
    private BookingDAO bookingDAO;

    @Mock
    private AssignmentDAO assignmentDAO;

    @Mock
    private ApplicationDAO applicationDAO;

    @Mock
    private Trip trip;

    @Mock
    private Activity activity;

    @Mock
    private Booking booking;

    @Mock
    private Assignment assignment;

    @Mock
    private Guide guide;

    @Mock
    private Traveler traveler;

    @Mock
    private AssignmentRegister assignmentRegister;

    @Mock
    private BookingRegister bookingRegister;

    private TripService tripService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tripService = new TripService(tripDAO, notificationService, activityDAO,
                bookingDAO, assignmentDAO, applicationDAO);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create service with minimal constructor")
        void shouldCreateServiceWithMinimalConstructor() {
            // When
            TripService service = new TripService(tripDAO, notificationService);

            // Then
            assertNotNull(service);
        }

        @Test
        @DisplayName("Should create service with full constructor")
        void shouldCreateServiceWithFullConstructor() {
            // When
            TripService service = new TripService(tripDAO, notificationService,
                    activityDAO, bookingDAO,
                    assignmentDAO, applicationDAO);

            // Then
            assertNotNull(service);
        }

        @Test
        @DisplayName("Should set DAOs using setters")
        void shouldSetDAOsUsingSetters() {
            // Given
            TripService service = new TripService(tripDAO, notificationService);

            // When
            service.setActivityDAO(activityDAO);
            service.setBookingDAO(bookingDAO);
            service.setAssignmentDAO(assignmentDAO);
            service.setApplicationDAO(applicationDAO);

            // Then
            assertNotNull(service);
        }
    }

    @Nested
    @DisplayName("Basic CRUD Operations")
    class BasicCrudOperations {

        @Test
        @DisplayName("Should add trip successfully")
        void shouldAddTrip() {
            // When
            tripService.addTrip(trip);

            // Then
            verify(tripDAO, times(1)).save(trip);
        }

        @Test
        @DisplayName("Should get trip by id")
        void shouldGetTripById() {
            // Given
            int tripId = 1;
            when(tripDAO.findById(tripId)).thenReturn(trip);

            // When
            Trip result = tripService.getTripById(tripId);

            // Then
            assertEquals(trip, result);
            verify(tripDAO, times(1)).findById(tripId);
        }

        @Test
        @DisplayName("Should get all trips")
        void shouldGetAllTrips() {
            // Given
            List<Trip> expectedTrips = Arrays.asList(trip);
            when(tripDAO.findAll()).thenReturn(expectedTrips);

            // When
            List<Trip> result = tripService.getAllTrips();

            // Then
            assertEquals(expectedTrips, result);
            verify(tripDAO, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Complete Trip Loading")
    class CompleteTripLoading {

        @Test
        @DisplayName("Should load complete trip with all relations")
        void shouldLoadCompleteTripWithAllRelations() {
            // Given
            int tripId = 1;
            List<Integer> activityIds = Arrays.asList(1, 2);
            List<Activity> activities = Arrays.asList(activity);

            when(tripDAO.findById(tripId)).thenReturn(trip);
            when(trip.getActivityIds()).thenReturn(activityIds);
            when(trip.getPlannedActivities()).thenReturn(new ArrayList<>());
            when(activityDAO.findById(anyInt())).thenReturn(activity);
            when(activityDAO.findByTripId(tripId)).thenReturn(activities);

            // When
            Trip result = tripService.getCompleteTripById(tripId);

            // Then
            assertEquals(trip, result);
            verify(tripDAO, times(1)).findById(tripId);
            verify(bookingDAO, times(1)).loadBookingsForTrip(trip);
            verify(assignmentDAO, times(1)).loadAssignmentsForTrip(trip);
            verify(applicationDAO, times(1)).loadApplicationsForTrip(trip);
        }

        @Test
        @DisplayName("Should return null when trip not found")
        void shouldReturnNullWhenTripNotFound() {
            // Given
            int tripId = 1;
            when(tripDAO.findById(tripId)).thenReturn(null);

            // When
            Trip result = tripService.getCompleteTripById(tripId);

            // Then
            assertNull(result);
            verify(tripDAO, times(1)).findById(tripId);
            verify(bookingDAO, never()).loadBookingsForTrip(any());
        }

        @Test
        @DisplayName("Should load activities by trip id when activity ids are null")
        void shouldLoadActivitiesByTripIdWhenActivityIdsAreNull() {
            // Given
            int tripId = 1;
            List<Activity> activities = Arrays.asList(activity);

            when(tripDAO.findById(tripId)).thenReturn(trip);
            when(trip.getActivityIds()).thenReturn(null);
            when(trip.getTripId()).thenReturn(tripId);
            when(activityDAO.findByTripId(tripId)).thenReturn(activities);

            // When
            tripService.getCompleteTripById(tripId);

            // Then
            verify(activityDAO, times(1)).findByTripId(tripId);
            verify(trip, times(1)).addPlannedActivity(activity);
        }

        @Test
        @DisplayName("Should get all complete trips")
        void shouldGetAllCompleteTrips() {
            // Given
            List<Trip> trips = Arrays.asList(trip);
            when(tripDAO.findAll()).thenReturn(trips);
            when(trip.getActivityIds()).thenReturn(new ArrayList<>());

            // When
            List<Trip> result = tripService.getAllCompleteTrips();

            // Then
            assertEquals(trips, result);
            verify(tripDAO, times(1)).findAll();
            verify(bookingDAO, times(1)).loadBookingsForTrip(trip);
            verify(assignmentDAO, times(1)).loadAssignmentsForTrip(trip);
            verify(applicationDAO, times(1)).loadApplicationsForTrip(trip);
        }
    }

    @Nested
    @DisplayName("Update Trip")
    class UpdateTrip {

        @Test
        @DisplayName("Should update trip and send notifications")
        void shouldUpdateTripAndSendNotifications() {
            // Given
            String tripTitle = "Test Trip";
            List<Assignment> assignments = Arrays.asList(assignment);
            List<Booking> bookings = Arrays.asList(booking);

            when(trip.isAlreadyStarted()).thenReturn(false);
            when(trip.getTitle()).thenReturn(tripTitle);
            when(trip.getAssignmentRegister()).thenReturn(assignmentRegister);
            when(trip.getBookingRegister()).thenReturn(bookingRegister);
            when(assignmentRegister.getAllAssignments()).thenReturn(assignments);
            when(bookingRegister.getAllBookings()).thenReturn(bookings);
            when(assignment.getGuide()).thenReturn(guide);
            when(booking.getTraveler()).thenReturn(traveler);

            // When
            tripService.updateTrip(trip);

            // Then
            verify(tripDAO, times(1)).update(trip);
            verify(notificationService, times(1)).sendNotification(guide, "Trip details updated: " + tripTitle);
            verify(notificationService, times(1)).sendNotification(traveler, "Trip details updated: " + tripTitle);
        }

        @Test
        @DisplayName("Should throw exception when updating started trip")
        void shouldThrowExceptionWhenUpdatingStartedTrip() {
            // Given
            when(trip.isAlreadyStarted()).thenReturn(true);

            // When & Then
            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> tripService.updateTrip(trip));

            assertEquals("Cannot update a trip that has already started.", exception.getMessage());
            verify(tripDAO, never()).update(trip);
            verify(notificationService, never()).sendNotification(any(), any());
        }

        @Test
        @DisplayName("Should handle empty assignments and bookings")
        void shouldHandleEmptyAssignmentsAndBookings() {
            // Given
            when(trip.isAlreadyStarted()).thenReturn(false);
            when(trip.getAssignmentRegister()).thenReturn(assignmentRegister);
            when(trip.getBookingRegister()).thenReturn(bookingRegister);
            when(assignmentRegister.getAllAssignments()).thenReturn(new ArrayList<>());
            when(bookingRegister.getAllBookings()).thenReturn(new ArrayList<>());

            // When
            tripService.updateTrip(trip);

            // Then
            verify(tripDAO, times(1)).update(trip);
            verify(notificationService, never()).sendNotification(any(), any());
        }
    }

    @Nested
    @DisplayName("Delete Trip")
    class DeleteTrip {

        @Test
        @DisplayName("Should delete trip and send notifications")
        void shouldDeleteTripAndSendNotifications() {
            // Given
            int tripId = 1;
            String tripTitle = "Test Trip";
            List<Assignment> assignments = Arrays.asList(assignment);
            List<Booking> bookings = Arrays.asList(booking);

            when(tripDAO.findById(tripId)).thenReturn(trip);
            when(trip.isAlreadyStarted()).thenReturn(false);
            when(trip.getTitle()).thenReturn(tripTitle);
            when(trip.getActivityIds()).thenReturn(new ArrayList<>());
            when(trip.getAssignmentRegister()).thenReturn(assignmentRegister);
            when(trip.getBookingRegister()).thenReturn(bookingRegister);
            when(assignmentRegister.getAllAssignments()).thenReturn(assignments);
            when(bookingRegister.getAllBookings()).thenReturn(bookings);
            when(assignment.getGuide()).thenReturn(guide);
            when(booking.getTraveler()).thenReturn(traveler);

            // When
            tripService.deleteTrip(tripId);

            // Then
            verify(tripDAO, times(1)).deleteById(tripId);
            verify(notificationService, times(1)).sendNotification(guide, "Trip has been cancelled: " + tripTitle);
            verify(notificationService, times(1)).sendNotification(traveler, "Trip has been cancelled: " + tripTitle);
        }

        @Test
        @DisplayName("Should throw exception when deleting started trip")
        void shouldThrowExceptionWhenDeletingStartedTrip() {
            // Given
            int tripId = 1;
            when(tripDAO.findById(tripId)).thenReturn(trip);
            when(trip.isAlreadyStarted()).thenReturn(true);
            when(trip.getActivityIds()).thenReturn(new ArrayList<>());

            // When & Then
            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> tripService.deleteTrip(tripId));

            assertEquals("Cannot delete a trip that has already started.", exception.getMessage());
            verify(tripDAO, never()).deleteById(tripId);
            verify(notificationService, never()).sendNotification(any(), any());
        }

        @Test
        @DisplayName("Should handle deletion of non-existing trip")
        void shouldHandleDeletionOfNonExistingTrip() {
            // Given
            int tripId = 1;
            when(tripDAO.findById(tripId)).thenReturn(null);

            // When
            tripService.deleteTrip(tripId);

            // Then
            verify(tripDAO, never()).deleteById(tripId);
            verify(notificationService, never()).sendNotification(any(), any());
        }

        @Test
        @DisplayName("Should handle trip with no assignments or bookings")
        void shouldHandleTripWithNoAssignmentsOrBookings() {
            // Given
            int tripId = 1;
            when(tripDAO.findById(tripId)).thenReturn(trip);
            when(trip.isAlreadyStarted()).thenReturn(false);
            when(trip.getActivityIds()).thenReturn(new ArrayList<>());
            when(trip.getAssignmentRegister()).thenReturn(assignmentRegister);
            when(trip.getBookingRegister()).thenReturn(bookingRegister);
            when(assignmentRegister.getAllAssignments()).thenReturn(new ArrayList<>());
            when(bookingRegister.getAllBookings()).thenReturn(new ArrayList<>());

            // When
            tripService.deleteTrip(tripId);

            // Then
            verify(tripDAO, times(1)).deleteById(tripId);
            verify(notificationService, never()).sendNotification(any(), any());
        }
    }

    @Nested
    @DisplayName("Relation Loading Edge Cases")
    class RelationLoadingEdgeCases {

        @Test
        @DisplayName("Should handle null activity DAO")
        void shouldHandleNullActivityDAO() {
            // Given
            TripService serviceWithNullDAO = new TripService(tripDAO, notificationService);
            int tripId = 1;
            when(tripDAO.findById(tripId)).thenReturn(trip);

            // When
            Trip result = serviceWithNullDAO.getCompleteTripById(tripId);

            // Then
            assertNotNull(result);
            verify(tripDAO, times(1)).findById(tripId);
        }

        @Test
        @DisplayName("Should handle null booking DAO")
        void shouldHandleNullBookingDAO() {
            // Given
            TripService serviceWithNullDAO = new TripService(tripDAO, notificationService);
            serviceWithNullDAO.setActivityDAO(activityDAO);
            int tripId = 1;
            when(tripDAO.findById(tripId)).thenReturn(trip);
            when(trip.getActivityIds()).thenReturn(new ArrayList<>());

            // When
            Trip result = serviceWithNullDAO.getCompleteTripById(tripId);

            // Then
            assertNotNull(result);
            verify(tripDAO, times(1)).findById(tripId);
        }

        @Test
        @DisplayName("Should handle null assignment DAO")
        void shouldHandleNullAssignmentDAO() {
            // Given
            TripService serviceWithNullDAO = new TripService(tripDAO, notificationService);
            serviceWithNullDAO.setActivityDAO(activityDAO);
            serviceWithNullDAO.setBookingDAO(bookingDAO);
            int tripId = 1;
            when(tripDAO.findById(tripId)).thenReturn(trip);
            when(trip.getActivityIds()).thenReturn(new ArrayList<>());

            // When
            Trip result = serviceWithNullDAO.getCompleteTripById(tripId);

            // Then
            assertNotNull(result);
            verify(tripDAO, times(1)).findById(tripId);
        }

        @Test
        @DisplayName("Should handle null application DAO")
        void shouldHandleNullApplicationDAO() {
            // Given
            TripService serviceWithNullDAO = new TripService(tripDAO, notificationService);
            serviceWithNullDAO.setActivityDAO(activityDAO);
            serviceWithNullDAO.setBookingDAO(bookingDAO);
            serviceWithNullDAO.setAssignmentDAO(assignmentDAO);
            int tripId = 1;
            when(tripDAO.findById(tripId)).thenReturn(trip);
            when(trip.getActivityIds()).thenReturn(new ArrayList<>());

            // When
            Trip result = serviceWithNullDAO.getCompleteTripById(tripId);

            // Then
            assertNotNull(result);
            verify(tripDAO, times(1)).findById(tripId);
        }

        @Test
        @DisplayName("Should handle null activity when loading by id")
        void shouldHandleNullActivityWhenLoadingById() {
            // Given
            int tripId = 1;
            List<Integer> activityIds = Arrays.asList(1, 2);

            when(tripDAO.findById(tripId)).thenReturn(trip);
            when(trip.getActivityIds()).thenReturn(activityIds);
            when(trip.getPlannedActivities()).thenReturn(new ArrayList<>());
            when(activityDAO.findById(1)).thenReturn(activity);
            when(activityDAO.findById(2)).thenReturn(null); // Null activity

            // When
            Trip result = tripService.getCompleteTripById(tripId);

            // Then
            assertNotNull(result);
            verify(activityDAO, times(1)).findById(1);
            verify(activityDAO, times(1)).findById(2);
            verify(trip.getPlannedActivities(), times(1)).add(activity);
        }
    }
}