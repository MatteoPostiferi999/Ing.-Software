package business.controller;

import business.service.*;
import model.notification.Notification;
import model.review.Review;
import model.review.Reviewable;
import model.trip.Trip;
import model.user.Traveler;


import java.time.LocalDate;
import java.util.List;

public class TravelerController {

    private final Traveler traveler;
    private final ViewTripsService viewTripsService;
    private final BookingService bookingService;
    private final NotificationService notificationService;
    private final ReviewService reviewService;

    public TravelerController(Traveler traveler, ViewTripsService viewTripsService, BookingService bookingService,
                              NotificationService notificationService, ReviewService reviewService) {
        this.traveler = traveler;
        this.viewTripsService = viewTripsService;
        this.bookingService = bookingService;
        this.notificationService = notificationService;
        this.reviewService = reviewService;
    }

    public List<Trip> viewAvailableTrips(LocalDate minDate, LocalDate maxDate, Double maxPrice) {
        viewTripsService.setStrategy(new TravelerFilter(traveler, minDate, maxDate, maxPrice));
        return viewTripsService.viewTrips();
    }

    public Trip viewTripDetails(int tripId) {
        return viewTripsService.viewTripDetails(tripId);
    }

    public void bookTrip(Trip trip) {
        bookingService.bookTrip(traveler, trip);
    }

    public Notification readNextNotification() {
        return notificationService.getNextUnreadNotification(traveler);
    }

    public void leaveReview(int rating, String description, Reviewable target) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        Review review = new Review(rating, description, traveler, target);
        reviewService.addReview(review);
    }
}
