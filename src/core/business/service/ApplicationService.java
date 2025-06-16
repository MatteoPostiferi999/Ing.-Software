package core.business.service;

import core.model.Application;
import core.model.ApplicationStatus;
import core.model.Guide;
import core.model.Trip;

import java.util.List;

public class ApplicationService {
    public void sendApplication(Guide guide, Trip trip, String cv) { }
    public void cancelApplication(Application application) { }
    public List<Application> getApplicationsByGuide(Guide guide) { return null; }
    public void getAllApplications() { }
    public List<Application> getApplicationsByTrip(Trip trip) { return null; }
}

