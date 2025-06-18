package business.controller;

import business.service.*;
import business.service.ApplicationService;
import model.user.Guide;
import model.user.Skill;
import model.notification.Notification;
import model.trip.Trip;


import java.time.LocalDate;
import java.util.List;

public class GuideController {
    private Guide guide;
    private ViewTripsService viewTripsService;
    private ApplicationService applicationService;
    private NotificationService notificationService;

    public GuideController(Guide guide, ViewTripsService viewTripsService, ApplicationService applicationService, NotificationService notificationService) {
        this.guide = guide;
        this.viewTripsService = viewTripsService;
        this.applicationService = applicationService;
        this.notificationService = notificationService;
    }

    public List<Trip> viewAvailableTrips(LocalDate minDate, LocalDate maxDate) {
        viewTripsService.setStrategy(new GuideFilter(guide, minDate, maxDate));
        return viewTripsService.viewTrips();
    }

    public Trip viewTripDetails(int tripId) {
        return viewTripsService.viewTripDetails(tripId);
    }

    public void submitApplication(Trip trip, String cv) {
        applicationService.sendApplication(cv, guide, trip);
    }

    public void withdrawApplication(Trip trip) {
        applicationService.withdrawApplication(guide, trip);
    }

    public void updateGuideProfile(List<Skill> updatedSkills) {
        guide.setSkills(updatedSkills);
        // If persistent storage is used, call the GuideService or DAO to update the guide in the database.
    }

    public Notification readNextUnreadNotification() {
        return notificationService.getNextUnreadNotification(guide);
    }

}
