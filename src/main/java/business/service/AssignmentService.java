package business.service;

import model.trip.Trip;
import model.assignment.Assignment;
import model.user.Guide;
import model.application.Application;
import model.application.ApplicationRegister;
import dao.interfaces.AssignmentDAO;
import business.service.NotificationService;
import model.notification.Notification;

import java.util.Comparator;
import java.util.List;

public class AssignmentService {

    private final ApplicationRegister applicationRegister;
    private final AssignmentDAO assignmentDAO;
    private final NotificationService notificationService;

    public AssignmentService(ApplicationRegister applicationRegister, AssignmentDAO assignmentDAO, NotificationService notificationService) {
        this.applicationRegister = applicationRegister;
        this.assignmentDAO = assignmentDAO;
        this.notificationService = notificationService;
    }

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
}
