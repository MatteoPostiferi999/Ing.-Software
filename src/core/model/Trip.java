package core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un viaggio con validazioni su attributi critici.
 */
public class Trip implements Reviewable {
    private int idTrip;
    private String title;
    private String description;
    private Skills requiredSkill;
    private double price;
    private TripStatus tripStatus;
    private int minTrav;
    private int maxTrav;
    private List<Activity> activities;
    private List<Review> reviews = new ArrayList<>();

    public Trip(int idTrip,
                String title,
                String description,
                Skills requiredSkill,
                double price,
                TripStatus tripStatus,
                int minTrav,
                int maxTrav,
                List<Activity> activities) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (requiredSkill == null) {
            throw new IllegalArgumentException("Required skill cannot be null");
        }
        if (tripStatus == null) {
            throw new IllegalArgumentException("Trip status cannot be null");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (minTrav < 0 || maxTrav < minTrav) {
            throw new IllegalArgumentException("Invalid travellers range: minTrav must be >= 0 and <= maxTrav");
        }
        if (activities == null) {
            throw new IllegalArgumentException("Activities list cannot be null");
        }

        this.idTrip = idTrip;
        this.title = title;
        this.description = description;
        this.requiredSkill = requiredSkill;
        this.price = price;
        this.tripStatus = tripStatus;
        this.minTrav = minTrav;
        this.maxTrav = maxTrav;
        this.activities = activities;
    }

    // Getters e setters

    public int getIdTrip() {
        return idTrip;
    }

    public void setIdTrip(int idTrip) {
        this.idTrip = idTrip;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        this.description = description;
    }

    public Skills getRequiredSkill() {
        return requiredSkill;
    }

    public void setRequiredSkill(Skills requiredSkill) {
        if (requiredSkill == null) {
            throw new IllegalArgumentException("Required skill cannot be null");
        }
        this.requiredSkill = requiredSkill;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    public TripStatus getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(TripStatus tripStatus) {
        if (tripStatus == null) {
            throw new IllegalArgumentException("Trip status cannot be null");
        }
        this.tripStatus = tripStatus;
    }

    public int getMinTrav() {
        return minTrav;
    }

    public void setMinTrav(int minTrav) {
        if (minTrav < 0 || minTrav > this.maxTrav) {
            throw new IllegalArgumentException("minTrav must be >= 0 and <= maxTrav");
        }
        this.minTrav = minTrav;
    }

    public int getMaxTrav() {
        return maxTrav;
    }

    public void setMaxTrav(int maxTrav) {
        if (maxTrav < this.minTrav) {
            throw new IllegalArgumentException("maxTrav must be >= minTrav");
        }
        this.maxTrav = maxTrav;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        if (activities == null) {
            throw new IllegalArgumentException("Activities list cannot be null");
        }
        this.activities = activities;
    }

    // Metodi richiesti da Reviewable (senza logica)
    @Override
    public List<Review> getReviews() {
        return reviews;
    }

    @Override
    public void addReview(Review review) {
        if (review != null) {
            this.reviews.add(review);
        }
    }

    @Override
    public double getAverageRating() {
        // Nessuna logica: implementazione vuota o da ignorare
        return 0.0; // verrà gestita da ReviewService
    }
}
