package model.review;

import model.user.Traveler;

public class Review {
    private int reviewID;
    private int rating;
    private String text;
    private Traveler author;
    private Reviewable target;

    // Constructor for new reviews (reviewID will be assigned later)
    public Review(int rating, String text, Traveler author, Reviewable target) {
        this.reviewID = 0;
        this.rating = rating;
        this.text = text;
        this.author = author;
        this.target = target;
    }

    // Constructor for reconstruction from database
    public Review(int reviewID, int rating, String text, Traveler author, Reviewable target) {
        this.reviewID = reviewID;
        this.rating = rating;
        this.text = text;
        this.author = author;
        this.target = target;
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
    }

    public void setTarget(Reviewable target) {
        this.target = target;
    }
}
