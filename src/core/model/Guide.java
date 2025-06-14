package core.model;
// package core.model;

import java.util.List;

public class Guide {
    private int idGuide;
    private List<Skills> skills;
    private List<Trip> assignedTrips;
    private double rating;

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
}