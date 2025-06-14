package core.business.controller;

import core.model.Trip;
import core.model.Application;

public class Agency {
    public void createTrip(Trip trip) { }
    public void updateTrip(Trip trip) { }
    public void deleteTrip(int tripId) { }
    public void reviewApplication(Application application, boolean accept) { }
    public void assignGuide(int tripId, int guideId) { }
}
