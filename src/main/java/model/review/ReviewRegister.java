package model.review;

import java.util.ArrayList;
import java.util.List;



public class ReviewRegister {
    private List<Review> reviews = new ArrayList<>();
    private double averageRating;

    public ReviewRegister() {
        reviews = new ArrayList<>();
        this.averageRating = 0.0;
    }

    // Constructor for reconstruction from database
    public ReviewRegister(List<Review> reviews) {
        this.reviews = reviews;
        updateAverageRating();
    }



    public void addReview(Review review) {
        reviews.add(review);
        updateAverageRating();
    }

    public void removeReview(Review review) {
        reviews.remove(review);
        updateAverageRating();
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public double getAverageRating() {
        return averageRating;
    }

    private void updateAverageRating() {
        if (reviews.isEmpty()) {
            averageRating = 0;
            return;
        }
        int total = 0;
        for (Review r : reviews) {
            total += r.getRating();
        }
        averageRating = (double) total / reviews.size();
    }
}

