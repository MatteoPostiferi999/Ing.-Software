// src/main/java/dao/interfaces/TripDAO.java
package dao.interfaces;

import java.util.List;
import model.trip.Trip;

public interface TripDAO {
    Trip findById(int id); // solo dati base
    Trip findByIdFull(int id); // con relazioni
    List<Trip> findAll();
    List<Trip> findAllFull();
    void save(Trip trip);
    void update(Trip trip);
    void deleteById(int id);
}