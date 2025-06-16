package core.dao.impl;

import core.dao.interfaces.ReviewDAO;
import core.model.Review;
import core.model.Reviewable;
import core.model.Traveler;

import java.util.ArrayList;
import java.util.List;

public class ConcreteReviewDAO implements ReviewDAO {
    @Override
    public Review getById(int id) {
        return null;
    }

    @Override
    public List<Review> getByTarget(Reviewable target) {
        return new ArrayList<>();
    }

    @Override
    public List<Review> getByAuthor(Traveler author) {
        return new ArrayList<>();
    }

    @Override
    public void save(Review review) {
    }

    @Override
    public void delete(int id) {
    }
}
