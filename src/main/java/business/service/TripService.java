package business.service;

import dao.interfaces.TripDAO;
import dao.interfaces.ActivityDAO;
import dao.interfaces.BookingDAO;
import dao.interfaces.AssignmentDAO;
import dao.interfaces.ApplicationDAO;

import model.trip.Trip;
import model.trip.Activity;
import model.user.Skill;
import model.notification.Notification;
import business.service.NotificationService;

import java.util.List;

public class TripService {
    private final TripDAO tripDAO;
    private final NotificationService notificationService;
    private ActivityDAO activityDAO;
    private BookingDAO bookingDAO;
    private AssignmentDAO assignmentDAO;
    private ApplicationDAO applicationDAO;

    public TripService(TripDAO tripDAO, NotificationService notificationService) {
        this.tripDAO = tripDAO;
        this.notificationService = notificationService;
    }

    // Costruttore completo con tutti i DAO necessari
    public TripService(TripDAO tripDAO, NotificationService notificationService,
                      ActivityDAO activityDAO, BookingDAO bookingDAO,
                      AssignmentDAO assignmentDAO, ApplicationDAO applicationDAO) {
        this.tripDAO = tripDAO;
        this.notificationService = notificationService;
        this.activityDAO = activityDAO;
        this.bookingDAO = bookingDAO;
        this.assignmentDAO = assignmentDAO;
        this.applicationDAO = applicationDAO;
    }

    // Setter per i DAO (per injection o inizializzazione posticipata)
    public void setActivityDAO(ActivityDAO activityDAO) {
        this.activityDAO = activityDAO;
    }

    public void setBookingDAO(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }

    public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
        this.assignmentDAO = assignmentDAO;
    }

    public void setApplicationDAO(ApplicationDAO applicationDAO) {
        this.applicationDAO = applicationDAO;
    }

    public void addTrip(Trip trip) {
        tripDAO.save(trip);
    }

    public Trip getTripById(int id) {
        return tripDAO.findById(id);
    }

    /**
     * Carica un viaggio completo con tutte le sue relazioni dal database
     * @param id ID del viaggio da caricare
     * @return Trip completo con tutte le relazioni caricate
     */
    public Trip getCompleteTripById(int id) {
        Trip trip = tripDAO.findById(id);
        if (trip != null) {
            loadTripRelations(trip);
        }
        return trip;
    }

    /**
     * Carica tutte le relazioni di un viaggio
     * @param trip Viaggio di cui caricare le relazioni
     */
    private void loadTripRelations(Trip trip) {
        loadPlannedActivities(trip);
        loadBookings(trip);
        loadAssignments(trip);
        loadApplications(trip);
        // Per ora non carichiamo le recensioni, le caricheremo quando necessario
    }

    /**
     * Carica le attività pianificate di un viaggio
     * @param trip Viaggio di cui caricare le attività
     */
    private void loadPlannedActivities(Trip trip) {
        if (activityDAO != null && trip.getActivityIds() != null) {
            for (Integer activityId : trip.getActivityIds()) {
                Activity activity = activityDAO.findById(activityId);
                if (activity != null) {
                    trip.getPlannedActivities().add(activity);
                }
            }
        } else if (activityDAO != null) {
            // Se non sono stati caricati gli ID, carichiamo tutte le attività associate al viaggio
            List<Activity> activities = activityDAO.findByTripId(trip.getTripId());
            for (Activity activity : activities) {
                trip.addPlannedActivity(activity);
            }
        }
    }

    /**
     * Carica le prenotazioni di un viaggio
     * @param trip Viaggio di cui caricare le prenotazioni
     */
    private void loadBookings(Trip trip) {
        if (bookingDAO != null) {
            bookingDAO.loadBookingsForTrip(trip);
        }
    }

    /**
     * Carica le guide assegnate a un viaggio
     * @param trip Viaggio di cui caricare le guide
     */
    private void loadAssignments(Trip trip) {
        if (assignmentDAO != null) {
            assignmentDAO.loadAssignmentsForTrip(trip);
        }
    }

    /**
     * Carica le candidature per un viaggio
     * @param trip Viaggio di cui caricare le candidature
     */
    private void loadApplications(Trip trip) {
        if (applicationDAO != null) {
            applicationDAO.loadApplicationsForTrip(trip);
        }
    }

    public void updateTrip(Trip trip) {
        if (trip.isAlreadyStarted()) {
            throw new IllegalStateException("Cannot update a trip that has already started.");
        }

        tripDAO.update(trip);

        // Notify guides
        trip.getAssignmentRegister().getAllAssignments().forEach(assignment -> {
            notificationService.sendNotification(assignment.getGuide(),"Trip details updated: " + trip.getTitle());
        });

        // Notify travelers
        trip.getBookingRegister().getAllBookings().forEach(booking -> {
            notificationService.sendNotification(booking.getTraveler(), "Trip details updated: " + trip.getTitle());
        });
    }

    public List<Trip> getAllTrips() {
        return tripDAO.findAll();
    }

    /**
     * Carica tutti i viaggi con le loro relazioni
     * @return Lista di viaggi completi
     */
    public List<Trip> getAllCompleteTrips() {
        List<Trip> trips = tripDAO.findAll();
        for (Trip trip : trips) {
            loadTripRelations(trip);
        }
        return trips;
    }
}