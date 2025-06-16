package core.business.service;

import core.model.Application;
import core.model.ApplicationStatus;
import core.model.Guide;
import core.model.Trip;
import core.model.Assignment;
import core.dao.interfaces.ApplicationDAO;
import core.model.ApplicationRegister;

import java.util.List;

public class ApplicationService {
    private final ApplicationRegister applicationRegister;
    private final ApplicationDAO applicationDAO;

    public ApplicationService(ApplicationRegister register, ApplicationDAO dao) {
        this.applicationRegister = register;
        this.applicationDAO = dao;
    }

    // Invio candidatura
    public void sendApplication(int applicationId, String cv, Guide guide, Trip trip) {
        Application application = new Application(applicationId, cv, guide, trip);
        applicationRegister.addApplication(application); // logica interna
        applicationDAO.save(application);                // persistenza
    }

    // Revisione della candidatura (accetta o rifiuta)
    public void reviewApplication(Application application, ApplicationStatus newStatus) {
        application.setStatus(newStatus);

        if (newStatus == ApplicationStatus.ACCEPTED) {
            // Assegna la guida al viaggio
            Assignment assignment = new Assignment(application.getGuide(), application.getTrip());
            application.getTrip().getAssignmentRegister().addAssignment(assignment);
        } else if (newStatus == ApplicationStatus.REJECTED) {
            // Rimuovi la candidatura solo dal register (non dal DB se serve traccia storica)
            applicationRegister.removeApplication(application);
            applicationDAO.delete(application); // rimuovi dal DB
        }

        applicationDAO.update(application); // aggiorna stato nel DB (accepted o rejected)
    }
}


