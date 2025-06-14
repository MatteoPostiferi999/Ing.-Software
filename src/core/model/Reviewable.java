package core.model;

import java.util.List;

public interface Reviewable {
    List<Review> getReviews();
    void addReview(Review review);
    double getAverageRating();
}
