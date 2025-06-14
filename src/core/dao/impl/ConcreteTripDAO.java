package core.dao.impl;

import core.dao.interfaces.TripDAO;
import core.model.Trip;
import java.util.*;

public class ConcreteTripDAO implements TripDAO {
    public Trip getById(int id) { return null; }
    public List<Trip> getAll() { return new ArrayList<>(); }
    public void save(Trip trip) {}
    public void update(Trip trip) {}
    public void delete(int id) {}
}
