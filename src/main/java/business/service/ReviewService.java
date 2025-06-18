package business.service;

import model.review.Review;
import model.review.ReviewRegister;
import model.review.Reviewable;
import dao.interfaces.ReviewDAO;
import model.user.Traveler;
import model.notification.Notifiable;

import java.util.List;

public class ReviewService {

    private final ReviewDAO reviewDAO;
    private final ReviewRegister reviewRegister;
    private NotificationService notificationService;

    public ReviewService(ReviewDAO reviewDAO, ReviewRegister reviewRegister) {
        this.reviewDAO = reviewDAO;
        this.reviewRegister = reviewRegister;
    }

    // Nuovo costruttore che include NotificationService
    public ReviewService(ReviewDAO reviewDAO, ReviewRegister reviewRegister, NotificationService notificationService) {
        this.reviewDAO = reviewDAO;
        this.reviewRegister = reviewRegister;
        this.notificationService = notificationService;
    }

    // Setter per dependency injection
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void addReview(Review review) {
        reviewRegister.addReview(review);
        reviewDAO.save(review);
    }

    public void createAndAddReview(int rating, String comment, Reviewable target, Traveler author) {
        Review review = new Review(rating, comment, author, target);
        reviewRegister.addReview(review);
        reviewDAO.save(review);

        // Notifichiamo il target che ha ricevuto una recensione
        if (target instanceof Notifiable && notificationService != null) {
            String message = String.format("Hai ricevuto una nuova recensione da %s con valutazione: %d/5",
                author.getUserName(), rating);
            notificationService.sendNotification((Notifiable) target, message);
        }
    }

    public List<Review> getReviewsByTarget(Reviewable target) {
        return reviewDAO.getByTarget(target);
    }

    public List<Review> getReviewsByAuthor(Traveler author) {
        return reviewDAO.getByAuthor(author);
    }

    public Review getReviewById(int reviewId) {
        return reviewDAO.getById(reviewId);
    }

    public void deleteReview(Review review) {
        reviewRegister.removeReview(review);
        reviewDAO.delete(review.getReviewID());
    }

    public void updateReview(int reviewId, int newRating, String newText) {
        Review review = reviewDAO.getById(reviewId);
        if (review != null) {
            review.setRating(newRating);
            review.setText(newText);
            reviewDAO.save(review);

            // Aggiorna anche il reviewRegister se contiene questa recensione
            List<Review> reviews = reviewRegister.getReviews();
            for (int i = 0; i < reviews.size(); i++) {
                if (reviews.get(i).getReviewID() == reviewId) {
                    reviews.get(i).setRating(newRating);
                    reviews.get(i).setText(newText);
                    break;
                }
            }
        }
    }

    public double getAverageRating() {
        return reviewRegister.getAverageRating();
    }

    public List<Review> getAllReviews() {
        return reviewRegister.getReviews();
    }
}
