package dao.impl;

import dao.interfaces.ActivityDAO;
import db.DBManager;
import model.trip.Activity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConcreteActivityDAO implements ActivityDAO {
    private final DBManager dbManager = DBManager.getInstance();

    @Override
    public void save(Activity activity) {
        String sql = "INSERT INTO activity (duration, description, name, trip_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, activity.getDuration());
            stmt.setString(2, activity.getDescription());
            stmt.setString(3, activity.getName());
            stmt.setInt(4, activity.getTripId());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    activity.setActivityId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Activity activity) {
        String sql = "UPDATE activity SET duration = ?, description = ?, name = ?, trip_id = ? WHERE activity_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, activity.getDuration());
            stmt.setString(2, activity.getDescription());
            stmt.setString(3, activity.getName());
            stmt.setInt(4, activity.getTripId());
            stmt.setInt(5, activity.getActivityId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Activity findById(int id) {
        String sql = "SELECT * FROM activity WHERE activity_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Activity(
                        rs.getInt("activity_id"),
                        rs.getInt("duration"),
                        rs.getString("description"),
                        rs.getString("name"),
                        rs.getInt("trip_id")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Activity> findByTripId(int tripId) {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT * FROM activity WHERE trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tripId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(new Activity(
                        rs.getInt("activity_id"),
                        rs.getInt("duration"),
                        rs.getString("description"),
                        rs.getString("name"),
                        rs.getInt("trip_id")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activities;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM activity WHERE activity_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Manteniamo i metodi esistenti per retrocompatibilità
    public List<Activity> getAllByTrip(int tripId) {
        return findByTripId(tripId);
    }

    public void addToTrip(int tripId, Activity activity) {
        activity.setTripId(tripId);
        save(activity);
    }

    public void removeFromTrip(int tripId, String activityName) {
        String sql = "DELETE FROM activity WHERE trip_id = ? AND name = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tripId);
            stmt.setString(2, activityName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}