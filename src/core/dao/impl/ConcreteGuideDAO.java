package core.dao.impl;

import core.dao.interfaces.GuideDAO;
import core.model.Guide;
import java.util.*;

public class ConcreteGuideDAO implements GuideDAO {
    public Guide getById(int id) { return null; }
    public List<Guide> getAll() { return new ArrayList<>(); }
    public void save(Guide guide) {}
    public void update(Guide guide) {}
    public void delete(int id) {}
}
