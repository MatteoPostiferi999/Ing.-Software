package core.dao.interfaces;

import java.util.List;
import core.model.User;

public interface UnqualifiedDAO {
    User getById(String id);
    List<User> getAll();
    void save(User u);
    void delete(String id);
}
