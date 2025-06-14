package core.dao.interfaces;

import java.util.List;
import core.model.Traveler;

public interface TravelerDAO {
    Traveler getById(int id);
    List<Traveler> getAll();
    void save(Traveler traveler);
    void update(Traveler traveler);
    void delete(int id);
}
