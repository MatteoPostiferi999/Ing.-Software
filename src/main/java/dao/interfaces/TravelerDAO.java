package dao.interfaces;

import java.util.List;
import model.user.Traveler;

public interface TravelerDAO {
    Traveler getById(int id);
    Traveler findById(int id); // Alias per getById per consistenza con altre interfacce
    Traveler findByUserId(int userId); // Nuovo metodo per trovare un viaggiatore tramite user_id
    List<Traveler> getAll();
    void save(Traveler traveler);
    void update(Traveler traveler);
    void delete(int id);
    void delete(Traveler traveler); // Sovraccarico del metodo delete per accettare un oggetto Traveler
}
