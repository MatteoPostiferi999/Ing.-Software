package integration.controller;

import business.controller.Agency;
import business.controller.GuideController;
import business.controller.TravelerController;
import business.service.*;
import dao.interfaces.*;
import model.application.Application;
import model.trip.Trip;
import model.user.Guide;
import model.user.Skill;
import model.user.Traveler;
import model.user.User;
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

public class TripManagementIntegrationTest {

    // DAO
    @Mock private TripDAO tripDAO;
    @Mock private UserDAO userDAO;
    @Mock private GuideDAO guideDAO;
    @Mock private TravelerDAO travelerDAO;
    @Mock private ApplicationDAO applicationDAO;
    @Mock private BookingDAO bookingDAO;
    @Mock private AssignmentDAO assignmentDAO;

    // Services
    private TripService tripService;
    private ApplicationService applicationService;
    private AssignmentService assignmentService;
    private ViewTripsService viewTripsService;
    private BookingService bookingService;
    private NotificationService notificationService;
    private GuideService guideService;
    private TravelerService travelerService;
    private ReviewService reviewService;

    // Controllers
    private Agency agency;
    private GuideController guideController;
    private TravelerController travelerController;

    // Model
    private Guide guide;
    private Traveler traveler;
    private Trip trip;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup services
        notificationService = mock(NotificationService.class);
        tripService = new TripService(tripDAO);
        applicationService = new ApplicationService(applicationDAO, mock(model.application.ApplicationRegister.class), notificationService);
        assignmentService = new AssignmentService(mock(model.application.ApplicationRegister.class), assignmentDAO, notificationService);
        viewTripsService = new ViewTripsService(tripService);
        bookingService = new BookingService(bookingDAO, tripDAO, travelerDAO, notificationService);
        guideService = new GuideService(guideDAO);
        travelerService = mock(TravelerService.class);
        reviewService = mock(ReviewService.class);

        // Setup controllers
        agency = new Agency(tripService, applicationService, assignmentService);

        // Setup model
        User userGuide = new User("Guida Turistica", "guida@example.com", "password");
        userGuide.setUserId(1);
        guide = new Guide(userGuide);
        guide.setGuideId(1);
        List<Skill> skills = new ArrayList<>();
        skills.add(new Skill("Italiano"));
        skills.add(new Skill("Storia dell'arte"));
        guide.setSkills(skills);

        User userTraveler = new User("Viaggiatore", "viaggiatore@example.com", "password");
        userTraveler.setUserId(2);
        traveler = new Traveler(userTraveler);
        traveler.setTravelerId(1);

        trip = new Trip("Tour di Roma", "Visita guidata della città eterna", 500.0, 
                     LocalDate.now().plusMonths(2), 5, 20, 2);
        trip.setTripId(1);
        trip.setRequiredSkills(Arrays.asList(new Skill("Italiano")));

        // Configure controllers with model
        guideController = new GuideController(guide, viewTripsService, applicationService, 
                                            notificationService, guideService);
        travelerController = new TravelerController(traveler, viewTripsService, bookingService, 
                                                 notificationService, reviewService, travelerService);
    }

    @Test
    public void testCompleteTrip_CreationApplicationAndBooking() {
        // 1. Agenzia crea un viaggio
        when(tripDAO.findAll()).thenReturn(Arrays.asList(trip));
        when(tripDAO.findById(trip.getTripId())).thenReturn(trip);

        List<Trip> allTrips = agency.getAllTrips();
        assertEquals(1, allTrips.size());

        // 2. Guida visualizza e si candida per il viaggio
        when(viewTripsService.viewTrips()).thenReturn(Arrays.asList(trip));
        when(viewTripsService.viewTripDetails(trip.getTripId())).thenReturn(trip);

        List<Trip> availableTrips = guideController.viewAvailableTrips(
            LocalDate.now(), LocalDate.now().plusMonths(3));
        assertEquals(1, availableTrips.size());

        Trip tripDetails = guideController.viewTripDetails(trip.getTripId());
        assertNotNull(tripDetails);

        // La guida invia la candidatura
        boolean applicationSent = guideController.submitApplication(trip, "Sono una guida esperta");
        assertTrue(applicationSent);
        verify(applicationService).sendApplication("Sono una guida esperta", guide, trip);

        // 3. Agenzia vede e accetta la candidatura
        Application mockApplication = mock(Application.class);
        when(mockApplication.getGuide()).thenReturn(guide);
        when(mockApplication.getTrip()).thenReturn(trip);
        when(applicationDAO.getById(1)).thenReturn(mockApplication);
        when(applicationDAO.getByGuide(guide)).thenReturn(Arrays.asList(mockApplication));

        agency.acceptApplication(1);
        verify(applicationService).updateApplicationStatus(mockApplication, true);

        // 4. Agenzia assegna le guide
        agency.assignGuides(trip.getTripId());
        verify(assignmentService).assignBestGuidesToTrip(trip);

        // 5. Viaggiatore visualizza e prenota il viaggio
        when(viewTripsService.viewTrips()).thenReturn(Arrays.asList(trip));

        List<Trip> tripsForTraveler = travelerController.viewAvailableTrips(
            LocalDate.now(), LocalDate.now().plusMonths(3), 1000.0);
        assertEquals(1, tripsForTraveler.size());

        boolean bookingResult = travelerController.bookTrip(trip);
        assertTrue(bookingResult);
        verify(bookingService).bookTrip(traveler, trip);
    }

    @Test
    public void testTripUpdateAndCancellation() {
        // 1. Agenzia aggiorna un viaggio
        when(tripDAO.findById(1)).thenReturn(trip);

        agency.updateTrip(1, "Tour di Roma - Edizione Speciale", "Visita esclusiva della città eterna", 
                         600.0, LocalDate.now().plusMonths(3), 8, 25, 3);

        verify(tripDAO).updateTrip(trip);
        assertEquals("Tour di Roma - Edizione Speciale", trip.getTitle());
        assertEquals(600.0, trip.getPrice());
        assertEquals(8, trip.getBookingRegister().getMinTrav());
        assertEquals(25, trip.getBookingRegister().getMaxTrav());
        assertEquals(3, trip.getAssignmentRegister().getMaxGuides());

        // 2. Viaggiatore prenota e poi cancella
        when(bookingService.bookTrip(traveler, trip)).thenReturn(true);
        boolean bookingResult = travelerController.bookTrip(trip);
        assertTrue(bookingResult);

        // Simula una prenotazione esistente
        model.booking.Booking mockBooking = mock(model.booking.Booking.class);
        when(mockBooking.getTrip()).thenReturn(trip);
        when(bookingService.cancelBooking(traveler, trip)).thenReturn(true);

        boolean cancellationResult = travelerController.cancelBooking(mockBooking);
        assertTrue(cancellationResult);
        verify(bookingService).cancelBooking(traveler, trip);

        // 3. Agenzia elimina il viaggio
        agency.deleteTrip(1);
        verify(tripDAO).deleteTrip(1);
    }
}
