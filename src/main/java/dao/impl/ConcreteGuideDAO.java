package dao.impl;

import dao.interfaces.GuideDAO;
import dao.interfaces.UserDAO;
import dao.interfaces.ReviewDAO;
import dao.interfaces.NotificationDAO;
import db.DBManager;
import model.user.Guide;
import model.user.Skill;
import model.user.User;
import model.review.Review;
import model.review.ReviewRegister;
import model.notification.Notification;
import model.notification.NotificationRegister;
import model.trip.Trip;

import java.sql.*;
import java.util.*;

public class ConcreteGuideDAO implements GuideDAO {
    private final DBManager dbManager = DBManager.getInstance();
    private UserDAO userDAO;
    private ReviewDAO reviewDAO;
    private NotificationDAO notificationDAO;

    public ConcreteGuideDAO() { }

    public ConcreteGuideDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public ConcreteGuideDAO(UserDAO userDAO, ReviewDAO reviewDAO, NotificationDAO notificationDAO) {
        this.userDAO = userDAO;
        this.reviewDAO = reviewDAO;
        this.notificationDAO = notificationDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setReviewDAO(ReviewDAO reviewDAO) {
        this.reviewDAO = reviewDAO;
    }

    public void setNotificationDAO(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    @Override
    public Guide getById(int id) {
        return findById(id);
    }

    @Override
    public Guide findById(int id) {
        String sql = "SELECT * FROM guides WHERE guide_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createGuideFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Guide findByUserId(int userId) {
        String sql = "SELECT * FROM guides WHERE user_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createGuideFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Guide> getAll() {
        List<Guide> list = new ArrayList<>();
        String sql = "SELECT * FROM guides";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(createGuideFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void save(Guide guide) {
        if (guide.getGuideId() == 0) {
            insertGuide(guide);
        } else {
            update(guide);
        }
    }

    @Override
    public void update(Guide guide) {
        String sql = "UPDATE guides SET user_id = ? WHERE guide_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, guide.getOwner().getUserId());
            stmt.setInt(2, guide.getGuideId());
            stmt.executeUpdate();

            // aggiorna relazione skills
            updateGuideSkills(guide);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String relSql   = "DELETE FROM guide_skills WHERE guide_id = ?";
        String guideSql = "DELETE FROM guides WHERE guide_id = ?";
        try (Connection conn = dbManager.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(relSql)) {
                st.setInt(1, id);
                st.executeUpdate();
            }
            try (PreparedStatement st = conn.prepareStatement(guideSql)) {
                st.setInt(1, id);
                st.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Guide guide) {
        delete(guide.getGuideId());
    }

    @Override
    public List<Trip> getAssignedTrips(int guideId) {
        List<Trip> assigned = new ArrayList<>();
        String sql = """
                SELECT t.* 
                FROM trips t 
                JOIN assignments a ON t.trip_id = a.trip_id 
                WHERE a.guide_id = ?
                """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, guideId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int tid = rs.getInt("trip_id");
                    // delega a TripDAO
                    Trip trip = getTripById(tid);
                    if (trip != null) assigned.add(trip);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assigned;
    }

    private Trip getTripById(int tripId) {
        try {
            dao.interfaces.TripDAO tripDAO = new dao.impl.ConcreteTripDAO();
            return tripDAO.findById(tripId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void insertGuide(Guide guide) {
        String sql = "INSERT INTO guides (user_id) VALUES (?) RETURNING guide_id";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, guide.getOwner().getUserId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    guide.setGuideId(rs.getInt(1));
                    saveGuideSkills(guide);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveGuideSkills(Guide guide) {
        if (guide.getSkillIds() == null || guide.getSkillIds().isEmpty()) return;
        String sql = "INSERT INTO guide_skills (guide_id, skill_id) VALUES (?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Integer sid : guide.getSkillIds()) {
                stmt.setInt(1, guide.getGuideId());
                stmt.setInt(2, sid);
                stmt.addBatch();
            }
            stmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateGuideSkills(Guide guide) {
        String delSql = "DELETE FROM guide_skills WHERE guide_id = ?";
        try (Connection conn = dbManager.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(delSql)) {
                st.setInt(1, guide.getGuideId());
                st.executeUpdate();
            }
            saveGuideSkills(guide);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Guide createGuideFromResultSet(ResultSet rs) throws SQLException {
        int gid    = rs.getInt("guide_id");
        int uid    = rs.getInt("user_id");
        User owner = null;

        if (userDAO != null) {
            owner = userDAO.findById(uid);
        }

        // carica skills
        List<Skill> skills = loadGuideSkills(gid);
        List<Integer> sids = new ArrayList<>();
        for (Skill sk : skills) sids.add(sk.getSkillId());

        // carica registri
        ReviewRegister reviewReg         = loadReviewRegister(gid);
        NotificationRegister notifReg    = loadNotificationRegister(gid);

        return new Guide(gid, sids, skills, owner, reviewReg, notifReg);
    }

    private List<Skill> loadGuideSkills(int guideId) {
        List<Skill> list = new ArrayList<>();
        String sql = """
                SELECT s.* 
                FROM skills s 
                JOIN guide_skills gs ON s.skill_id = gs.skill_id 
                WHERE gs.guide_id = ?
                """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, guideId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Skill(
                            rs.getInt("skill_id"),
                            rs.getString("name"),
                            rs.getString("description")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private ReviewRegister loadReviewRegister(int guideId) {
        if (reviewDAO != null) {
            Guide proxy = new Guide(null);
            proxy.setGuideId(guideId);
            List<Review> reviews = reviewDAO.getByTarget(proxy);
            return new ReviewRegister(reviews);
        }
        return new ReviewRegister();
    }

    private NotificationRegister loadNotificationRegister(int guideId) {
        if (notificationDAO != null) {
            Guide proxy = new Guide(null);
            proxy.setGuideId(guideId);
            List<Notification> notifs = notificationDAO.getByGuide(proxy);
            int unread = 0;
            for (Notification n : notifs) if (!n.isRead()) unread++;
            return new NotificationRegister(notifs, unread);
        }
        return new NotificationRegister();
    }
}