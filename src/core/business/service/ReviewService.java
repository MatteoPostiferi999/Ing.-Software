package core.business.service;

import core.dao.interfaces.ReviewDAO;
import core.model.Review;
import core.model.ReviewRegister;
import core.model.Reviewable;

import java.util.List;

public class ReviewService {
    private final ReviewRegister reviewRegister;
    private final ReviewDAO reviewDAO;

    public ReviewService(ReviewRegister reviewRegister, ReviewDAO reviewDAO) {
        this.reviewRegister = reviewRegister;
        this.reviewDAO = reviewDAO;
    }

    // Aggiunge una nuova recensione
    public void leaveReview(Review review) {
        reviewRegister.addReview(review);  // per navigazione runtime
        reviewDAO.save(review);            // per persistenza su DB
    }

    // Calcola la media dei voti per un Reviewable (Trip o Guide)
    public double getAverageRating(Reviewable target) {
        List<Review> reviews = target.getReviews();  // Recupera le recensioni associate al target da register o da interfaccia?
        if (reviews.isEmpty()) return 0.0;

        int total = 0;
        for (Review r : reviews) {
            total += r.getRating();
        }
        return (double) total / reviews.size();
    }
}
