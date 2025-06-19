package integration.business.controller;

import business.controller.GuideController;
import business.service.*;
import model.application.Application;
import model.application.ApplicationStatus;
import model.notification.Notification;
import model.trip.Trip;
import model.user.Guide;
import model.user.Skill;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GuideControllerIT {

    private GuideController guideController;

    private Guide guide;
    private ViewTripsService viewTripsService;
    private ApplicationService applicationService;
    private NotificationService notificationService;
    private GuideService guideService;

    @BeforeAll
    void setupAll() {
        guide = new Guide(1, "Test Guide");
        viewTripsService = new ViewTripsServiceImpl();
        applicationService = new ApplicationServiceImpl();
        notificationService = new NotificationServiceImpl();
        guideService = new GuideServiceImpl();

        guideController = new GuideController(
                guide,
                viewTripsService,
                applicationService,
                notificationService,
                guideService
        );

        // Assume here you insert test data into your real or in-memory DB
    }

    @AfterAll
    void tearDownAll() {
        // Clean up test data
    }

    @Test
    void testViewAvailableTrips() {
        List<Trip> trips = guideController.viewAvailableTrips(LocalDate.now(), LocalDate.now().plusMonths(1));
        assertNotNull(trips);
        assertFalse(trips.isEmpty());
    }

    @Test
    void testSubmitApplication() {
        Trip testTrip = new Trip(100, "Test Trip");
        String cv = "Test CV";

        assertTrue(guideController.submitApplication(testTrip, cv));
    }

    @Test
    void testWithdrawApplication() {
        Trip testTrip = new Trip(100, "Test Trip");
        assertTrue(guideController.withdrawApplication(testTrip));
    }

    @Test
    void testTrackApplicationsStatus() {
        List<Application> applications = guideController.trackApplicationsStatus();
        assertNotNull(applications);
    }

    @Test
    void testGetApplicationsByStatus() {
        Map<ApplicationStatus, List<Application>> groupedApplications = guideController.getApplicationsByStatus();
        assertNotNull(groupedApplications);
        assertFalse(groupedApplications.isEmpty());
    }

    @Test
    void testUpdateGuideSkills() {
        List<Skill> skills = List.of(new Skill("Hiking"));
        assertTrue(guideController.updateGuideSkills(skills));
    }

    @Test
    void testAddSkill() {
        Skill skill = new Skill("Skiing");
        assertTrue(guideController.addSkill(skill));
    }

    @Test
    void testRemoveSkill() {
        Skill skill = new Skill("Hiking");
        assertTrue(guideController.removeSkill(skill));
    }

    @Test
    void testGetNotifications() {
        List<Notification> notifications = guideController.getNotifications();
        assertNotNull(notifications);
    }

    @Test
    void testReadNextUnreadNotification() {
        Notification notification = guideController.readNextUnreadNotification();
        assertNotNull(notification);
    }

    @Test
    void testMarkNotificationAsRead() {
        int notificationId = 123;
        assertTrue(guideController.markNotificationAsRead(notificationId));
    }

    @Test
    void testMarkAllNotificationsAsRead() {
        assertDoesNotThrow(() -> guideController.markAllNotificationsAsRead());
    }

    @Test
    void testGetAssignedTrips() {
        List<Trip> trips = guideController.getAssignedTrips();
        assertNotNull(trips);
    }

    @Test
    void testGetUpcomingAssignedTrips() {
        List<Trip> trips = guideController.getUpcomingAssignedTrips();
        assertNotNull(trips);
    }

    @Test
    void testGetPastAssignedTrips() {
        List<Trip> trips = guideController.getPastAssignedTrips();
        assertNotNull(trips);
    }
}
