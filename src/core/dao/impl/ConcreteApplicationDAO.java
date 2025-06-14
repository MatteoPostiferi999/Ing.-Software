package core.dao.impl;

import core.dao.interfaces.ApplicationDAO;
import core.model.Application;
import core.model.ApplicationStatus;
import java.util.*;

public class ConcreteApplicationDAO implements ApplicationDAO {
    public Application getById(int id) { return null; }
    public List<Application> getByTripId(int tripId) { return new ArrayList<>(); }
    public List<Application> getByGuideId(int guideId) { return new ArrayList<>(); }
    public void save(Application application) {}
    public void updateStatus(int id, ApplicationStatus status) {}
    public void delete(int id) {}
}
