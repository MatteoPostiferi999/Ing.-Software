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

    public ConcreteTripDAO() { }

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
        String sql = """
        INSERT INTO trips (
            title, description, price, date,
            min_travelers, max_travelers, max_guides
        ) VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

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

            // salva attività
            if (activityDAO != null && trip.getPlannedActivities() != null) {
                for (Activity a : trip.getPlannedActivities()) {
                    a.setTripId(trip.getTripId());
                    activityDAO.save(a);
                }
            }
            // salva skills richieste
            saveRequiredSkills(trip);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveRequiredSkills(Trip trip) {
        String sql = "INSERT INTO trip_required_skills (trip_id, skill_id) VALUES (?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Skill s : trip.getRequiredSkills()) {
                stmt.setInt(1, trip.getTripId());
                stmt.setInt(2, s.getSkillId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Trip trip) {
        String sql = "UPDATE trips SET title=?, description=?, price=?, date=?, min_travelers=?, max_travelers=?, max_guides=? "
                + "WHERE trip_id=?";
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

            // rimuovi e risalva attività
            if (activityDAO != null) {
                deleteActivities(trip.getTripId());
                for (Activity a : trip.getPlannedActivities()) {
                    a.setTripId(trip.getTripId());
                    activityDAO.save(a);
                }
            }
            // rimuovi e risalva skills
            updateRequiredSkills(trip);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteActivities(int tripId) {
        String sql = "DELETE FROM activities WHERE trip_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tripId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateRequiredSkills(Trip trip) {
        String deleteSql = "DELETE FROM trip_required_skills WHERE trip_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {

            stmt.setInt(1, trip.getTripId());
            stmt.executeUpdate();
            saveRequiredSkills(trip);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Trip findById(int id) {
        String sql = "SELECT * FROM trips WHERE trip_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createBasicTripFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Trip findByIdFull(int id) {
        var t = findById(id);
        if (t != null) loadTripRelations(t);
        return t;
    }

    @Override
    public List<Trip> findAll() {
        List<Trip> list = new ArrayList<>();
        String sql = "SELECT * FROM trips";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(createBasicTripFromResultSet(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Trip> findAllFull() {
        var all = findAll();
        for (Trip t : all) loadTripRelations(t);
        return all;
    }

    @Override
    public void deleteById(int id) {
        deleteActivities(id);
        deleteRequiredSkills(id);
        String sql = "DELETE FROM trips WHERE trip_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteRequiredSkills(int tripId) {
        String sql = "DELETE FROM trip_required_skills WHERE trip_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tripId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Trip createBasicTripFromResultSet(ResultSet rs) throws SQLException {
        int id       = rs.getInt("trip_id");
        String title = rs.getString("title");
        String desc  = rs.getString("description");
        double pr    = rs.getDouble("price");
        LocalDate dt = rs.getDate("date").toLocalDate();
        int minT     = rs.getInt("min_travelers");
        int maxT     = rs.getInt("max_travelers");
        int maxG     = rs.getInt("max_guides");
        return new Trip(id, title, desc, pr, dt, minT, maxT, maxG);
    }

    public void loadTripRelations(Trip trip) {
        loadRequiredSkills(trip);
        loadActivities(trip);
        if (bookingDAO     != null) bookingDAO.loadBookingsForTrip(trip);
        if (assignmentDAO  != null) assignmentDAO.loadAssignmentsForTrip(trip);
        if (applicationDAO != null) applicationDAO.loadApplicationsForTrip(trip);
    }

    private void loadRequiredSkills(Trip trip) {
        String sql = "SELECT s.* FROM skills s " +
                "JOIN trip_required_skills trs ON s.skill_id=trs.skill_id " +
                "WHERE trs.trip_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trip.getTripId());
            try (ResultSet rs = stmt.executeQuery()) {
                List<Skill> skills = new ArrayList<>();
                List<Integer> ids  = new ArrayList<>();
                while (rs.next()) {
                    int sid = rs.getInt("skill_id");
                    skills.add(new Skill(sid, rs.getString("name"), rs.getString("description")));
                    ids.add(sid);
                }
                trip.setRequiredSkills(skills);
                trip.setRequiredSkillIds(ids);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadActivities(Trip trip) {
        if (activityDAO != null) {
            var acts = activityDAO.findByTripId(trip.getTripId());
            trip.setPlannedActivities(acts);
            List<Integer> ids = new ArrayList<>();
            for (Activity a : acts) ids.add(a.getActivityId());
            trip.setActivityIds(ids);
        }
    }
}