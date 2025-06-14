package core.model;

import java.util.ArrayList;
import java.util.List;

public class Guide implements Notifiable, Reviewable {
    private int idGuide;
    private List<Skills> skills;
    private List<Trip> assignedTrips;
    private double rating;

    private List<Notification> notifications = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();

    // Constructor
    public Guide(int idGuide, List<Skills> skills, List<Trip> assignedTrips, double rating) {
        this.idGuide = idGuide;
        this.skills = skills;
        this.assignedTrips = assignedTrips;
        this.rating = rating;
    }

    // Getters and Setters
    public int getIdGuide() {
        return idGuide;
    }

    public void setIdGuide(int idGuide) {
        this.idGuide = idGuide;
    }

    public List<Skills> getSkills() {
        return skills;
    }

    public void setSkills(List<Skills> skills) {
        this.skills = skills;
    }

    public List<Trip> getAssignedTrips() {
        return assignedTrips;
    }

    public void setAssignedTrips(List<Trip> assignedTrips) {
        this.assignedTrips = assignedTrips;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    // Notifiable implementation
    @Override
    public void receiveNotification(Notification notification) {
        if (notification != null) {
            notifications.add(notification);
        }
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    // Reviewable implementation
    @Override
    public void addReview(Review review) {
        if (review != null) {
            reviews.add(review);
        }
    }

    @Override
    public List<Review> getReviews() {
        return reviews;
    }

    @Override
    public double getAverageRating() {
        // Logica disabilitata: demandata a ReviewService
        return 0.0;
    }
}
