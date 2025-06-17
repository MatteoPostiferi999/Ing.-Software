package model.application;

import model.trip.Trip;

import java.util.ArrayList;
import java.util.List;

public class ApplicationRegister {
    private List<Application> applications;

    // Constructor for new empty register
    public ApplicationRegister() {
        this.applications = new ArrayList<>();
    }

    // Constructor for reconstruction from database
    public ApplicationRegister(List<Application> applications) {
        this.applications = applications;
    }

    // Getter
    public List<Application> getApplications() {
        return applications;
    }

    public void addApplication(Application application) {
        applications.add(application);
    }

    public List<Application> getAcceptedApplicationsForTrip(Trip trip) {
        List<Application> acceptedApplications = new ArrayList<>();
        for (Application application : applications) {
            if (application.getStatus() == ApplicationStatus.ACCEPTED) {
                acceptedApplications.add(application);
            }
        }
        return acceptedApplications;
    }

    // Other utility methods if needed (e.g., addApplication, removeApplication, etc.)
}
