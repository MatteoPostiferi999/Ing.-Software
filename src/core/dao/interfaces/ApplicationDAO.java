package core.dao.interfaces;

import java.util.List;
import core.model.Application;
import core.model.ApplicationStatus;
import core.model.Guide;
import core.model.Trip;

public interface ApplicationDAO {
    Application getById(int id);  
    List<Application> getByTrip(Trip trip);
    List<Application> getByGuide(Guide guide);
    void save(Application application);
    void updateStatus(Application application, ApplicationStatus status);
    void delete(Application application);
}
