package core.model;

/**
 * Classe astratta che rappresenta una recensione generica tra Traveler e un Reviewable (Trip o Guide), basata su ID.
 */
public abstract class Review {
    private int idReview;
    private int rating;  // da 1 a 5
    private String text;

    private int authorId;   // id del Traveler
    private int targetId;   // id del Reviewable (Trip o Guide)

    public Review(int idReview, int rating, String text, int authorId, int targetId) {
        this.idReview = idReview;
        setRating(rating);
        this.text = text;
        this.authorId = authorId;
        this.targetId = targetId;
    }

    // Getter e setter

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

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }
}
