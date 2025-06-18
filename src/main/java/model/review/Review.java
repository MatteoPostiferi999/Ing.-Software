package model.review;

import model.user.Traveler;

public class Review {
    private int reviewID;
    private int rating;
    private String text;
    private Traveler author;
    private Reviewable target;
    private int authorID;
    private int targetID;
    private String targetType;

    // Constructor for new reviews (reviewID will be assigned later)
    public Review(int rating, String text, Traveler author, Reviewable target) {
        this.reviewID = 0;
        this.rating = rating;
        this.text = text;
        this.author = author;
        this.target = target;
        this.authorID = author.getTravelerId();
        this.setTargetInfo(target);
    }

    // Constructor for reconstruction from database
    public Review(int reviewID, int rating, String text, Traveler author, Reviewable target) {
        this.reviewID = reviewID;
        this.rating = rating;
        this.text = text;
        this.author = author;
        this.target = target;
        this.authorID = author != null ? author.getTravelerId() : 0;
        this.setTargetInfo(target);
    }

    // Constructor for lazy loading
    public Review(int reviewID, int rating, String text, int authorID, int targetID, String targetType) {
        this.reviewID = reviewID;
        this.rating = rating;
        this.text = text;
        this.author = null;
        this.target = null;
        this.authorID = authorID;
        this.targetID = targetID;
        this.targetType = targetType;
    }

    // Helper method to set target info
    private void setTargetInfo(Reviewable target) {
        if (target != null) {
            this.targetType = target.getClass().getSimpleName();

            // Extract the target ID based on its type
            if ("Guide".equals(this.targetType)) {
                try {
                    this.targetID = (int) target.getClass().getMethod("getUserID").invoke(target);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ("Trip".equals(this.targetType)) {
                try {
                    this.targetID = (int) target.getClass().getMethod("getTripID").invoke(target);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Getters
    public int getReviewID() {
        return reviewID;
    }

    public int getRating() {
        return rating;
    }

    public String getText() {
        return text;
    }

    public Traveler getAuthor() {
        return author;
    }

    public Reviewable getTarget() {
        return target;
    }

    public int getAuthorID() {
        return authorID;
    }

    public int getTargetID() {
        return targetID;
    }

    public String getTargetType() {
        return targetType;
    }

    // Setters
    public void setReviewID(int reviewID) {
        this.reviewID = reviewID;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAuthor(Traveler author) {
        this.author = author;
        if (author != null) {
            this.authorID = author.getTravelerId();
        }
    }

    public void setTarget(Reviewable target) {
        this.target = target;
        setTargetInfo(target);
    }

    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }

    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
}
