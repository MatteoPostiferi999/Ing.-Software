package core.dao.interfaces;

import java.util.List;
import core.model.Activity;
import core.model.Trip;

public interface ActivityDAO {
List<Activity> getAllByTrip(Trip trip);
void addToTrip(Trip trip, Activity activity);
void removeFromTrip(Trip trip, String activityName);
}
