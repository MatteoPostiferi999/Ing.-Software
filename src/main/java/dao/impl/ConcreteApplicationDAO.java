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

    // Costruttore di default
    public ConcreteApplicationDAO() {
        // Default constructor
    }

    // Costruttore con dependency injection
    public ConcreteApplicationDAO(GuideDAO guideDAO, TripDAO tripDAO) {
        this.guideDAO = guideDAO;
        this.tripDAO = tripDAO;
    }

    // Metodi per dependency injection
    public void setGuideDAO(GuideDAO guideDAO) {
        this.guideDAO = guideDAO;
    }

    public void setTripDAO(TripDAO tripDAO) {
        this.tripDAO = tripDAO;
    }

    @Override
    public void save(Application application) {
        if (application.getApplicationId() == 0) {
            // Nuova candidatura
            insertApplication(application);
        } else {
            // Aggiornamento completo (non solo lo status)
            updateApplication(application);
        }
    }

    private void insertApplication(Application application) {
        String sql = "INSERT INTO application (cv, status, guide_id, trip_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, application.getCV());
            stmt.setString(2, application.getStatus().name());
            stmt.setInt(3, application.getGuideId());
            stmt.setInt(4, application.getTripId());
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
        String sql = "UPDATE application SET cv = ?, status = ?, guide_id = ?, trip_id = ? WHERE application_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, application.getCV());
            stmt.setString(2, application.getStatus().name());
            stmt.setInt(3, application.getGuideId());
            stmt.setInt(4, application.getTripId());
            stmt.setInt(5, application.getApplicationId());
            stmt.executeUpdate();
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
        String sql = "SELECT * FROM application WHERE trip_id = ?";
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
        String sql = "SELECT * FROM application WHERE guide_id = ?";
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
        List<Application> applications = getByTripId(trip.getTripId());
        for (Application app : applications) {
            // Carica la guida se non è già caricata
            if (app.getGuide() == null && guideDAO != null) {
                Guide guide = guideDAO.findById(app.getGuideId());
                app.setGuide(guide);
            }

            // Imposta il riferimento al viaggio
            app.setTrip(trip);

            // Aggiungi la candidatura al registro del viaggio
            trip.getApplicationRegister().addApplication(app);
        }
    }

    @Override
    public Application findByGuideAndTrip(int guideId, int tripId) {
        String sql = "SELECT * FROM application WHERE guide_id = ? AND trip_id = ?";
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
        String sql = "SELECT * FROM application WHERE status = ?";
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
        String sql = "SELECT * FROM application WHERE trip_id = ? AND status = ?";
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
        String sql = "SELECT COUNT(*) AS count FROM application WHERE trip_id = ?";
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
        String sql = "SELECT COUNT(*) AS count FROM application WHERE trip_id = ? AND status = ?";
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

    // Metodo helper per creare un'istanza di Application dal ResultSet
    private Application createApplicationFromResultSet(ResultSet rs) throws SQLException {
        int applicationId = rs.getInt("application_id");
        String cv = rs.getString("cv");
        int guideId = rs.getInt("guide_id");
        int tripId = rs.getInt("trip_id");
        ApplicationStatus status = ApplicationStatus.valueOf(rs.getString("status"));

        // Crea l'applicazione con gli ID
        Application application = new Application(applicationId, cv, guideId, tripId, status);

        // Se disponibili, carica gli oggetti correlati
        if (guideDAO != null) {
            Guide guide = guideDAO.findById(guideId);
            application.setGuide(guide);
        }

        if (tripDAO != null) {
            Trip trip = tripDAO.findById(tripId);
            application.setTrip(trip);
        }

        return application;
    }
}