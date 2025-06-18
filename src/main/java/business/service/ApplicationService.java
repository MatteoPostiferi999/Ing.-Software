package business.service;

import model.application.Application;
import model.application.ApplicationStatus;
import model.user.Guide;
import model.trip.Trip;
import dao.interfaces.ApplicationDAO;
import dao.interfaces.GuideDAO;
import dao.interfaces.TripDAO;
import model.application.ApplicationRegister;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import business.service.NotificationService;
import model.notification.Notification;

public class ApplicationService {
    private final ApplicationDAO applicationDAO;
    private final ApplicationRegister applicationRegister;
    private final NotificationService notificationService;
    private GuideDAO guideDAO;
    private TripDAO tripDAO;

    public ApplicationService(ApplicationDAO applicationDAO, ApplicationRegister applicationRegister, NotificationService notificationService) {
        this.applicationDAO = applicationDAO;
        this.applicationRegister = applicationRegister;
        this.notificationService = notificationService;
    }

    // Costruttore completo con tutti i DAO
    public ApplicationService(ApplicationDAO applicationDAO, ApplicationRegister applicationRegister,
                             NotificationService notificationService, GuideDAO guideDAO, TripDAO tripDAO) {
        this.applicationDAO = applicationDAO;
        this.applicationRegister = applicationRegister;
        this.notificationService = notificationService;
        this.guideDAO = guideDAO;
        this.tripDAO = tripDAO;
    }

    // Metodi setter per dependency injection
    public void setGuideDAO(GuideDAO guideDAO) {
        this.guideDAO = guideDAO;
    }

    public void setTripDAO(TripDAO tripDAO) {
        this.tripDAO = tripDAO;
    }

    public void sendApplication(String cv, Guide guide, Trip trip) {
        Application application = new Application(cv, guide, trip); // ID will be assigned by DB
        applicationRegister.addApplication(application);             // internal logic
        applicationDAO.save(application);                            // persistence
    }

    public void updateApplicationStatus(Application application, boolean accepted) {
        if (accepted) {
            application.accept();
            notificationService.sendNotification(application.getGuide(), "Your application for the trip \"" + application.getTrip().getTitle() + "\" has been accepted.");
        } else {
            application.reject();
            notificationService.sendNotification(application.getGuide(), "Your application for the trip \"" + application.getTrip().getTitle() + "\" has been rejected. :(");
        }
        applicationDAO.updateStatus(application, application.getStatus()); // update status in DB
    }

    public Application getApplication(int applicationId) {
        return applicationDAO.getById(applicationId);
    }


    public void withdrawApplication(Guide guide, Trip trip) {
        Application application = trip.getApplicationRegister().getApplicationByGuide(guide);
        if (application != null && application.isPending()) {
            applicationRegister.removeApplication(application);
            applicationDAO.delete(application);
            notificationService.sendNotification(guide, "Your application for the trip \"" + trip.getTitle() + "\" has been withdrawn.");
        }
    }

    // Metodi aggiuntivi per il caricamento delle candidature

    /**
     * Carica tutte le candidature per un viaggio e le aggiunge al suo registro
     * @param trip Il viaggio di cui caricare le candidature
     */
    public void loadApplicationsForTrip(Trip trip) {
        applicationDAO.loadApplicationsForTrip(trip);
    }

    /**
     * Ottiene tutte le candidature di una guida
     * @param guide La guida di cui ottenere le candidature
     * @return Lista delle candidature della guida
     */
    public List<Application> getApplicationsByGuide(Guide guide) {
        return applicationDAO.getByGuide(guide);
    }

    /**
     * Ottiene tutte le candidature in base allo stato
     * @param status Lo stato delle candidature da ottenere
     * @return Lista delle candidature con lo stato specificato
     */
    public List<Application> getApplicationsByStatus(ApplicationStatus status) {
        return applicationDAO.findByStatus(status);
    }

    /**
     * Ottiene le candidature per un viaggio filtrate per stato
     * @param trip Il viaggio di cui ottenere le candidature
     * @param status Lo stato delle candidature da ottenere
     * @return Lista delle candidature per il viaggio con lo stato specificato
     */
    public List<Application> getApplicationsByTripAndStatus(Trip trip, ApplicationStatus status) {
        return applicationDAO.findByTripAndStatus(trip.getTripId(), status);
    }

