package core.dao.interfaces;

import java.util.List;
import core.model.Review;

public interface ReviewDAO {
    Review getById(int id);
    List<Review> getByTargetId(int targetId);
    List<Review> getByAuthorId(int authorId);
    void save(Review review);
    void delete(int id);
}
