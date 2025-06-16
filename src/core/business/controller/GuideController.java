package core.business.controller;

import core.business.service.*;
import core.model.*;

import java.util.List;

public class GuideController {
    private final ApplicationService applicationService;
    private final ViewTripsService viewTripsService;
    private final GuideService guideService;
    private final NotificationService notificationService;

    public GuideController(ApplicationService applicationService,
                           ViewTripsService viewTripsService,
                           GuideService guideService,
                           NotificationService notificationService) {
        this.applicationService = applicationService;
        this.viewTripsService = viewTripsService;
        this.guideService = guideService;
        this.notificationService = notificationService;
    }

    // Invia una candidatura per un viaggio
    public void sendApplication(int id, String cv, Guide guide, Trip trip) {
        applicationService.sendApplication(id, cv, guide, trip);
    }

    // Visualizza i viaggi compatibili con le skill della guida
    public List<Trip> viewAvailableTrips(Guide guide) {
        viewTripsService.setStrategy(new GuideFilter(guide));
        return viewTripsService.viewTrips();
    }

    // Modifica il profilo della guida (skill, ecc.)
    public void editGuideProfile(Guide guide, List<Skill> newSkills) {
        guideService.editProfile(guide, newSkills, guide.getNotificationRegister());
    }

    // Recupera tutte le notifiche ricevute dalla guida (senza modificarle)
    public List<Notification> getNotifications(Guide guide) {
        return notificationService.getAllNotifications(guide);
    }

    // Marca una singola notifica come letta
    public void readNotification(Notification notification) {
        notificationService.markNotificationAsRead(notification);
    }

    // Marca tutte le notifiche della guida come lette
    public void readAllNotifications(Guide guide) {
        notificationService.markAllAsRead(guide);
    }
}
