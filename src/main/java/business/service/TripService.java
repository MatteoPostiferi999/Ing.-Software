package business.service;

import dao.interfaces.TripDAO;
import model.trip.Trip;
import model.notification.Notification;
import business.service.NotificationService;

import java.util.List;

public class TripService {
    private final TripDAO tripDAO;
    private final NotificationService notificationService;

    public TripService(TripDAO tripDAO, NotificationService notificationService) {
        this.tripDAO = tripDAO;
        this.notificationService = notificationService;
    }

    public void addTrip(Trip trip) {
        tripDAO.save(trip);
    }

    public Trip getTripById(int id) {
        return tripDAO.findById(id);
    }

    public void updateTrip(Trip trip) {
        if (trip.isAlreadyStarted()) {
            throw new IllegalStateException("Cannot update a trip that has already started.");
        }

        tripDAO.update(trip);

        // Notify guides
        trip.getAssignmentRegister().getAllAssignments().forEach(assignment -> {
            notificationService.sendNotification(assignment.getGuide(),"Trip details updated: " + trip.getTitle());
        });

        // Notify travelers
        trip.getBookingRegister().getAllBookings().forEach(booking -> {
            notificationService.sendNotification(booking.getTraveler(), "Trip details updated: " + trip.getTitle());
        });
    }

    public void deleteTrip(int id) {
        tripDAO.delete(id);
    }

    public List<Trip> getAllTrips() {
        return tripDAO.findAll();
    }
}