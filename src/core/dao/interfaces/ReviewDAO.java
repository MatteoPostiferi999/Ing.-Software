package core.dao.interfaces;

import java.util.List;
import core.model.Review;
import core.model.Reviewable;
import core.model.Traveler;

public interface ReviewDAO {
    Review getById(int id);
    List<Review> getByTarget(Reviewable target);
    List<Review> getByAuthor(Traveler author);
    void save(Review review);
    void delete(int id);
}
