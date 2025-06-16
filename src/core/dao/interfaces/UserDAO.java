package core.dao.interfaces;

import core.model.User;

public interface UserDAO {
    void save(User user);
    User getByEmail(String email);
    User getById(String id);
    void update(User user);
    void delete(User user);
}
