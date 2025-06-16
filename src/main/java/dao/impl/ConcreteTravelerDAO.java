package dao.impl;

import dao.interfaces.TravelerDAO;
import model.user.Traveler;
import java.util.*;

public class ConcreteTravelerDAO implements TravelerDAO {
    public Traveler getById(int id) { return null; }
    public List<Traveler> getAll() { return new ArrayList<>(); }
    public void save(Traveler traveler) {}
    public void update(Traveler traveler) {}
    public void delete(int id) {}
}
