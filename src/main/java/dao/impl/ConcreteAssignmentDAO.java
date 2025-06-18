package dao.impl;

import dao.interfaces.AssignmentDAO;
import dao.interfaces.GuideDAO;
import dao.interfaces.TripDAO;
import db.DBManager;
import model.assignment.Assignment;
import model.trip.Trip;
import model.user.Guide;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConcreteAssignmentDAO implements AssignmentDAO {
    private final DBManager dbManager = DBManager.getInstance();
    private GuideDAO guideDAO;
    private TripDAO tripDAO;

    // Costruttore base
    public ConcreteAssignmentDAO() {
        // Default constructor
    }

    // Costruttore con dependency injection
    public ConcreteAssignmentDAO(GuideDAO guideDAO, TripDAO tripDAO) {
        this.guideDAO = guideDAO;
        this.tripDAO = tripDAO;
    }

    // Setter per dependency injection
    public void setGuideDAO(GuideDAO guideDAO) {
        this.guideDAO = guideDAO;
    }

    public void setTripDAO(TripDAO tripDAO) {
        this.tripDAO = tripDAO;
    }

    @Override
    public void save(Assignment assignment) {
        if (assignment.getAssignmentId() == 0) {
            // Nuova assegnazione
            insertAssignment(assignment);
        } else {
            // Aggiornamento
            update(assignment);
        }
    }

    private void insertAssignment(Assignment assignment) {
        String sql = "INSERT INTO assignment (guide_id, trip_id, assignment_date) VALUES (?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, assignment.getGuideId());
            stmt.setInt(2, assignment.getTripId());
            stmt.setDate(3, Date.valueOf(assignment.getDate()));
            stmt.executeUpdate();

            // Ottieni l'ID generato
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    assignment.setAssignmentId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Assignment assignment) {
        String sql = "UPDATE assignment SET guide_id = ?, trip_id = ?, assignment_date = ? WHERE assignment_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignment.getGuideId());
            stmt.setInt(2, assignment.getTripId());
            stmt.setDate(3, Date.valueOf(assignment.getDate()));
            stmt.setInt(4, assignment.getAssignmentId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Assignment assignment) {
        String sql = "DELETE FROM assignment WHERE assignment_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignment.getAssignmentId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Assignment findById(int assignmentId) {
        String sql = "SELECT * FROM assignment WHERE assignment_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createAssignmentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Assignment> findAll() {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM assignment";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                assignments.add(createAssignmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }

    @Override
    public List<Assignment> findByGuideId(int guideId) {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM assignment WHERE guide_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guideId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    assignments.add(createAssignmentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }

    @Override
    public List<Assignment> findByTripId(int tripId) {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM assignment WHERE trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tripId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    assignments.add(createAssignmentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }

    @Override
    public Assignment findByGuideAndTrip(int guideId, int tripId) {
        String sql = "SELECT * FROM assignment WHERE guide_id = ? AND trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guideId);
            stmt.setInt(2, tripId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createAssignmentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void loadAssignmentsForTrip(Trip trip) {
        List<Assignment> assignments = findByTripId(trip.getTripId());
        for (Assignment assignment : assignments) {
            // Carica la guida se necessario e non già caricata
            if (assignment.getGuide() == null && guideDAO != null) {
                Guide guide = guideDAO.findById(assignment.getGuideId());
                assignment.setGuide(guide);
            }

            // Imposta il riferimento al viaggio
            assignment.setTrip(trip);

            // Aggiungi l'assegnazione al registro del viaggio
            trip.getAssignmentRegister().addAssignment(assignment);
        }
    }

    @Override
    public List<Assignment> loadAssignmentsForGuide(Guide guide) {
        List<Assignment> assignments = findByGuideId(guide.getGuideId());
        for (Assignment assignment : assignments) {
            // Imposta il riferimento alla guida
            assignment.setGuide(guide);

            // Carica il viaggio se necessario e non già caricato
            if (assignment.getTrip() == null && tripDAO != null) {
                Trip trip = tripDAO.findById(assignment.getTripId());
                assignment.setTrip(trip);
            }
        }
        return assignments;
    }

    // Metodo helper per creare un'istanza di Assignment dal ResultSet
    private Assignment createAssignmentFromResultSet(ResultSet rs) throws SQLException {
        int assignmentId = rs.getInt("assignment_id");
        int guideId = rs.getInt("guide_id");
        int tripId = rs.getInt("trip_id");
        LocalDate date = rs.getDate("assignment_date").toLocalDate();

        // Crea l'oggetto Assignment con gli ID
        Assignment assignment = new Assignment(assignmentId, guideId, tripId, date);

        // Se i DAO sono disponibili, carica anche gli oggetti correlati
        if (guideDAO != null) {
            Guide guide = guideDAO.findById(guideId);
            assignment.setGuide(guide);
        }

        if (tripDAO != null) {
            Trip trip = tripDAO.findById(tripId);
            assignment.setTrip(trip);
        }

        return assignment;
    }
}
