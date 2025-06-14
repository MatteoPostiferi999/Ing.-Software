package core.dao.interfaces;

import java.util.List;
import core.model.Activity;

public interface ActivityDAO {
    List<Activity> getAllByTripId(int tripId);
    void addToTrip(int tripId, Activity activity);
    void removeFromTrip(int tripId, String activityName);
}
