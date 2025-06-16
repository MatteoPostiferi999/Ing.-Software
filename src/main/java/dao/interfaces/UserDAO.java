package dao.interfaces;

import model.user.User;

public interface UserDAO {
    void save(User user);
    User findByEmail(String email);
    User findById(int id);
    void update(User user);
    void delete(User user);
}
