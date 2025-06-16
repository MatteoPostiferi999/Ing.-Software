package dao.interfaces;

import java.util.List;
import model.trip.Trip;

public interface TripDAO {
    Trip findById(int id);
    List<Trip> findAll();
    void save(Trip trip);
    void update(Trip trip);
    void deleteById(int id);
}
