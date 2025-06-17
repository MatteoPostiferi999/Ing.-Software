package business.service;

import model.review.Review;
import model.review.ReviewRegister;
import model.review.Reviewable;
import dao.interfaces.ReviewDAO;

public class ReviewService {

    private final ReviewDAO reviewDAO;
    private final ReviewRegister reviewRegister;

    public ReviewService(ReviewDAO reviewDAO, ReviewRegister reviewRegister) {
        this.reviewDAO = reviewDAO;
        this.reviewRegister = reviewRegister;
    }

    public void addReview(Review review) {
        reviewRegister.addReview(review);
        reviewDAO.save(review);
    }

    public void createAndAddReview(int rating, String comment, Reviewable target, model.user.Traveler author) {
        Review review = new Review(rating, comment, author, target);
        reviewRegister.addReview(review);

        reviewDAO.save(review);
    }
}
