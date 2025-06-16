package core.business.service;

import core.model.Review;
import core.model.Reviewable;
import core.model.Traveler;

import java.util.List;

public class ReviewService {
    
    public void leaveReview(Review review) { }

    public List<Review> getReviewsForTarget(Reviewable target) {
        return null;
    }

    public List<Review> getReviewsByAuthor(Traveler author) {
        return null;
    }

    public double calculateAverageRating(Reviewable target) {
        return 0.0;
    }
}
