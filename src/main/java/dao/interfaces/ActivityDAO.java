package dao.interfaces;

import java.util.List;
import model.trip.Activity;
import model.trip.Trip;

public interface ActivityDAO {
List<Activity> getAllByTrip(Trip trip);
void addToTrip(Trip trip, Activity activity);
void removeFromTrip(Trip trip, String activityName);
}
