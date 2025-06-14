package core.dao.impl;

import core.dao.interfaces.UnqualifiedDAO;
import core.model.Unqualified;
import java.util.*;

public class ConcreteUnqualifiedDAO implements UnqualifiedDAO {
    public Unqualified getById(String id) { return null; }
    public List<Unqualified> getAll() { return new ArrayList<>(); }
    public void save(Unqualified u) {}
    public void delete(String id) {}
}
