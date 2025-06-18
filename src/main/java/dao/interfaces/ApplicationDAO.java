package dao.interfaces;

import model.application.Application;
import model.application.ApplicationStatus;
import model.user.Guide;
import model.trip.Trip;
import java.util.List;


public interface ApplicationDAO {
    void save(Application application); // salva nuova candidatura
    void updateStatus(Application application, ApplicationStatus status); // aggiorna solo lo status
    void delete(Application application); // rimuove una candidatura (es. se rifiutata)

    Application getById(int id); // recupera per ID (opzionale ma utile)
    List<Application> getByTrip(Trip trip); // tutte le candidature per un viaggio
    List<Application> getByGuide(Guide guide); // tutte le candidature inviate da una guida

    // Metodi che operano con gli ID invece che con oggetti completi
    List<Application> getByTripId(int tripId);
    List<Application> getByGuideId(int guideId);

    // Metodo per caricare le candidature direttamente nel registro di un viaggio
    void loadApplicationsForTrip(Trip trip);

    // Metodo per trovare una specifica candidatura per guida e viaggio
    Application findByGuideAndTrip(int guideId, int tripId);

    // Metodi per filtrare le candidature per stato
    List<Application> findByStatus(ApplicationStatus status);
    List<Application> findByTripAndStatus(int tripId, ApplicationStatus status);

    // Metodi aggiuntivi per operazioni frequenti
    boolean hasGuideAppliedForTrip(int guideId, int tripId);
    int countApplicationsByTripId(int tripId);
    int countPendingApplicationsByTripId(int tripId);
}
