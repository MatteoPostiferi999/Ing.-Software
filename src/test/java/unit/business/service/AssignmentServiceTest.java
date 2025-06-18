package unit.business.service;

import business.service.AssignmentService;
import business.service.NotificationService;
import dao.interfaces.AssignmentDAO;
import dao.interfaces.GuideDAO;
import dao.interfaces.TripDAO;
import model.application.Application;
import model.application.ApplicationRegister;
import model.assignment.Assignment;
import model.assignment.AssignmentRegister;
import model.booking.BookingRegister;
import model.trip.Trip;
import model.user.Guide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssignmentServiceTest {

    @Mock private ApplicationRegister applicationRegister;
    @Mock private AssignmentDAO assignmentDAO;
    @Mock private NotificationService notificationService;
    @Mock private GuideDAO guideDAO;
    @Mock private TripDAO tripDAO;
    @Mock private Guide guide;
    @Mock private Trip trip;
    @Mock private Assignment assignment;
    @Mock private AssignmentRegister assignmentRegister;
    @Mock private BookingRegister bookingRegister;

    private AssignmentService assignmentService;

    @BeforeEach
    void setUp() {
        assignmentService = new AssignmentService(applicationRegister, assignmentDAO,
                notificationService, guideDAO, tripDAO);
    }

    @Test
    void testAssignBestGuidesToTrip() {
        // Arrange
        List<Application> acceptedApplications = new ArrayList<>();
        Application app = mock(Application.class);
        acceptedApplications.add(app);

        when(applicationRegister.getAcceptedApplicationsForTrip(trip)).thenReturn(acceptedApplications);
        when(trip.getMaxGuides()).thenReturn(2);
        when(trip.getAssignmentRegister()).thenReturn(assignmentRegister);
        when(assignmentRegister.getAssignments()).thenReturn(new ArrayList<>());
        when(app.getGuide()).thenReturn(guide);
        when(guide.getRating()).thenReturn(4.5);
        when(trip.getTitle()).thenReturn("Test Trip");

        // Act
        assignmentService.assignBestGuidesToTrip(trip);

        // Assert
        verify(assignmentDAO).save(any(Assignment.class));
        verify(notificationService).sendNotification(eq(guide), contains("assigned to the trip"));
    }

    @Test
    void testAssignGuideToTrip_Success() {
        // Arrange
        when(trip.getAssignmentRegister()).thenReturn(assignmentRegister);
        when(assignmentRegister.canAddMoreGuides()).thenReturn(true);
        when(guide.getGuideId()).thenReturn(1);
        when(trip.getTripId()).thenReturn(1);
        when(assignmentDAO.findByGuideAndTrip(1, 1)).thenReturn(null);
        when(trip.getTitle()).thenReturn("Test Trip");

        // Act
        Assignment result = assignmentService.assignGuideToTrip(guide, trip);

        // Assert
        assertNotNull(result);
        verify(assignmentDAO).save(any(Assignment.class));
        verify(assignmentRegister).addAssignment(any(Assignment.class));
        verify(notificationService).sendNotification(eq(guide), contains("Sei stato assegnato al viaggio"));
    }

    @Test
    void testAssignGuideToTrip_WhenTripIsFull() {
        // Arrange
        when(trip.getAssignmentRegister()).thenReturn(assignmentRegister);
        when(assignmentRegister.canAddMoreGuides()).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                assignmentService.assignGuideToTrip(guide, trip));
        verify(assignmentDAO, never()).save(any(Assignment.class));
    }

    @Test
    void testAssignGuideToTrip_WhenGuideAlreadyAssigned() {
        // Arrange
        when(trip.getAssignmentRegister()).thenReturn(assignmentRegister);
        when(assignmentRegister.canAddMoreGuides()).thenReturn(true);
        when(guide.getGuideId()).thenReturn(1);
        when(trip.getTripId()).thenReturn(1);
        when(assignmentDAO.findByGuideAndTrip(1, 1)).thenReturn(assignment);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                assignmentService.assignGuideToTrip(guide, trip));
        verify(assignmentDAO, never()).save(any(Assignment.class));
    }

    @Test
    void testRemoveGuideFromTrip_Success() {
        // Arrange
        when(guide.getGuideId()).thenReturn(1);
        when(trip.getTripId()).thenReturn(1);
        when(trip.getTitle()).thenReturn("Test Trip");
        when(assignmentDAO.findByGuideAndTrip(1, 1)).thenReturn(assignment);
        when(trip.getAssignmentRegister()).thenReturn(assignmentRegister);

        // Act
        boolean result = assignmentService.removeGuideFromTrip(guide, trip);

        // Assert
        assertTrue(result);
        verify(assignmentDAO).delete(assignment);
        verify(assignmentRegister).removeAssignment(assignment);
        verify(notificationService).sendNotification(eq(guide), contains("rimosso dal viaggio"));
    }

    @Test
    void testRemoveGuideFromTrip_WhenNotAssigned() {
        // Arrange
        when(guide.getGuideId()).thenReturn(1);
        when(trip.getTripId()).thenReturn(1);
        when(assignmentDAO.findByGuideAndTrip(1, 1)).thenReturn(null);

        // Act
        boolean result = assignmentService.removeGuideFromTrip(guide, trip);

        // Assert
        assertFalse(result);
        verify(assignmentDAO, never()).delete(any(Assignment.class));
        verify(notificationService, never()).sendNotification(any(), any());
    }

    @Test
    void testIsGuideAssignedToTrip() {
        // Arrange
        when(assignmentDAO.findByGuideAndTrip(1, 1)).thenReturn(assignment);

        // Act
        boolean result = assignmentService.isGuideAssignedToTrip(1, 1);

        // Assert
        assertTrue(result);
    }

    @Test
    void testGetAllAssignments() {
        // Arrange
        List<Assignment> expectedAssignments = Arrays.asList(assignment);
        when(assignmentDAO.findAll()).thenReturn(expectedAssignments);

        // Act
        List<Assignment> result = assignmentService.getAllAssignments();

        // Assert
        assertEquals(expectedAssignments, result);
    }

    @Test
    void testLoadAssignmentsForTrip() {
        // Arrange
        when(trip.getAssignmentRegister()).thenReturn(assignmentRegister);
        List<Assignment> expectedAssignments = Arrays.asList(assignment);
        when(assignmentRegister.getAssignments()).thenReturn(expectedAssignments);

        // Act
        List<Assignment> result = assignmentService.loadAssignmentsForTrip(trip);

        // Assert
        assertEquals(expectedAssignments, result);
        verify(assignmentDAO).loadAssignmentsForTrip(trip);
    }

    @Test
    void testGetTripsForGuide() {
        // Arrange
        when(guide.getGuideId()).thenReturn(1);
        Assignment assignment = mock(Assignment.class);
        when(assignment.getTripId()).thenReturn(1);
        when(assignment.getTrip()).thenReturn(null);
        List<Assignment> assignments = Arrays.asList(assignment);
        when(assignmentDAO.findByGuideId(1)).thenReturn(assignments);
        when(tripDAO.findById(1)).thenReturn(trip);

        // Act
        List<Trip> result = assignmentService.getTripsForGuide(guide);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(trip, result.get(0));
        verify(tripDAO).findById(1);
    }
}