package dao.interfaces;

import java.util.List;
import model.review.Review;
import model.review.Reviewable;
import model.user.Traveler;

public interface ReviewDAO {
    Review getById(int id);
    List<Review> getByTarget(Reviewable target);
    List<Review> getByAuthor(Traveler author);
    void save(Review review);
    void delete(int id);
}
