package integration.business.controller;

import business.controller.GuideController;
import business.service.*;
import dao.interfaces.*;
import model.application.Application;
import model.application.ApplicationRegister;
import model.application.ApplicationStatus;
import model.notification.Notification;
import model.notification.NotificationRegister;
import model.review.ReviewRegister;
import model.trip.Trip;
import model.user.Guide;
import model.user.Skill;
import model.user.User;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GuideControllerIT {

    private GuideController guideController;

    private Guide guide;
    private ViewTripsService mockViewTripsService;
    private ApplicationService applicationService;
    private NotificationService notificationService;
    private GuideService guideService;

    private TripDAO mockTripDAO;
    private ApplicationDAO mockApplicationDAO;
    private NotificationDAO mockNotificationDAO;
    private GuideDAO mockGuideDAO;
    private TravelerDAO mockTravelerDAO;
    private ApplicationRegister mockApplicationRegister;

    @BeforeAll
    void setupAll() {
        User mockOwner = Mockito.mock(User.class);
        ReviewRegister mockReviews = Mockito.mock(ReviewRegister.class);
        NotificationRegister mockNotifications = Mockito.mock(NotificationRegister.class);

        guide = new Guide(
                1,
                new ArrayList<>(), // ✅ mutabile
                mockOwner,
                mockReviews,
                mockNotifications
        );

        // Mocks DAO
        mockTripDAO = Mockito.mock(TripDAO.class);
        mockApplicationDAO = Mockito.mock(ApplicationDAO.class);
        mockNotificationDAO = Mockito.mock(NotificationDAO.class);
        mockGuideDAO = Mockito.mock(GuideDAO.class);
        mockTravelerDAO = Mockito.mock(TravelerDAO.class);
        mockApplicationRegister = Mockito.mock(ApplicationRegister.class);

        // Service reali (tranne ViewTripsService)
        mockViewTripsService = Mockito.mock(ViewTripsService.class);
        notificationService = new NotificationService(mockNotificationDAO, mockGuideDAO, mockTravelerDAO);
        guideService = new GuideService(mockGuideDAO);
        applicationService = new ApplicationService(
                mockApplicationDAO,
                mockApplicationRegister,
                notificationService,
                mockGuideDAO,
                mockTripDAO
        );

        // Setup comportamenti mock
        Trip mockTrip = new Trip(1, "Trip Test", "Description", 100.0, LocalDate.now(), 2, 10, 3);
        when(mockViewTripsService.viewTrips()).thenReturn(List.of(mockTrip));
        doNothing().when(mockApplicationDAO).save(any(Application.class));
        doNothing().when(mockApplicationDAO).delete(any(Application.class));

        when(mockApplicationDAO.getByGuideId(anyInt())).thenReturn(List.of(
                new Application(1, "CV Test", 1, 1, ApplicationStatus.PENDING)
        ));

        doNothing().when(mockGuideDAO).update(any(Guide.class));

        Notification mockNotification = new Notification(1, "Test notification", guide, false);
        when(mockNotificationDAO.getByGuide(any(Guide.class))).thenReturn(List.of(mockNotification));
        when(mockNotificationDAO.getUnreadByRecipient(anyInt(), eq("GUIDE"))).thenReturn(List.of(mockNotification));
        doNothing().when(mockNotificationDAO).markAsRead(anyInt());
        doNothing().when(mockNotificationDAO).markAllAsRead(anyInt(), eq("GUIDE"));
        when(mockGuideDAO.getAssignedTrips(anyInt())).thenReturn(List.of(mockTrip));

        // Istanza controller
        guideController = new GuideController(
                guide,
                mockViewTripsService,
                applicationService,
                notificationService,
                guideService
        );
    }

    @Test
    void testViewAvailableTrips() {
        List<Trip> trips = guideController.viewAvailableTrips(LocalDate.now(), LocalDate.now().plusMonths(1));
        assertNotNull(trips);
        assertFalse(trips.isEmpty());
    }

    @Test
    void testSubmitApplication() {
        Trip testTrip = new Trip(100, "Test Trip", "Description", 100.0, LocalDate.now(), 2, 10, 3);
        String cv = "Test CV";
        assertTrue(guideController.submitApplication(testTrip, cv));
    }

    @Test
    void testWithdrawApplication() {
        Trip testTrip = new Trip(100, "Test Trip", "Description", 100.0, LocalDate.now(), 2, 10, 3);
        assertTrue(guideController.withdrawApplication(testTrip));
    }

    @Test
    void testTrackApplicationsStatus() {
        List<Application> applications = guideController.trackApplicationsStatus();
        assertNotNull(applications);
    }

    @Test
    void testUpdateGuideSkills() {
        List<Skill> skills = new ArrayList<>();
        skills.add(new Skill("Hiking", "Mountain hiking"));
        assertTrue(guideController.updateGuideSkills(skills));
    }

    @Test
    void testRemoveSkill() {
        Skill skill = new Skill("Hiking", "Mountain hiking");
        guide.addSkill(skill); // usa lista mutabile
        assertTrue(guideController.removeSkill(skill));
    }

    @Test
    void testGetNotifications() {
        List<Notification> notifications = guideController.getNotifications();
        assertNotNull(notifications);
    }

    @Test
    void testReadNextUnreadNotification() {
        // Creo una notifica non letta
        Notification unreadNotification = new Notification(2, "Unread notification", guide, false);

        // Configuro il NotificationRegister mockato della guida per restituire la notifica non letta
        NotificationRegister mockNotificationRegister = guide.getNotificationRegister();
        when(mockNotificationRegister.getNotifications()).thenReturn(List.of(unreadNotification));

        // Eseguo il test
        Notification notification = guideController.readNextUnreadNotification();

        // Verifico che la notifica sia stata restituita
        assertNotNull(notification);
    }

    @Test
    void testMarkNotificationAsRead() {
        int notificationId = 123;
        assertDoesNotThrow(() -> guideController.markNotificationAsRead(notificationId));
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
}
