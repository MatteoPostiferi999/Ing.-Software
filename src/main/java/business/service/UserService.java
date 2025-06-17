package business.service;

import business.dao.UserDAO;
import model.user.User;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public boolean register(User user) {
        if (emailExists(user.getEmail())) {
            return false;
        }
        userDAO.save(user);
        return true;
    }

    public User login(String email, String password) {
        User user = userDAO.findByEmailAndPassword(email, password);
        if (user == null) {
            return null;
        }
        return user;
    }

    private boolean emailExists(String email) {
        return userDAO.findByEmail(email) != null;
    }
}
