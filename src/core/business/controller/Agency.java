package core.business.controller;

import core.business.service.ManageGuidesService;
import core.business.service.TripService;
import core.model.Application;
import core.model.Trip;
import core.model.Guide;


public class Agency {
    private TripService tripService;
    private ManageGuidesService manageGuideService;

    public Agency(TripService tripService, ManageGuidesService manageGuideService) {
        this.tripService = tripService;
        this.manageGuideService = manageGuideService;
    }

    public void createTrip(Trip trip) {
        tripService.createTrip(trip);
    }


    public void deleteTrip(int tripId) {
        tripService.deleteTrip(tripId);
    }


    public void assignGuideToTrip(Trip trip, Guide guide) {
        manageGuideService.assignGuideToTrip(guide, trip);
    }
}
