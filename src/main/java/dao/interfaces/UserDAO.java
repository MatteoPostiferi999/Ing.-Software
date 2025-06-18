package dao.interfaces;

import model.user.User;

public interface UserDAO {
    void save(User user);
    User findByEmail(String email);
    User findById(int id);
    User findByEmailAndPassword(String email, String password);
    void update(User user);
    void delete(User user);
}
