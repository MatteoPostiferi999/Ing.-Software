package core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Registro delle candidature effettuate dalle guide per i viaggi.
 * Mapper: Trip x Guide
 */
public class ApplicationRegister {
    private List<Application> applications = new ArrayList<>();
    //private int tripId; 

    public ApplicationRegister(int tripId) {
        //this.tripId = tripId;
    }


    public void addApplication(Application application) {
        applications.add(application);
    }

    public void removeApplication(Application application) {
        applications.remove(application);
    }

    public List<Application> getAllApplications() {
        return applications;
    }

    
}
