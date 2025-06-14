package core.dao.interfaces;

import java.util.List;
import core.model.Unqualified;

public interface UnqualifiedDAO {
    Unqualified getById(String id);
    List<Unqualified> getAll();
    void save(Unqualified u);
    void delete(String id);
}
