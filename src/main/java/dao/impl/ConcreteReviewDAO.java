package dao.impl;

import dao.interfaces.ReviewDAO;
import dao.interfaces.TravelerDAO;
import dao.interfaces.GuideDAO;
import dao.interfaces.TripDAO;
import db.DBManager;
import model.review.Review;
import model.review.Reviewable;
import model.user.Traveler;
import model.user.Guide;
import model.trip.Trip;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConcreteReviewDAO implements ReviewDAO {
    private final DBManager dbManager = DBManager.getInstance();
    private TravelerDAO travelerDAO;
    private GuideDAO guideDAO;
    private TripDAO tripDAO;

    // Constructor
    public ConcreteReviewDAO() {
        // Default constructor
    }

    // Constructor with dependencies
    public ConcreteReviewDAO(TravelerDAO travelerDAO, GuideDAO guideDAO, TripDAO tripDAO) {
        this.travelerDAO = travelerDAO;
        this.guideDAO = guideDAO;
        this.tripDAO = tripDAO;
    }

    // Setters for DAOs
    public void setTravelerDAO(TravelerDAO travelerDAO) {
        this.travelerDAO = travelerDAO;
    }

    public void setGuideDAO(GuideDAO guideDAO) {
        this.guideDAO = guideDAO;
    }

    public void setTripDAO(TripDAO tripDAO) {
        this.tripDAO = tripDAO;
    }

    @Override
    public Review getById(int id) {
        String sql = "SELECT * FROM review WHERE review_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return createReviewFromResultSet(rs, false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Review> getByTarget(Reviewable target) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM review WHERE target_id = ? AND target_type = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String targetType = target.getClass().getSimpleName();
            int targetId = getTargetId(target);

            stmt.setInt(1, targetId);
            stmt.setString(2, targetType);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reviews.add(createReviewFromResultSet(rs, false));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    @Override
    public List<Review> getByAuthor(Traveler author) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM review WHERE author_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, author.getUserID());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Since we know the author, we can pass it directly to avoid loading it again
                Review review = createReviewFromResultSet(rs, true);
                review.setAuthor(author);
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    @Override
    public void save(Review review) {
        if (review.getReviewID() == 0) {
            insertReview(review);
        } else {
            updateReview(review);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM review WHERE review_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to insert a new review
    private void insertReview(Review review) {
        String sql = "INSERT INTO review (rating, text, author_id, target_id, target_type) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, review.getRating());
            stmt.setString(2, review.getText());
            stmt.setInt(3, review.getAuthorID());
            stmt.setInt(4, review.getTargetID());
            stmt.setString(5, review.getTargetType());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        review.setReviewID(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to update an existing review
    private void updateReview(Review review) {
        String sql = "UPDATE review SET rating = ?, text = ? WHERE review_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, review.getRating());
            stmt.setString(2, review.getText());
            stmt.setInt(3, review.getReviewID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to create a Review object from ResultSet
    private Review createReviewFromResultSet(ResultSet rs, boolean lazyLoad) throws SQLException {
        int reviewId = rs.getInt("review_id");
        int rating = rs.getInt("rating");
        String text = rs.getString("text");
        int authorId = rs.getInt("author_id");
        int targetId = rs.getInt("target_id");
        String targetType = rs.getString("target_type");

        if (lazyLoad) {
            // Return a review with IDs only (for lazy loading)
            return new Review(reviewId, rating, text, authorId, targetId, targetType);
        } else {
            // Load author using TravelerDAO
            Traveler author = travelerDAO.findById(authorId);

            // Load target based on target type
            Reviewable target = null;
            if ("Guide".equals(targetType)) {
                target = guideDAO.findById(targetId);
            } else if ("Trip".equals(targetType)) {
                target = (Reviewable) tripDAO.findById(targetId);
            }

            return new Review(reviewId, rating, text, author, target);
        }
    }

    // Helper method to get ID of the target (Guide or Trip)
    private int getTargetId(Reviewable target) {
        try {
            String className = target.getClass().getSimpleName();

            if ("Guide".equals(className)) {
                return (int) target.getClass().getMethod("getGuideId").invoke(target);
            } else if ("Trip".equals(className)) {
                return (int) target.getClass().getMethod("getTripId").invoke(target);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
