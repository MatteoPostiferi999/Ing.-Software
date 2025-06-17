package model.user;

import model.notification.Notifiable;
import model.notification.Notification;
import model.notification.NotificationRegister;
import model.review.Review;
import model.review.ReviewRegister;
import model.review.Reviewable;

import java.util.List;

public class Guide implements Notifiable, Reviewable {
    private int guideId;
    private List<Skill> skills;

    private ReviewRegister reviews;
    private NotificationRegister notifications;
    private User owner;

    // Constructor for new Guide (created from scratch)
    public Guide(User owner) {
        this.guideId = 0;
        this.skills = null;
        this.owner = owner;
        this.reviews = new ReviewRegister();
        this.notifications = new NotificationRegister();
    }

    // Constructor for Guide reconstructed from database
    public Guide(int guideId, List<Skill> skills, User owner, ReviewRegister reviews, NotificationRegister notifications) {
        this.guideId = guideId;
        this.skills = skills;
        this.owner = owner;
        this.reviews = reviews;
        this.notifications = notifications;
    }

    // Getters and Setters
    public int getGuideId() {
        return guideId;
    }

    public void setGuideId(int guideId) {
        this.guideId = guideId;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }


    public ReviewRegister getReviewsRegister() {
        return reviews;
    }

    public void setReviewsRegister(ReviewRegister reviews) {
        this.reviews = reviews;
    }

    public NotificationRegister getNotificationRegister() {
        return notifications;
    }

    public void setNotificationRegister(NotificationRegister notifications) {
        this.notifications = notifications;
    }

    public String getUserName() {
    return owner.getUserName();
    }

    public String getEmail() {
        return owner.getEmail();
    }

    public String getPassword() {
        return owner.getPassword();
    }
    public User getOwner() {
        return owner;
    }
    public void setOwner(User owner) {
        this.owner = owner;
    }

    // Notifiable implementation
    @Override
    public void receiveNotification(Notification notification) {
        notifications.addNotification(notification);
    }

    // Reviewable implementation
    @Override
    public void addReview(Review review) {
        reviews.addReview(review);
    }


    public double getRating() {
        return reviews.getAverageRating();
    }
}
