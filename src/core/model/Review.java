package core.model;
// Review.java
// package core.model;

/**
 * Classe astratta che rappresenta una recensione generica.
 */
public abstract class Review {
    private int idReview;
    private int rating;  // da 1 a 5
    private String text;

    public Review(int idReview, int rating, String text) {
        this.idReview = idReview;
        setRating(rating);
        this.text = text;
    }

    public int getIdReview() {
        return idReview;
    }

    public void setIdReview(int idReview) {
        this.idReview = idReview;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}