package model.review;

import java.util.ArrayList;
import java.util.List;


/**
 * ReviewRegister rappresenta una collezione di recensioni associate a un'entità Reviewable.
 * Aggregazione: le Review sono create esternamente e aggiunte tramite addReview().
 * Il campo averageRating non viene aggiornato automaticamente: va gestito esternamente (es. via ReviewService).
 */
public class ReviewRegister {
    private List<Review> reviews = new ArrayList<>();
    private double averageRating; // valore settabile dall'esterno

    public void addReview(Review review) {
        reviews.add(review);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
