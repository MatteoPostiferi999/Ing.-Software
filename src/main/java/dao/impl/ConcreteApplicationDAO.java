package dao.impl;

import dao.interfaces.ApplicationDAO;
import db.DBManager;
import model.application.Application;
import model.application.ApplicationStatus;
import model.user.Guide;
import model.trip.Trip;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConcreteApplicationDAO implements ApplicationDAO {
    private final DBManager dbManager = DBManager.getInstance();

    @Override
    public void save(Application application) {
        String sql = "INSERT INTO application (cv, status, guide_id, trip_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, application.getCV());
            stmt.setString(2, application.getStatus().name());
            stmt.setInt(3, application.getGuide().getGuideId());
            stmt.setInt(4, application.getTrip().getTripId());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    application.setApplicationId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateStatus(Application application, ApplicationStatus status) {
        String sql = "UPDATE application SET status = ? WHERE application_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, application.getApplicationId());
            stmt.executeUpdate();
            application.setStatus(status);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Application application) {
        String sql = "DELETE FROM application WHERE application_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, application.getApplicationId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Application getById(int id) {
        String sql = "SELECT * FROM application WHERE application_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Qui dovresti recuperare Guide e Trip tramite i rispettivi DAO
                    Guide guide = new Guide(rs.getInt("guide_id")); // Sostituisci con recupero reale
                    Trip trip = new Trip(rs.getInt("trip_id"));     // Sostituisci con recupero reale
                    return new Application(
                        rs.getInt("application_id"),
                        rs.getString("cv"),
                        guide,
                        trip,
                        ApplicationStatus.valueOf(rs.getString("status"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Application> getByTrip(Trip trip) {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT * FROM application WHERE trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trip.getTripId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Guide guide = new Guide(rs.getInt("guide_id")); // Sostituisci con recupero reale
                    applications.add(new Application(
                        rs.getInt("application_id"),
                        rs.getString("cv"),
                        guide,
                        trip,
                        ApplicationStatus.valueOf(rs.getString("status"))
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    @Override
    public List<Application> getByGuide(Guide guide) {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT * FROM application WHERE guide_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guide.getGuideId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Trip trip = new Trip(rs.getInt("trip_id")); // Sostituisci con recupero reale
                    applications.add(new Application(
                        rs.getInt("application_id"),
                        rs.getString("cv"),
                        guide,
                        trip,
                        ApplicationStatus.valueOf(rs.getString("status"))
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }
}