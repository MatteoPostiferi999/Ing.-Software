package business.service;

import model.application.Application;
import model.user.Guide;
import model.trip.Trip;
import dao.interfaces.ApplicationDAO;
import model.application.ApplicationRegister;
import java.util.Comparator;
import java.util.List;
import business.service.NotificationService;
import model.notification.Notification;

public class ApplicationService {
    private final ApplicationDAO applicationDAO;
    private final ApplicationRegister applicationRegister;
    private final NotificationService notificationService;

    public ApplicationService(ApplicationDAO applicationDAO, ApplicationRegister applicationRegister, NotificationService notificationService) {
        this.applicationDAO = applicationDAO;
        this.applicationRegister = applicationRegister;
        this.notificationService = notificationService;
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
        applicationDAO.update(application); // update status in DB
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
}
