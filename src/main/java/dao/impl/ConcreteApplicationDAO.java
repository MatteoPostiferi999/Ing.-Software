package dao.impl;

import dao.interfaces.ApplicationDAO;
import dao.interfaces.GuideDAO;
import dao.interfaces.TripDAO;
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
    private GuideDAO guideDAO;
    private TripDAO tripDAO;

    public ConcreteApplicationDAO() { }

    public ConcreteApplicationDAO(GuideDAO guideDAO, TripDAO tripDAO) {
        this.guideDAO = guideDAO;
        this.tripDAO = tripDAO;
    }

    public void setGuideDAO(GuideDAO guideDAO) {
        this.guideDAO = guideDAO;
    }

    public void setTripDAO(TripDAO tripDAO) {
        this.tripDAO = tripDAO;
    }

    @Override
    public void save(Application application) {
        if (application.getApplicationId() == 0) {
            insertApplication(application);
        } else {
            updateApplication(application);
        }
    }

    private void insertApplication(Application application) {
        String sql = "INSERT INTO applications (trip_id, guide_id, cv, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, application.getTripId());
            stmt.setInt(2, application.getGuideId());
            stmt.setString(3, application.getCV());
            stmt.setString(4, application.getStatus().name());
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

    private void updateApplication(Application application) {
        String sql = "UPDATE applications SET trip_id = ?, guide_id = ?, cv = ?, status = ? WHERE application_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, application.getTripId());
            stmt.setInt(2, application.getGuideId());
            stmt.setString(3, application.getCV());
            stmt.setString(4, application.getStatus().name());
            stmt.setInt(5, application.getApplicationId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateStatus(Application application, ApplicationStatus status) {
        String sql = "UPDATE applications SET status = ? WHERE application_id = ?";
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
        String sql = "DELETE FROM applications WHERE application_id = ?";
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
        String sql = "SELECT application_id, trip_id, guide_id, cv, status FROM applications WHERE application_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createApplicationFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Application> getByTrip(Trip trip) {
        return getByTripId(trip.getTripId());
    }

    @Override
    public List<Application> getByTripId(int tripId) {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT application_id, trip_id, guide_id, cv, status FROM applications WHERE trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tripId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(createApplicationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    @Override
    public List<Application> getByGuide(Guide guide) {
        return getByGuideId(guide.getGuideId());
    }

    @Override
    public List<Application> getByGuideId(int guideId) {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT application_id, trip_id, guide_id, cv, status FROM applications WHERE guide_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, guideId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(createApplicationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    @Override
    public void loadApplicationsForTrip(Trip trip) {
        for (Application app : getByTripId(trip.getTripId())) {
            if (app.getGuide() == null && guideDAO != null) {
                Guide g = guideDAO.findById(app.getGuideId());
                app.setGuide(g);
            }
            app.setTrip(trip);
            trip.getApplicationRegister().addApplication(app);
        }
    }

    @Override
    public Application findByGuideAndTrip(int guideId, int tripId) {
        String sql = "SELECT application_id, trip_id, guide_id, cv, status FROM applications WHERE guide_id = ? AND trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, guideId);
            stmt.setInt(2, tripId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createApplicationFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Application> findByStatus(ApplicationStatus status) {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT application_id, trip_id, guide_id, cv, status FROM applications WHERE status = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(createApplicationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    @Override
    public List<Application> findByTripAndStatus(int tripId, ApplicationStatus status) {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT application_id, trip_id, guide_id, cv, status FROM applications WHERE trip_id = ? AND status = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tripId);
            stmt.setString(2, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(createApplicationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    @Override
    public boolean hasGuideAppliedForTrip(int guideId, int tripId) {
        return findByGuideAndTrip(guideId, tripId) != null;
    }

    @Override
    public int countApplicationsByTripId(int tripId) {
        String sql = "SELECT COUNT(*) AS count FROM applications WHERE trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tripId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int countPendingApplicationsByTripId(int tripId) {
        String sql = "SELECT COUNT(*) AS count FROM applications WHERE trip_id = ? AND status = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tripId);
            stmt.setString(2, ApplicationStatus.PENDING.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Application createApplicationFromResultSet(ResultSet rs) throws SQLException {
        int applicationId = rs.getInt("application_id");
        int tripId = rs.getInt("trip_id");
        int guideId = rs.getInt("guide_id");
        String cv = rs.getString("cv");
        ApplicationStatus status = ApplicationStatus.valueOf(rs.getString("status"));

        Application application = new Application(applicationId, cv, guideId, tripId, status);

        // Lazy loading of related entities
        if (guideDAO != null) {
            Guide g = guideDAO.findById(guideId);
            application.setGuide(g);
        }
        if (tripDAO != null) {
            Trip t = tripDAO.findById(tripId);
            application.setTrip(t);
        }

        return application;
    }
}