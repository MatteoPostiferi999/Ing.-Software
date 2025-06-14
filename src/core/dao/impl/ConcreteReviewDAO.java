package core.dao.impl;

import core.dao.interfaces.ReviewDAO;
import core.model.Review;
import java.util.*;

public class ConcreteReviewDAO implements ReviewDAO {
    public Review getById(int id) { return null; }
    public List<Review> getByTargetId(int targetId) { return new ArrayList<>(); }
    public List<Review> getByAuthorId(int authorId) { return new ArrayList<>(); }
    public void save(Review review) {}
    public void delete(int id) {}
}
