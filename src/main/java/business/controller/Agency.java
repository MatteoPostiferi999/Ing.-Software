package business.controller;

import business.service.ApplicationService;
import business.service.AssignmentService;
import business.service.TripService;
import business.service.UserService;
import business.service.*;
import model.application.Application;
import model.application.ApplicationStatus;
import model.assignment.Assignment;
import model.trip.Trip;

public class Agency {
    private final UserService userService;
    private final TripService tripService;
    private final ApplicationService applicationService;
    private final AssignmentService assignmentService;

    public Agency(UserService userService,
                  TripService tripService,
                  ApplicationService applicationService,
                  AssignmentService assignmentService) {
        this.userService = userService;
        this.tripService = tripService;
        this.applicationService = applicationService;
        this.assignmentService = assignmentService;
    }

    public void createTrip(Trip trip) {
        tripService.createTrip(trip);
    }

    public void reviewApplication(Application application, ApplicationStatus status) {
        applicationService.reviewApplication(application, status);
    }

    public void assignGuide(Assignment assignment) {
        assignmentService.assignGuide(assignment);
    }
}
