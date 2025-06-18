package business.controller;

import business.service.ApplicationService;
import business.service.AssignmentService;
import business.service.TripService;
import model.trip.Trip;

import java.time.LocalDate;

public class Agency {
    private final TripService tripService;
    private final ApplicationService applicationService;
    private final AssignmentService assignmentService;

    public Agency(TripService tripService, ApplicationService applicationService, AssignmentService assignmentService) {
        this.tripService = tripService;
        this.applicationService = applicationService;
        this.assignmentService = assignmentService;
    }

    public void createTrip(String title, String description, double price, LocalDate date, int minTrav, int maxTrav, int maxGuides) {
        Trip trip = new Trip(title, description, price, date, minTrav, maxTrav, maxGuides);
        tripService.addTrip(trip);
    }

    public void updateTrip(int id, String title, String description, double price, LocalDate date, int minTrav, int maxTrav, int maxGuides) {
        Trip existingTrip = tripService.getTripById(id);
        if (existingTrip == null) {
            throw new IllegalArgumentException("Trip with ID " + id + " does not exist.");
        }
        existingTrip.setTitle(title);
        existingTrip.setDescription(description);
        existingTrip.setPrice(price);
        existingTrip.setDate(date);
        existingTrip.getBookingRegister().setMinTrav(minTrav);
        existingTrip.getBookingRegister().setMaxTrav(maxTrav);
        existingTrip.getAssignmentRegister().setMaxGuides(maxGuides);
        tripService.updateTrip(existingTrip);
    }

    public void deleteTrip(int tripId) {
        tripService.deleteTrip(tripId);
    }

    public void acceptApplication(int applicationId) {
       applicationService.updateApplicationStatus(applicationService.getApplication(applicationId), true);
    }

    public void rejectApplication(int applicationId) {
        applicationService.updateApplicationStatus(applicationService.getApplication(applicationId), false);
    }

    public void assignGuides(int tripId) {
        assignmentService.assignBestGuidesToTrip(tripService.getTripById(tripId));
    }
}
