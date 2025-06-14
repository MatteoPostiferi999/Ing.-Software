package core.business.service;

import core.model.Application;
import core.model.ApplicationStatus;
import java.util.List;

public class ApplicationService {
    public void applyToTrip(int guideId, int tripId, String cv) { }
    public void cancelApplication(int applicationId) { }
    public void changeApplicationStatus(int applicationId, ApplicationStatus status) { }
    public List<Application> getApplicationsByGuide(int guideId) { return null; }
    public List<Application> getApplicationsByTrip(int tripId) { return null; }
}
