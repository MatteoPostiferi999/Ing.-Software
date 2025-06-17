package model.trip;

import model.assignment.AssignmentRegister;
import model.booking.BookingRegister;
import model.review.Review;
import model.review.ReviewRegister;
import model.review.Reviewable;
import model.user.Skill;
import model.application.ApplicationRegister;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class Trip implements Reviewable {
    private int tripId;
    private String title;
    private String description;
    private double price;
    private LocalDate date;

    private List<Skill> requiredSkills;
    private List<Activity> plannedActivities;
    private ReviewRegister reviews;
    private BookingRegister bookings;
    private AssignmentRegister assignedGuides;
    private ApplicationRegister applicationRegister;

    // Constructor for new Trip (ID will be assigned by the database)
    public Trip(String title, String description, double price, LocalDate date, int minTrav, int maxTrav, int maxGuides) {
        this.tripId = 0; // will be set by DB
        this.title = title;
        this.description = description;
        this.price = price;
        this.date = date;
        this.requiredSkills = new ArrayList<>();
        this.plannedActivities = new ArrayList<>();
        this.reviews = new ReviewRegister();
        this.bookings = new BookingRegister(minTrav, maxTrav);
        this.assignedGuides = new AssignmentRegister(maxGuides);
        this.applicationRegister = new ApplicationRegister();
    }

    public Trip(int tripId, String title, String description, double price, LocalDate date, int minTrav, int maxTrav, int maxGuides) {
        this.tripId = tripId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.date = date;
        this.requiredSkills = new ArrayList<>();
        this.plannedActivities = new ArrayList<>();
        this.reviews = new ReviewRegister();
        this.bookings = new BookingRegister(minTrav, maxTrav);
        this.assignedGuides = new AssignmentRegister(maxGuides);
        this.applicationRegister = new ApplicationRegister();
    }

    // Getters & Setters
    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Skill> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<Skill> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public List<Activity> getPlannedActivities() {
        return plannedActivities;
    }

    public void setPlannedActivities(List<Activity> plannedActivities) {
        this.plannedActivities = plannedActivities;
    }


    public void setReviews(ReviewRegister reviews) {
        this.reviews = reviews;
    }

    public BookingRegister getBookingRegister() {
        return bookings;
    }

    public void setBookingRegister(BookingRegister bookings) {
        this.bookings = bookings;
    }

    public AssignmentRegister getAssignmentRegister() {
        return assignedGuides;
    }

    public void setAssignmentRegister(AssignmentRegister assignedGuides) {
        this.assignedGuides = assignedGuides;
    }

    public model.application.ApplicationRegister getApplicationRegister() {
        return applicationRegister;
    }

    // Reviewable implementation

    @Override
    public void addReview(Review review) {
        reviews.addReview(review);
    }


    public int getMaxGuides() {
        return assignedGuides.getMaxGuides();
    }
}