    /**
     * Verifica se una guida ha già fatto una candidatura per un viaggio
     * @param guide La guida da verificare
     * @param trip Il viaggio da verificare
     * @return true se la guida ha già fatto una candidatura, false altrimenti
     */
    public boolean hasGuideAppliedForTrip(Guide guide, Trip trip) {
        return applicationDAO.hasGuideAppliedForTrip(guide.getGuideId(), trip.getTripId());
    }

    /**
     * Conta il numero di candidature per un viaggio
     * @param trip Il viaggio di cui contare le candidature
     * @return Il numero totale di candidature
     */
    public int countApplicationsForTrip(Trip trip) {
        return applicationDAO.countApplicationsByTripId(trip.getTripId());
    }

    /**
     * Conta il numero di candidature in attesa per un viaggio
     * @param trip Il viaggio di cui contare le candidature in attesa
     * @return Il numero di candidature in attesa
     */
    public int countPendingApplicationsForTrip(Trip trip) {
        return applicationDAO.countPendingApplicationsByTripId(trip.getTripId());
    }

    /**
     * Accetta più candidature in batch
     * @param applications Lista delle candidature da accettare
     */
    public void acceptApplications(List<Application> applications) {
        for (Application application : applications) {
            if (application.isPending()) {
                application.accept();
                applicationDAO.updateStatus(application, ApplicationStatus.ACCEPTED);
                notificationService.sendNotification(application.getGuide(),
                    "Your application for the trip \"" + application.getTrip().getTitle() + "\" has been accepted.");
            }
        }
    }

    /**
     * Rifiuta più candidature in batch
     * @param applications Lista delle candidature da rifiutare
     */
    public void rejectApplications(List<Application> applications) {
        for (Application application : applications) {
            if (application.isPending()) {
                application.reject();
                applicationDAO.updateStatus(application, ApplicationStatus.REJECTED);
                notificationService.sendNotification(application.getGuide(),
                    "Your application for the trip \"" + application.getTrip().getTitle() + "\" has been rejected.");
            }
        }
    }

    /**
     * Ottiene tutte le guide che hanno fatto una candidatura accettata per un viaggio
     * @param trip Il viaggio di cui ottenere le guide
     * @return Lista delle guide con candidature accettate
     */
    public List<Guide> getAcceptedGuidesForTrip(Trip trip) {
        List<Application> acceptedApplications = getApplicationsByTripAndStatus(trip, ApplicationStatus.ACCEPTED);
        List<Guide> guides = new ArrayList<>();

        for (Application app : acceptedApplications) {
            if (app.getGuide() == null && guideDAO != null) {
                // Carica la guida se non è già caricata
                Guide guide = guideDAO.findById(app.getGuideId());
                app.setGuide(guide);
            }

            if (app.getGuide() != null) {
                guides.add(app.getGuide());
            }
        }

        return guides;
    }

    /**
     * Seleziona automaticamente le migliori guide in base al rating
     * @param trip Il viaggio per cui selezionare le guide
     * @param count Il numero di guide da selezionare
     */
    public void selectBestGuidesForTrip(Trip trip, int count) {
        List<Application> pendingApplications = getApplicationsByTripAndStatus(trip, ApplicationStatus.PENDING);

        // Ordina le candidature per rating delle guide (dal più alto al più basso)
        pendingApplications.sort((app1, app2) -> Double.compare(
            app2.getGuide().getRating(),
            app1.getGuide().getRating()
        ));

        // Seleziona solo il numero richiesto di guide
        List<Application> selectedApplications = pendingApplications.stream()
            .limit(count)
            .collect(java.util.stream.Collectors.toList());

        // Accetta le candidature selezionate
        acceptApplications(selectedApplications);

        // Rifiuta le altre candidature
        List<Application> rejectedApplications = new ArrayList<>(pendingApplications);
        rejectedApplications.removeAll(selectedApplications);
        rejectApplications(rejectedApplications);
    }
}
