package business.service;

import model.trip.Trip;
import model.assignment.Assignment;
import model.user.Guide;
import model.application.Application;
import model.application.ApplicationRegister;
import dao.interfaces.AssignmentDAO;
import dao.interfaces.GuideDAO;
import dao.interfaces.TripDAO;
import business.service.NotificationService;
import model.notification.Notification;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AssignmentService {

    private final ApplicationRegister applicationRegister;
    private final AssignmentDAO assignmentDAO;
    private final NotificationService notificationService;
    private GuideDAO guideDAO;
    private TripDAO tripDAO;

    public AssignmentService(ApplicationRegister applicationRegister, AssignmentDAO assignmentDAO, NotificationService notificationService) {
        this.applicationRegister = applicationRegister;
        this.assignmentDAO = assignmentDAO;
        this.notificationService = notificationService;
    }

    // Costruttore completo con tutti i DAO
    public AssignmentService(ApplicationRegister applicationRegister, AssignmentDAO assignmentDAO,
                            NotificationService notificationService, GuideDAO guideDAO, TripDAO tripDAO) {
        this.applicationRegister = applicationRegister;
        this.assignmentDAO = assignmentDAO;
        this.notificationService = notificationService;
        this.guideDAO = guideDAO;
        this.tripDAO = tripDAO;
    }

    // Setter per dependency injection
    public void setGuideDAO(GuideDAO guideDAO) {
        this.guideDAO = guideDAO;
    }

    public void setTripDAO(TripDAO tripDAO) {
        this.tripDAO = tripDAO;
    }

    /**
     * Assegna le migliori guide disponibili a un viaggio in base al rating
     * @param trip Il viaggio a cui assegnare le guide
     */
    public void assignBestGuidesToTrip(Trip trip) {
        List<Application> acceptedApplications = applicationRegister.getAcceptedApplicationsForTrip(trip);

        int maxGuides = trip.getMaxGuides();
        int alreadyAssigned = trip.getAssignmentRegister().getAssignments().size();
        int remaining = maxGuides - alreadyAssigned;

        if (remaining <= 0) return;

        acceptedApplications.stream()
            .sorted(Comparator.comparingDouble((Application a) -> a.getGuide().getRating()).reversed())
            .limit(remaining)
            .forEach(app -> {
                Guide guide = app.getGuide();
                Assignment assignment = new Assignment(guide, trip);
                trip.getAssignmentRegister().addAssignment(assignment);
                assignmentDAO.save(assignment);

                notificationService.sendNotification(guide, "You have been assigned to the trip: " + trip.getTitle());
            });

        if (trip.getAssignmentRegister().getAssignments().size() == trip.getMaxGuides()) {
            trip.getBookingRegister().getBookings().forEach(booking -> {
                notificationService.sendNotification(
                    booking.getTraveler(),
                    "The guides for your trip \"" + trip.getTitle() + "\" have been assigned and are now complete."
                );
            });
        }
    }

    /**
     * Assegna manualmente una guida a un viaggio
     * @param guide La guida da assegnare
     * @param trip Il viaggio a cui assegnare la guida
     * @return L'assegnazione creata o null se non è stato possibile crearla
     */
    public Assignment assignGuideToTrip(Guide guide, Trip trip) {
        // Verifica se il viaggio può accettare altre guide
        int currentAssignmentsCount = trip.getAssignmentRegister().getAssignments().size();
        if (!trip.getAssignmentRegister().canAddMoreGuides()) {
            throw new IllegalStateException("Il viaggio ha già raggiunto il numero massimo di guide");
        }

        // Verifica se la guida è già assegnata a questo viaggio
        if (isGuideAssignedToTrip(guide.getGuideId(), trip.getTripId())) {
            throw new IllegalStateException("La guida è già assegnata a questo viaggio");
        }

        // Crea una nuova assegnazione
        Assignment assignment = new Assignment(guide, trip);

        // Salva nel database
        assignmentDAO.save(assignment);

        // Aggiunge al registro del viaggio
        trip.getAssignmentRegister().addAssignment(assignment);

        // Notifica la guida
        notificationService.sendNotification(guide, "Sei stato assegnato al viaggio: " + trip.getTitle());

        return assignment;
    }

    /**
     * Rimuove un'assegnazione di una guida da un viaggio
     * @param guide La guida da rimuovere
     * @param trip Il viaggio da cui rimuovere la guida
     * @return true se l'assegnazione è stata rimossa, false altrimenti
     */
    public boolean removeGuideFromTrip(Guide guide, Trip trip) {
        // Trova l'assegnazione
        Assignment assignment = assignmentDAO.findByGuideAndTrip(guide.getGuideId(), trip.getTripId());
        if (assignment == null) {
            return false;
        }

        // Rimuovi l'assegnazione dal database
        assignmentDAO.delete(assignment);

        // Rimuovi l'assegnazione dal registro del viaggio
        trip.getAssignmentRegister().removeAssignment(assignment);

        // Notifica la guida
        notificationService.sendNotification(guide, "Sei stato rimosso dal viaggio: " + trip.getTitle());

        return true;
    }

    /**
     * Verifica se una guida è già assegnata a un viaggio
     * @param guideId ID della guida
     * @param tripId ID del viaggio
     * @return true se la guida è già assegnata, false altrimenti
     */
    public boolean isGuideAssignedToTrip(int guideId, int tripId) {
        Assignment assignment = assignmentDAO.findByGuideAndTrip(guideId, tripId);
        return assignment != null;
    }

    /**
     * Ottiene tutte le assegnazioni presenti nel sistema
     * @return Lista di tutte le assegnazioni
     */
    public List<Assignment> getAllAssignments() {
        return assignmentDAO.findAll();
    }

    /**
     * Ottiene tutte le assegnazioni per una guida specifica
     * @param guide La guida di cui cercare le assegnazioni
     * @return Lista delle assegnazioni della guida
     */
    public List<Assignment> getAssignmentsForGuide(Guide guide) {
        return assignmentDAO.loadAssignmentsForGuide(guide);
    }

    /**
     * Carica tutte le assegnazioni per un viaggio e le aggiunge al registro del viaggio
     * @param trip Il viaggio di cui caricare le assegnazioni
     * @return Lista delle assegnazioni caricate
     */
    public List<Assignment> loadAssignmentsForTrip(Trip trip) {
        assignmentDAO.loadAssignmentsForTrip(trip);
        return trip.getAssignmentRegister().getAssignments();
    }

    /**
     * Ottiene tutti i viaggi a cui è assegnata una guida
     * @param guide La guida di cui cercare i viaggi
     * @return Lista dei viaggi assegnati alla guida
     */
    public List<Trip> getTripsForGuide(Guide guide) {
        List<Assignment> assignments = assignmentDAO.findByGuideId(guide.getGuideId());
        List<Trip> trips = new ArrayList<>();

        for (Assignment assignment : assignments) {
            if (assignment.getTrip() == null && tripDAO != null) {
                // Se il viaggio non è stato caricato, lo carichiamo ora
                Trip trip = tripDAO.findById(assignment.getTripId());
                assignment.setTrip(trip);
            }

            if (assignment.getTrip() != null) {
                trips.add(assignment.getTrip());
            }
        }

        return trips;
    }

    /**
     * Ottiene tutte le guide assegnate a un viaggio
     * @param trip Il viaggio di cui cercare le guide
     * @return Lista delle guide assegnate al viaggio
     */
    public List<Guide> getGuidesForTrip(Trip trip) {
        List<Assignment> assignments = assignmentDAO.findByTripId(trip.getTripId());
        List<Guide> guides = new ArrayList<>();

        for (Assignment assignment : assignments) {
            if (assignment.getGuide() == null && guideDAO != null) {
                // Se la guida non è stata caricata, la carichiamo ora
                Guide guide = guideDAO.findById(assignment.getGuideId());
                assignment.setGuide(guide);
            }

            if (assignment.getGuide() != null) {
                guides.add(assignment.getGuide());
            }
        }

        return guides;
    }
}
