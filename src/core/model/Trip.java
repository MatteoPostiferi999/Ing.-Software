package core.model;

import java.util.List;
import core.model.Activity;
import core.model.Skills;
import core.model.TripStatus;
/**
 * Rappresenta un viaggio con validazioni su attributi critici.
 */
public class Trip {
    private int idTrip;
    private String title;
    private String description;
    private Skills requiredSkill;
    private double price;
    private TripStatus tripStatus;
    private int minTrav;
    private int maxTrav;
    private List<Activity> activities;

    /**
     * Costruttore con validazioni:
     * - title e description non null/empty
     * - requiredSkill e tripStatus non null
     * - price >= 0
     * - 0 <= minTrav <= maxTrav
     * - activities non null
     */
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

    // Getters and setters con validazioni

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
}