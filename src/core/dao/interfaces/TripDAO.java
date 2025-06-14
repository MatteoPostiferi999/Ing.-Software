package core.dao.interfaces;

import java.util.List;
import core.model.Trip;

public interface TripDAO {
    Trip getById(int id);
    List<Trip> getAll();
    void save(Trip trip);
    void update(Trip trip);
    void delete(int id);
}
