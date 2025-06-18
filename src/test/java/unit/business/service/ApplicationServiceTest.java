package unit.business.service;

import business.service.ApplicationService;
import business.service.NotificationService;
import dao.interfaces.ApplicationDAO;
import dao.interfaces.GuideDAO;
import dao.interfaces.TripDAO;
import model.application.Application;
import model.application.ApplicationRegister;
import model.application.ApplicationStatus;
import model.trip.Trip;
import model.user.Guide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @Mock private ApplicationDAO applicationDAO;
    @Mock private ApplicationRegister applicationRegister;
    @Mock private NotificationService notificationService;
    @Mock private GuideDAO guideDAO;
    @Mock private TripDAO tripDAO;
    @Mock private Guide guide;
    @Mock private Trip trip;
    @Mock private Application application;

    private ApplicationService applicationService;

    @BeforeEach
    void setUp() {
        applicationService = new ApplicationService(applicationDAO, applicationRegister,
                notificationService, guideDAO, tripDAO);
    }

    @Test
    void testSendApplication() {
        // Arrange
        String cv = "Test CV";
        when(trip.getTitle()).thenReturn("Test Trip");

        // Act
        applicationService.sendApplication(cv, guide, trip);

        // Assert
        verify(applicationRegister).addApplication(any(Application.class));
        verify(applicationDAO).save(any(Application.class));
    }

    @Test
    void testUpdateApplicationStatus_WhenAccepted() {
        // Arrange
        when(application.getGuide()).thenReturn(guide);
        when(application.getTrip()).thenReturn(trip);
        when(trip.getTitle()).thenReturn("Test Trip");

        // Act
        applicationService.updateApplicationStatus(application, true);

        // Assert
        verify(application).accept();
        verify(applicationDAO).updateStatus(application, ApplicationStatus.ACCEPTED);
        verify(notificationService).sendNotification(eq(guide), contains("accepted"));
    }

    @Test
    void testUpdateApplicationStatus_WhenRejected() {
        // Arrange
        when(application.getGuide()).thenReturn(guide);
        when(application.getTrip()).thenReturn(trip);
        when(trip.getTitle()).thenReturn("Test Trip");

        // Act
        applicationService.updateApplicationStatus(application, false);

        // Assert
        verify(application).reject();
        verify(applicationDAO).updateStatus(application, ApplicationStatus.REJECTED);
        verify(notificationService).sendNotification(eq(guide), contains("rejected"));
    }

    @Test
    void testGetApplication() {
        // Arrange
        int applicationId = 1;
        when(applicationDAO.getById(applicationId)).thenReturn(application);

        // Act
        Application result = applicationService.getApplication(applicationId);

        // Assert
        assertEquals(application, result);
        verify(applicationDAO).getById(applicationId);
    }

    @Test
    void testWithdrawApplication_WhenApplicationIsPending() {
        // Arrange
        when(trip.getApplicationRegister()).thenReturn(applicationRegister);
        when(applicationRegister.getApplicationByGuide(guide)).thenReturn(application);
        when(application.isPending()).thenReturn(true);
        when(trip.getTitle()).thenReturn("Test Trip");

        // Act
        applicationService.withdrawApplication(guide, trip);

        // Assert
        verify(applicationRegister).removeApplication(application);
        verify(applicationDAO).delete(application);
        verify(notificationService).sendNotification(eq(guide), contains("withdrawn"));
    }

    @Test
    void testWithdrawApplication_WhenApplicationIsNotPending() {
        // Arrange
        when(trip.getApplicationRegister()).thenReturn(applicationRegister);
        when(applicationRegister.getApplicationByGuide(guide)).thenReturn(application);
        when(application.isPending()).thenReturn(false);

        // Act
        applicationService.withdrawApplication(guide, trip);

        // Assert
        verify(applicationRegister, never()).removeApplication(any());
        verify(applicationDAO, never()).delete(any());
        verify(notificationService, never()).sendNotification(any(), any());
    }

    @Test
    void testGetApplicationsByGuide() {
        // Arrange
        List<Application> expectedApplications = Arrays.asList(application);
        when(applicationDAO.getByGuide(guide)).thenReturn(expectedApplications);

        // Act
        List<Application> result = applicationService.getApplicationsByGuide(guide);

        // Assert
        assertEquals(expectedApplications, result);
        verify(applicationDAO).getByGuide(guide);
    }

    @Test
    void testGetApplicationsByStatus() {
        // Arrange
        ApplicationStatus status = ApplicationStatus.PENDING;
        List<Application> expectedApplications = Arrays.asList(application);
        when(applicationDAO.findByStatus(status)).thenReturn(expectedApplications);

        // Act
        List<Application> result = applicationService.getApplicationsByStatus(status);

        // Assert
        assertEquals(expectedApplications, result);
        verify(applicationDAO).findByStatus(status);
    }

    @Test
    void testHasGuideAppliedForTrip() {
        // Arrange
        when(guide.getGuideId()).thenReturn(1);
        when(trip.getTripId()).thenReturn(1);
        when(applicationDAO.hasGuideAppliedForTrip(1, 1)).thenReturn(true);

        // Act
        boolean result = applicationService.hasGuideAppliedForTrip(guide, trip);

        // Assert
        assertTrue(result);
        verify(applicationDAO).hasGuideAppliedForTrip(1, 1);
    }

    @Test
    void testAcceptApplications() {
        // Arrange
        List<Application> applications = Arrays.asList(application);
        when(application.isPending()).thenReturn(true);
        when(application.getGuide()).thenReturn(guide);
        when(application.getTrip()).thenReturn(trip);
        when(trip.getTitle()).thenReturn("Test Trip");

        // Act
        applicationService.acceptApplications(applications);

        // Assert
        verify(application).accept();
        verify(applicationDAO).updateStatus(application, ApplicationStatus.ACCEPTED);
        verify(notificationService).sendNotification(eq(guide), contains("accepted"));
    }

    @Test
    void testRejectApplications() {
        // Arrange
        List<Application> applications = Arrays.asList(application);
        when(application.isPending()).thenReturn(true);
        when(application.getGuide()).thenReturn(guide);
        when(application.getTrip()).thenReturn(trip);
        when(trip.getTitle()).thenReturn("Test Trip");

        // Act
        applicationService.rejectApplications(applications);

        // Assert
        verify(application).reject();
        verify(applicationDAO).updateStatus(application, ApplicationStatus.REJECTED);
        verify(notificationService).sendNotification(eq(guide), contains("rejected"));
    }
}