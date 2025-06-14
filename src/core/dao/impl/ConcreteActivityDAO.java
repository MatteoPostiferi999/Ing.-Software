package core.dao.impl;

import core.dao.interfaces.ActivityDAO;
import core.model.Activity;
import java.util.*;

public class ConcreteActivityDAO implements ActivityDAO {
    public List<Activity> getAllByTripId(int tripId) { return new ArrayList<>(); }
    public void addToTrip(int tripId, Activity activity) {}
    public void removeFromTrip(int tripId, String activityName) {}
}
