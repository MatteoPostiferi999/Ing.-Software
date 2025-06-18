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
    // Attributi base
    private int tripId;
    private String title;
    private String description;
    private double price;
    private LocalDate date;

    // IDs per le relazioni (persistenza)
    private List<Integer> requiredSkillIds;
    private List<Integer> activityIds;

    // Registri per la gestione delle relazioni (logica di business)
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

        // Inizializzazione delle liste di ID per la persistenza
        this.requiredSkillIds = new ArrayList<>();
        this.activityIds = new ArrayList<>();

        // Inizializzazione delle liste e dei registri per la logica di business
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

        // Inizializzazione delle liste di ID per la persistenza
        this.requiredSkillIds = new ArrayList<>();
        this.activityIds = new ArrayList<>();

        // Inizializzazione delle liste e dei registri per la logica di business
        this.requiredSkills = new ArrayList<>();
        this.plannedActivities = new ArrayList<>();
        this.reviews = new ReviewRegister();
        this.bookings = new BookingRegister(minTrav, maxTrav);
        this.assignedGuides = new AssignmentRegister(maxGuides);
        this.applicationRegister = new ApplicationRegister();
    }

    // Getters & Setters per attributi base
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

    // Getters & Setters per liste di ID (persistenza)
    public List<Integer> getRequiredSkillIds() {
        return requiredSkillIds;
    }

    public void setRequiredSkillIds(List<Integer> requiredSkillIds) {
        this.requiredSkillIds = requiredSkillIds;
    }

    public void addRequiredSkillId(Integer skillId) {
        if (this.requiredSkillIds == null) {
            this.requiredSkillIds = new ArrayList<>();
        }
        this.requiredSkillIds.add(skillId);
    }

    public List<Integer> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<Integer> activityIds) {
        this.activityIds = activityIds;
    }

    public void addActivityId(Integer activityId) {
        if (this.activityIds == null) {
            this.activityIds = new ArrayList<>();
        }
        this.activityIds.add(activityId);
    }

    // Getters & Setters per le liste e i registri (logica di business)
    public List<Skill> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<Skill> requiredSkills) {
        this.requiredSkills = requiredSkills;

        // Aggiorna anche la lista degli ID
        this.requiredSkillIds.clear();
        for (Skill skill : requiredSkills) {
            this.requiredSkillIds.add(skill.getSkillId());
        }
    }

    public void addRequiredSkill(Skill skill) {
        if (this.requiredSkills == null) {
            this.requiredSkills = new ArrayList<>();
        }
        this.requiredSkills.add(skill);

        // Aggiorna anche la lista degli ID
        if (this.requiredSkillIds == null) {
            this.requiredSkillIds = new ArrayList<>();
        }
        this.requiredSkillIds.add(skill.getSkillId());
    }

    public List<Activity> getPlannedActivities() {
        return plannedActivities;
    }

    public void setPlannedActivities(List<Activity> activities) {
        this.plannedActivities = activities;

        // Aggiorna anche la lista degli ID
        this.activityIds.clear();
        for (Activity activity : activities) {
            this.activityIds.add(activity.getActivityId());
        }
    }

    public void addPlannedActivity(Activity activity) {
        if (this.plannedActivities == null) {
            this.plannedActivities = new ArrayList<>();
        }
        this.plannedActivities.add(activity);

        // Aggiorna anche la lista degli ID
        if (this.activityIds == null) {
            this.activityIds = new ArrayList<>();
        }
        this.activityIds.add(activity.getActivityId());
    }

    public ReviewRegister getReviewRegister() {
        return reviews;
    }

    public void setReviewRegister(ReviewRegister reviews) {
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

    public ApplicationRegister getApplicationRegister() {
        return applicationRegister;
    }

    public void setApplicationRegister(ApplicationRegister applicationRegister) {
        this.applicationRegister = applicationRegister;
    }

    // Implementazione di Reviewable
    @Override
    public void addReview(Review review) {
        reviews.addReview(review);
    }

    // Altri metodi di business logic
    public boolean isAlreadyStarted() {
        return date.isBefore(LocalDate.now());
    }

    public int getMaxGuides() {
        return assignedGuides.getMaxGuides();
    }

    public int getMaxTravelers() {
        return bookings.getMaxTrav();
    }

    public int getId() {
        return tripId;
    }

    public void setId(int id) {
        this.tripId = id;
    }
}