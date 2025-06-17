package model.application;

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

    // Other utility methods if needed (e.g., addApplication, removeApplication, etc.)
}
