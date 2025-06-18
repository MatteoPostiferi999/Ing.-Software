package dao.interfaces;

import model.trip.Activity;

import java.util.List;

public interface ActivityDAO {
    void save(Activity activity);
    void update(Activity activity);
    Activity findById(int id);
    List<Activity> findByTripId(int tripId);
    void delete(int id);
}
