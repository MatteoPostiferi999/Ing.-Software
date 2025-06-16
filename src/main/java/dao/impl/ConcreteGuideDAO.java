package dao.impl;

import dao.interfaces.GuideDAO;
import model.user.Guide;
import java.util.*;

public class ConcreteGuideDAO implements GuideDAO {
    public Guide getById(int id) { return null; }
    public List<Guide> getAll() { return new ArrayList<>(); }
    public void save(Guide guide) {}
    public void update(Guide guide) {}
    public void delete(int id) {}
}
