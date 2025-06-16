package dao.interfaces;

import java.util.List;
import model.user.Guide;

public interface GuideDAO {
    Guide getById(int id);
    List<Guide> getAll();
    void save(Guide guide);
    void update(Guide guide);
    void delete(int id);
}
