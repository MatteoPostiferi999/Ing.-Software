package core.business.service;

import core.dao.interfaces.UserDAO;
import core.model.User;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void registerUser(User user) {
        userDAO.save(user);
    }

    public User login(String email, String password) {
        User user = userDAO.getByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null; // oppure Optional<User> o eccezione custom
    }
}
