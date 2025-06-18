package dao.impl;

import dao.interfaces.UserDAO;
import dao.interfaces.GuideDAO;
import dao.interfaces.TravelerDAO;
import db.DBManager;
import model.user.User;
import model.user.Guide;
import model.user.Traveler;

import java.sql.*;

public class ConcreteUserDAO implements UserDAO {

    private final DBManager db = DBManager.getInstance();
    private GuideDAO guideDAO;
    private TravelerDAO travelerDAO;

    public ConcreteUserDAO() {
        // Default constructor
    }

    public ConcreteUserDAO(GuideDAO guideDAO, TravelerDAO travelerDAO) {
        this.guideDAO = guideDAO;
        this.travelerDAO = travelerDAO;
    }

    public void setGuideDAO(GuideDAO guideDAO) {
        this.guideDAO = guideDAO;
    }

    public void setTravelerDAO(TravelerDAO travelerDAO) {
        this.travelerDAO = travelerDAO;
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user.setUserId(rs.getInt(1));

                // Ora che l'utente ha un ID, possiamo salvare i profili
                if (guideDAO != null && user.getGuideProfile() != null) {
                    guideDAO.save(user.getGuideProfile());
                }

                if (travelerDAO != null && user.getTravelerProfile() != null) {
                    travelerDAO.save(user.getTravelerProfile());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = createUserFromResultSet(rs);
                loadUserProfiles(user);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User findByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = createUserFromResultSet(rs);
                loadUserProfiles(user);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = createUserFromResultSet(rs);
                loadUserProfiles(user);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, password = ? WHERE user_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setInt(4, user.getUserId());
            stmt.executeUpdate();

            // Aggiorna anche i profili
            if (guideDAO != null && user.getGuideProfile() != null) {
                guideDAO.update(user.getGuideProfile());
            }

            if (travelerDAO != null && user.getTravelerProfile() != null) {
                travelerDAO.update(user.getTravelerProfile());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(User user) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user.getUserId());

            // Prima di eliminare l'utente, elimina i profili correlati
            if (guideDAO != null && user.getGuideProfile() != null) {
                guideDAO.delete(user.getGuideProfile());
            }

            if (travelerDAO != null && user.getTravelerProfile() != null) {
                travelerDAO.delete(user.getTravelerProfile());
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to create a User object from a ResultSet
    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("password"),
            null, // Guide profile, will be loaded later
            null  // Traveler profile, will be loaded later
        );
    }

    // Helper method to load user profiles
    private void loadUserProfiles(User user) {
        if (user != null) {
            if (guideDAO != null) {
                Guide guide = guideDAO.findByUserId(user.getUserId());
                if (guide != null) {
                    // Set user reference to avoid circular references
                    guide.setOwner(user);
                }
                // Even if guide is null, we still need to set it
                user.setGuideProfile(guide);
            }

            if (travelerDAO != null) {
                Traveler traveler = travelerDAO.findByUserId(user.getUserId());
                if (traveler != null) {
                    // Set user reference to avoid circular references
                    traveler.setOwner(user);
                }
                // Even if traveler is null, we still need to set it
                user.setTravelerProfile(traveler);
            }
        }
    }
}
