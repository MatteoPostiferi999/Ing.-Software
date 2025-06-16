package dao.interfaces;

import model.application.Application;
import model.application.ApplicationStatus;
import model.user.Guide;
import model.trip.Trip;


public interface ApplicationDAO {
    void save(Application application); // salva nuova candidatura
    void updateStatus(Application application, ApplicationStatus status); // aggiorna solo lo status
    void delete(Application application); // rimuove una candidatura (es. se rifiutata)

    Application getById(int id); // recupera per ID (opzionale ma utile)
    List<Application> getByTrip(Trip trip); // tutte le candidature per un viaggio
    List<Application> getByGuide(Guide guide); // tutte le candidature inviate da una guida
}
