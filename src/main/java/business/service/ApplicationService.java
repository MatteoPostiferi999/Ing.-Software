package business.service;

import model.application.Application;
import model.user.Guide;
import model.trip.Trip;
import dao.interfaces.ApplicationDAO;
import model.application.ApplicationRegister;
import java.util.Comparator;
import java.util.List;

public class ApplicationService {
    private final ApplicationDAO applicationDAO;
    private final ApplicationRegister applicationRegister;

    public ApplicationService(ApplicationDAO applicationDAO, ApplicationRegister applicationRegister) {
        this.applicationDAO = applicationDAO;
        this.applicationRegister = applicationRegister;
    }

    public void sendApplication(String cv, Guide guide, Trip trip) {
        Application application = new Application(cv, guide, trip); // ID will be assigned by DB
        applicationRegister.addApplication(application);             // internal logic
        applicationDAO.save(application);                            // persistence
    }

    public void updateApplicationStatus(Application application, boolean accepted) {
        if (accepted) {
            application.accept();
        } else {
            application.reject();
        }
        applicationDAO.update(application); // update status in DB
    }


}
