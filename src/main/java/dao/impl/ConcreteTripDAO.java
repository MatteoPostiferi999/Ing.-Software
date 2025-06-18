package dao.impl;

import dao.interfaces.TripDAO;
import dao.interfaces.ActivityDAO;
import dao.interfaces.BookingDAO;
import dao.interfaces.AssignmentDAO;
import dao.interfaces.ApplicationDAO;
import db.DBManager;
import model.trip.Trip;
import model.trip.Activity;
import model.user.Skill;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConcreteTripDAO implements TripDAO {
    private final DBManager dbManager = DBManager.getInstance();
    private ActivityDAO activityDAO;
    private BookingDAO bookingDAO;
    private AssignmentDAO assignmentDAO;
    private ApplicationDAO applicationDAO;

    public ConcreteTripDAO() {
        // I DAO correlati possono essere iniettati qui o tramite un metodo di inizializzazione
    }

    public void setActivityDAO(ActivityDAO activityDAO) {
        this.activityDAO = activityDAO;
    }

    public void setBookingDAO(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }

    public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
        this.assignmentDAO = assignmentDAO;
    }

    public void setApplicationDAO(ApplicationDAO applicationDAO) {
        this.applicationDAO = applicationDAO;
    }

    @Override
    public void save(Trip trip) {
        String sql = "INSERT INTO trip (title, description, price, date, min_trav, max_trav, max_guides) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, trip.getTitle());
            stmt.setString(2, trip.getDescription());
            stmt.setDouble(3, trip.getPrice());
            stmt.setDate(4, Date.valueOf(trip.getDate()));
            stmt.setInt(5, trip.getBookingRegister().getMinTrav());
            stmt.setInt(6, trip.getBookingRegister().getMaxTrav());
            stmt.setInt(7, trip.getAssignmentRegister().getMaxGuides());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    trip.setTripId(rs.getInt(1));
                }
            }

            // Salvare le attività del viaggio
            if (activityDAO != null && trip.getPlannedActivities() != null) {
                for (Activity activity : trip.getPlannedActivities()) {
                    activity.setTripId(trip.getTripId());
                    activityDAO.save(activity);
                }
            }

            // Salvare le skills richieste
            saveRequiredSkills(trip);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo per salvare le skills richieste
    private void saveRequiredSkills(Trip trip) {
        String sql = "INSERT INTO trip_required_skills (trip_id, skill_id) VALUES (?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Skill skill : trip.getRequiredSkills()) {
                stmt.setInt(1, trip.getTripId());
                stmt.setInt(2, skill.getSkillId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Trip trip) {
        String sql = "UPDATE trip SET title = ?, description = ?, price = ?, date = ?, min_trav = ?, max_trav = ?, max_guides = ? WHERE trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trip.getTitle());
            stmt.setString(2, trip.getDescription());
            stmt.setDouble(3, trip.getPrice());
            stmt.setDate(4, Date.valueOf(trip.getDate()));
            stmt.setInt(5, trip.getBookingRegister().getMinTrav());
            stmt.setInt(6, trip.getBookingRegister().getMaxTrav());
            stmt.setInt(7, trip.getAssignmentRegister().getMaxGuides());
            stmt.setInt(8, trip.getTripId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Trip findById(int id) {
        String sql = "SELECT * FROM trip WHERE trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Trip trip = new Trip(
                        rs.getInt("trip_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getDate("date").toLocalDate(),
                        rs.getInt("min_trav"),
                        rs.getInt("max_trav"),
                        rs.getInt("max_guides")
                    );

                    // Caricare le skills richieste
                    loadRequiredSkills(trip);

                    // Caricare le attività pianificate
                    if (activityDAO != null) {
                        List<Activity> activities = activityDAO.findByTripId(trip.getTripId());
                        for (Activity activity : activities) {
                            trip.getPlannedActivities().add(activity);
                        }
                    }

                    // Caricare i bookings se necessario
                    if (bookingDAO != null) {
                        bookingDAO.loadBookingsForTrip(trip);
                    }

                    // Caricare le guide assegnate se necessario
                    if (assignmentDAO != null) {
                        assignmentDAO.loadAssignmentsForTrip(trip);
                    }

                    // Caricare le applicazioni se necessario
                    if (applicationDAO != null) {
                        applicationDAO.loadApplicationsForTrip(trip);
                    }

                    return trip;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadRequiredSkills(Trip trip) {
        String sql = "SELECT s.* FROM skill s JOIN trip_required_skills trs ON s.skill_id = trs.skill_id WHERE trs.trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trip.getTripId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Skill skill = new Skill(
                        rs.getInt("skill_id"),
                        rs.getString("name"),
                        rs.getString("description")
                    );
                    trip.getRequiredSkills().add(skill);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Trip> findAll() {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT * FROM trip";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Trip trip = new Trip(
                    rs.getInt("trip_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getDate("date").toLocalDate(),
                    rs.getInt("min_trav"),
                    rs.getInt("max_trav"),
                    rs.getInt("max_guides")
                );

                // Caricare le skills richieste
                loadRequiredSkills(trip);

                trips.add(trip);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trips;
    }

    // Metodo per caricare completamente un viaggio con tutte le sue relazioni
    public Trip loadCompleteTrip(int id) {
        Trip trip = findById(id);
        if (trip != null) {
            // Qui puoi aggiungere eventuali altre operazioni di caricamento avanzate
            // come le recensioni, che potrebbero non essere state caricate nel metodo findById
        }
        return trip;
    }
}