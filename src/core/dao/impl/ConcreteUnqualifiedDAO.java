package core.dao.impl;

import core.dao.interfaces.UnqualifiedDAO;
import core.model.User;
import java.util.*;

public class ConcreteUnqualifiedDAO implements UnqualifiedDAO {
    public User getById(String id) { return null; }
    public List<User> getAll() { return new ArrayList<>(); }
    public void save(User u) {}
    public void delete(String id) {}
}
