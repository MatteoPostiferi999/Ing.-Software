package dao.interfaces;

import java.util.List;
import model.user.Guide;

public interface GuideDAO {
    Guide getById(int id);
    Guide findById(int id); // Alias per getById per consistenza con altre interfacce
    Guide findByUserId(int userId); // Nuovo metodo per trovare una guida tramite user_id
    List<Guide> getAll();
    void save(Guide guide);
    void update(Guide guide);
    void delete(int id);
    void delete(Guide guide); // Sovraccarico del metodo delete per accettare un oggetto Guide
}
