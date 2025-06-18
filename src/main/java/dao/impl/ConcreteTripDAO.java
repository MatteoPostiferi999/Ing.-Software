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

            // Aggiornare le attività del viaggio
            if (activityDAO != null) {
                // Prima elimina tutte le attività esistenti
                deleteActivities(trip.getTripId());

                // Poi inserisci le nuove attività
                for (Activity activity : trip.getPlannedActivities()) {
                    activity.setTripId(trip.getTripId());
                    activityDAO.save(activity);
                }
            }

            // Aggiornare le skills richieste
            updateRequiredSkills(trip);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo per eliminare tutte le attività di un viaggio
    private void deleteActivities(int tripId) {
        String sql = "DELETE FROM activity WHERE trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tripId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo per aggiornare le skills richieste
    private void updateRequiredSkills(Trip trip) {
        // Prima elimina tutte le skills esistenti
        String deleteSql = "DELETE FROM trip_required_skills WHERE trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setInt(1, trip.getTripId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Poi inserisci le nuove skills
        saveRequiredSkills(trip);
    }

    @Override
    public Trip findById(int id) {
        String sql = "SELECT * FROM trip WHERE trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return createBasicTripFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Trip findByIdFull(int id) {
        Trip trip = findById(id);
        if (trip != null) {
            loadTripRelations(trip);
        }
        return trip;
    }

    @Override
    public List<Trip> findAll() {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT * FROM trip";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                trips.add(createBasicTripFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trips;
    }

    @Override
    public List<Trip> findAllFull() {
        List<Trip> trips = findAll();
        for (Trip trip : trips) {
            loadTripRelations(trip);
        }
        return trips;
    }

    @Override
    public void deleteById(int id) {
        // Prima elimina le relazioni
        deleteActivities(id);
        deleteRequiredSkills(id);

        // Poi elimina il viaggio
        String sql = "DELETE FROM trip WHERE trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo per eliminare le skills richieste per un viaggio
    private void deleteRequiredSkills(int tripId) {
        String sql = "DELETE FROM trip_required_skills WHERE trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tripId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo per creare un oggetto Trip base dal ResultSet
    private Trip createBasicTripFromResultSet(ResultSet rs) throws SQLException {
        int tripId = rs.getInt("trip_id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        double price = rs.getDouble("price");
        LocalDate date = rs.getDate("date").toLocalDate();
        int minTrav = rs.getInt("min_trav");
        int maxTrav = rs.getInt("max_trav");
        int maxGuides = rs.getInt("max_guides");

        return new Trip(tripId, title, description, price, date, minTrav, maxTrav, maxGuides);
    }

    // Metodo per caricare tutte le relazioni di un viaggio
    private void loadTripRelations(Trip trip) {
        loadRequiredSkills(trip);
        loadActivities(trip);

        // Carica anche le prenotazioni se BookingDAO è disponibile
        if (bookingDAO != null) {
            bookingDAO.loadBookingsForTrip(trip);
        }

        // Carica le assegnazioni se AssignmentDAO è disponibile
        if (assignmentDAO != null) {
            assignmentDAO.loadAssignmentsForTrip(trip);
        }

        // Carica le applicazioni se ApplicationDAO è disponibile
        if (applicationDAO != null) {
            applicationDAO.loadApplicationsForTrip(trip);
        }
    }

    // Metodo per caricare le skills richieste
    private void loadRequiredSkills(Trip trip) {
        String sql = "SELECT s.* FROM skill s JOIN trip_required_skills trs ON s.skill_id = trs.skill_id WHERE trs.trip_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trip.getTripId());
            ResultSet rs = stmt.executeQuery();
            List<Skill> skills = new ArrayList<>();
            List<Integer> skillIds = new ArrayList<>();

            while (rs.next()) {
                int skillId = rs.getInt("skill_id");
                String name = rs.getString("name");
                String description = rs.getString("description");

                Skill skill = new Skill(skillId, name, description);
                skills.add(skill);
                skillIds.add(skillId);
            }

            trip.setRequiredSkills(skills);
            trip.setRequiredSkillIds(skillIds);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo per caricare le attività
    private void loadActivities(Trip trip) {
        if (activityDAO != null) {
            List<Activity> activities = activityDAO.findByTripId(trip.getTripId());
            List<Integer> activityIds = new ArrayList<>();

            for (Activity activity : activities) {
                activityIds.add(activity.getActivityId());
            }

            trip.setPlannedActivities(activities);
            trip.setActivityIds(activityIds);
        }
    }
}