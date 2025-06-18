package model.user;

import model.notification.Notifiable;
import model.notification.Notification;
import model.notification.NotificationRegister;
import model.review.Review;
import model.review.ReviewRegister;
import model.review.Reviewable;

import java.util.ArrayList;
import java.util.List;

public class Guide implements Notifiable, Reviewable {
    private int guideId;
    private List<Integer> skillIds;  // IDs delle skill per la persistenza
    private List<Skill> skills;      // Oggetti Skill per la logica di business

    private ReviewRegister reviews;
    private NotificationRegister notifications;
    private User owner;

    // Constructor for new Guide (created from scratch)
    public Guide(User owner) {
        this.guideId = 0;
        this.skillIds = new ArrayList<>();
        this.skills = new ArrayList<>();
        this.owner = owner;
        this.reviews = new ReviewRegister();
        this.notifications = new NotificationRegister();
    }

    // Constructor for Guide reconstructed from database
    public Guide(int guideId, List<Skill> skills, User owner, ReviewRegister reviews, NotificationRegister notifications) {
        this.guideId = guideId;
        this.skillIds = new ArrayList<>();
        this.skills = skills;

        // Estrai gli ID dalle skills
        if (skills != null) {
            for (Skill skill : skills) {
                this.skillIds.add(skill.getSkillId());
            }
        }

        this.owner = owner;
        this.reviews = reviews;
        this.notifications = notifications;
    }

    // Constructor with separate skillIds
    public Guide(int guideId, List<Integer> skillIds, List<Skill> skills, User owner, ReviewRegister reviews, NotificationRegister notifications) {
        this.guideId = guideId;
        this.skillIds = skillIds;
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

    public List<Integer> getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(List<Integer> skillIds) {
        this.skillIds = skillIds;
    }

    public void addSkillId(int skillId) {
        if (this.skillIds == null) {
            this.skillIds = new ArrayList<>();
        }
        this.skillIds.add(skillId);
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;

        // Aggiorna anche la lista degli ID
        this.skillIds = new ArrayList<>();
        if (skills != null) {
            for (Skill skill : skills) {
                this.skillIds.add(skill.getSkillId());
            }
        }
    }

    public void addSkill(Skill skill) {
        if (this.skills == null) {
            this.skills = new ArrayList<>();
        }
        this.skills.add(skill);

        // Aggiunge anche l'ID alla lista degli ID
        if (this.skillIds == null) {
            this.skillIds = new ArrayList<>();
        }
        this.skillIds.add(skill.getSkillId());
    }

    public ReviewRegister getReviewsRegister() {
        return reviews;
    }

    public void setReviewsRegister(ReviewRegister reviews) {
        this.reviews = reviews;
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

    @Override
    public NotificationRegister getNotificationRegister() {
        return notifications;
    }

    // Reviewable implementation
    @Override
    public void addReview(Review review) {
        reviews.addReview(review);
    }

    public ReviewRegister getReviewRegister() {
        return reviews;
    }


    public double getRating() {
        return reviews.getAverageRating();
    }


}
