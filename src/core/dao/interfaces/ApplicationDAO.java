package core.dao.interfaces;

import java.util.List;
import core.model.Application;
import core.model.ApplicationStatus;

public interface ApplicationDAO {
    Application getById(int id);
    List<Application> getByTripId(int tripId);
    List<Application> getByGuideId(int guideId);
    void save(Application application);
    void updateStatus(int id, ApplicationStatus status);
    void delete(int id);
}
