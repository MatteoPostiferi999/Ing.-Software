package core.business.controller;

import core.business.service.*;
import core.model.*;

import java.util.List;

public class TravelerController {
    private final BookingService bookingService;
    private final ViewTripsService viewTripsService;
    private final ReviewService reviewService;
    private final NotificationService notificationService;

    public TravelerController(BookingService bookingService,
                              ViewTripsService viewTripsService,
                              ReviewService reviewService,
                              NotificationService notificationService) {
        this.bookingService = bookingService;
        this.viewTripsService = viewTripsService;
        this.reviewService = reviewService;
        this.notificationService = notificationService;
    }

    public void bookTrip(Traveler traveler, Trip trip) {
        bookingService.bookTrip(traveler, trip);
    }

    public List<Trip> viewAvailableTrips(Traveler traveler) {
        viewTripsService.setStrategy(new TravelerFilter(traveler));
        return viewTripsService.viewTrips();
    }

    public void leaveReview(Review review) {
        reviewService.leaveReview(review);
    }

    public List<Notification> getNotifications(Traveler traveler) {
        return notificationService.readNotifications(traveler);
    }
}
