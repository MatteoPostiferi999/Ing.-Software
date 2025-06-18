package unit.business.controller;

import business.controller.GuideController;
import business.service.*;
import model.application.Application;
import model.application.ApplicationStatus;
import model.trip.Trip;
import model.user.Guide;
import model.user.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GuideControllerTest {

    @Mock
    private Guide guide;

    @Mock
    private ViewTripsService viewTripsService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private GuideService guideService;

    private GuideController guideController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        guideController = new GuideController(guide, viewTripsService, applicationService, notificationService, guideService);
    }

    @Test
    public void testViewAvailableTrips() {
        // Arrange
        LocalDate minDate = LocalDate.of(2023, 1, 1);
        LocalDate maxDate = LocalDate.of(2023, 12, 31);
        List<Trip> mockTrips = Arrays.asList(new Trip("Viaggio a Roma", "Descrizione", 1000.0, LocalDate.of(2023, 6, 1), 5, 20, 2));
        when(viewTripsService.viewTrips()).thenReturn(mockTrips);

        // Act
        List<Trip> result = guideController.viewAvailableTrips(minDate, maxDate);

        // Assert
        assertEquals(mockTrips, result);
        verify(viewTripsService).setStrategy(any(GuideFilter.class));
        verify(viewTripsService).viewTrips();
    }

    @Test
    public void testViewTripDetails() {
        // Arrange
        int tripId = 1;
        Trip mockTrip = new Trip("Viaggio a Roma", "Descrizione", 1000.0, LocalDate.of(2023, 6, 1), 5, 20, 2);
        when(viewTripsService.viewTripDetails(tripId)).thenReturn(mockTrip);

        // Act
        Trip result = guideController.viewTripDetails(tripId);

        // Assert
        assertEquals(mockTrip, result);
        verify(viewTripsService).viewTripDetails(tripId);
    }

    @Test
    public void testSubmitApplication_Success() {
        // Arrange
        Trip trip = new Trip("Viaggio a Roma", "Descrizione", 1000.0, LocalDate.of(2023, 6, 1), 5, 20, 2);
        String cv = "Curriculum Vitae";

        // Act
        boolean result = guideController.submitApplication(trip, cv);

        // Assert
        assertTrue(result);
        verify(applicationService).sendApplication(cv, guide, trip);
    }

    @Test
    public void testSubmitApplication_Failure() {
        // Arrange
        Trip trip = new Trip("Viaggio a Roma", "Descrizione", 1000.0, LocalDate.of(2023, 6, 1), 5, 20, 2);
        String cv = "Curriculum Vitae";
        doThrow(new RuntimeException("Error")).when(applicationService).sendApplication(cv, guide, trip);

        // Act
        boolean result = guideController.submitApplication(trip, cv);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testWithdrawApplication_Success() {
        // Arrange
        Trip trip = new Trip("Viaggio a Roma", "Descrizione", 1000.0, LocalDate.of(2023, 6, 1), 5, 20, 2);

        // Act
        boolean result = guideController.withdrawApplication(trip);

        // Assert
        assertTrue(result);
        verify(applicationService).withdrawApplication(guide, trip);
    }

    @Test
    public void testWithdrawApplication_Failure() {
        // Arrange
        Trip trip = new Trip("Viaggio a Roma", "Descrizione", 1000.0, LocalDate.of(2023, 6, 1), 5, 20, 2);
        doThrow(new RuntimeException("Error")).when(applicationService).withdrawApplication(guide, trip);

        // Act
        boolean result = guideController.withdrawApplication(trip);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testTrackApplicationsStatus() {
        // Arrange
        List<Application> mockApplications = new ArrayList<>();
        when(applicationService.getApplicationsByGuide(guide)).thenReturn(mockApplications);

        // Act
        List<Application> result = guideController.trackApplicationsStatus();

        // Assert
        assertEquals(mockApplications, result);
        verify(applicationService).getApplicationsByGuide(guide);
    }

    @Test
    public void testGetApplicationsByStatus() {
        // Arrange
        Trip trip = new Trip("Viaggio a Roma", "Descrizione", 1000.0, LocalDate.of(2023, 6, 1), 5, 20, 2);
        Application pendingApp = mock(Application.class);
        when(pendingApp.getStatus()).thenReturn(ApplicationStatus.PENDING);

        Application acceptedApp = mock(Application.class);
        when(acceptedApp.getStatus()).thenReturn(ApplicationStatus.ACCEPTED);

        List<Application> mockApplications = Arrays.asList(pendingApp, acceptedApp);
        when(applicationService.getApplicationsByGuide(guide)).thenReturn(mockApplications);

        // Act
        Map<ApplicationStatus, List<Application>> result = guideController.getApplicationsByStatus();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsKey(ApplicationStatus.PENDING));
        assertTrue(result.containsKey(ApplicationStatus.ACCEPTED));
        assertEquals(1, result.get(ApplicationStatus.PENDING).size());
        assertEquals(1, result.get(ApplicationStatus.ACCEPTED).size());
        verify(applicationService).getApplicationsByGuide(guide);
    }

    @Test
    public void testUpdateGuideSkills_Success() {
        // Arrange
        List<Skill> skills = Arrays.asList(new Skill("Italiano"), new Skill("Inglese"));

        // Act
        boolean result = guideController.updateGuideSkills(skills);

        // Assert
        assertTrue(result);
        verify(guide).setSkills(skills);
        verify(guideService).updateGuide(guide);
    }

    @Test
    public void testUpdateGuideSkills_Failure() {
        // Arrange
        List<Skill> skills = Arrays.asList(new Skill("Italiano"), new Skill("Inglese"));
        doThrow(new RuntimeException("Error")).when(guideService).updateGuide(guide);

        // Act
        boolean result = guideController.updateGuideSkills(skills);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testAddSkill_Success() {
        // Arrange
        Skill skill = new Skill("Francese");

        // Act
        boolean result = guideController.addSkill(skill);

        // Assert
        assertTrue(result);
        verify(guide).addSkill(skill);
        verify(guideService).updateGuide(guide);
    }

    @Test
    public void testAddSkill_Failure() {
        // Arrange
        Skill skill = new Skill("Francese");
        doThrow(new RuntimeException("Error")).when(guideService).updateGuide(guide);

        // Act
        boolean result = guideController.addSkill(skill);

        // Assert
        assertFalse(result);
    }
}
