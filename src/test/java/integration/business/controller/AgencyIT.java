package integration.business.controller;

import business.controller.Agency;
import business.service.ApplicationService;
import business.service.AssignmentService;
import business.service.TripService;
import dao.interfaces.ApplicationDAO;
import dao.interfaces.GuideDAO;
import dao.interfaces.NotificationDAO;
import dao.interfaces.TripDAO;
import model.application.Application;
import model.application.ApplicationRegister;
import model.application.ApplicationStatus;
import model.notification.NotificationRegister;
import model.review.ReviewRegister;
import model.trip.Trip;
import model.user.Guide;
import model.user.User;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AgencyIT {

    private Agency agency;

    private TripDAO mockTripDAO;
    private ApplicationDAO mockApplicationDAO;
    private GuideDAO mockGuideDAO;
    private NotificationDAO mockNotificationDAO;

    private TripService tripService;
    private ApplicationService applicationService;
    private AssignmentService assignmentService;

    private ApplicationRegister mockApplicationRegister;
    private Trip testTrip;

    @BeforeAll
    void setupAll() {
        // Trip base
        testTrip = new Trip(1, "Trip to Alps", "Adventure trip", 500.0, LocalDate.now().plusDays(10), 3, 10, 2);

        // Mock DAO
        mockTripDAO = mock(TripDAO.class);
        mockApplicationDAO = mock(ApplicationDAO.class);
        mockGuideDAO = mock(GuideDAO.class);
        mockNotificationDAO = mock(NotificationDAO.class);
        mockApplicationRegister = mock(ApplicationRegister.class);

        // Real services
        tripService = new TripService(mockTripDAO, null);
        applicationService = new ApplicationService(mockApplicationDAO, mockApplicationRegister,
                new business.service.NotificationService(mockNotificationDAO, mockGuideDAO, null),
                mockGuideDAO, mockTripDAO);
        assignmentService = mock(AssignmentService.class); // mock perché complesso

        agency = new Agency(tripService, applicationService, assignmentService);

        // Stub comportamenti DAO
        when(mockTripDAO.findById(1)).thenReturn(testTrip);
        when(mockTripDAO.findAll()).thenReturn(List.of(testTrip));
        doNothing().when(mockTripDAO).save(any(Trip.class));
        doNothing().when(mockTripDAO).update(any(Trip.class));
        doNothing().when(mockTripDAO).deleteById(anyInt());
    }

    @Test
    void testCreateTrip() {
        assertDoesNotThrow(() -> agency.createTrip("Test Trip", "Desc", 100.0,
                LocalDate.now().plusDays(1), 2, 8, 2));
    }

    @Test
    void testUpdateTrip() {
        assertDoesNotThrow(() -> agency.updateTrip(
                1, "Updated Title", "New Desc", 550.0, LocalDate.now().plusDays(5), 4, 9, 2));
    }

    @Test
    void testDeleteTrip() {
        assertDoesNotThrow(() -> agency.deleteTrip(1));
    }

    @Test
    void testGetAllTrips() {
        List<Trip> trips = agency.getAllTrips();
        assertNotNull(trips);
        assertEquals(1, trips.size());
        assertEquals("Trip to Alps", trips.get(0).getTitle());
    }

    @Test
    void testGetTripById() {
        Trip t = agency.getTripById(1);
        assertNotNull(t);
        assertEquals(1, t.getTripId());
    }

    @Test
    void testGetApplicationsForTrip() {
        Trip trip = testTrip;

        Application app = new Application(1, "CV", 1, 1, ApplicationStatus.PENDING);
        app.setTrip(trip); // Imposta il Trip sull'application
        app.setGuide(new Guide(1, new ArrayList<>(), mock(User.class),
                mock(ReviewRegister.class), mock(NotificationRegister.class))); // Guida mock

        // Creo un ApplicationRegister per il Trip con l'applicazione
        ApplicationRegister tripAppRegister = mock(ApplicationRegister.class);
        when(tripAppRegister.getApplications()).thenReturn(List.of(app));
        trip.setApplicationRegister(tripAppRegister);

        // MOCK: tripService usa tripDAO.findById(1)
        when(mockTripDAO.findById(1)).thenReturn(trip);

        // MOCK: applicationService usa applicationDAO.getByTripId(1) se deve caricare le applicazioni
        when(mockApplicationDAO.getByTripId(1)).thenReturn(List.of(app));


        List<Application> applications = agency.getApplicationsForTrip(1);

        assertNotNull(applications);
        assertEquals(1, applications.size());
        assertEquals(app.getApplicationId(), applications.get(0).getApplicationId());
    }


    @Test
    void testRejectApplication() {
        // Crea trip valido
        Trip trip = new Trip(1, "Rejected trip", "desc", 100.0, LocalDate.now(), 2, 5, 2);

        // Crea guide fittizia
        Guide guide = new Guide(1, new ArrayList<>(), Mockito.mock(User.class),
                Mockito.mock(ReviewRegister.class),
                Mockito.mock(NotificationRegister.class));

        // Crea application con trip e guida assegnati
        Application app = new Application(11, "CV", 1, 1, ApplicationStatus.PENDING);
        app.setTrip(trip);
        app.setGuide(guide); // 🔐 necessario per evitare NullPointerException

        when(mockApplicationDAO.getById(11)).thenReturn(app);

        assertDoesNotThrow(() -> agency.rejectApplication(11));
    }


    @Test
    void testAssignGuides() {
        doNothing().when(assignmentService).assignBestGuidesToTrip(any(Trip.class));
        assertDoesNotThrow(() -> agency.assignGuides(1));
    }
}