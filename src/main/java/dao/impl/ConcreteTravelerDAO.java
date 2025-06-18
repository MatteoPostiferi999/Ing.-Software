package dao.impl;

import dao.interfaces.TravelerDAO;
import dao.interfaces.UserDAO;
import dao.interfaces.NotificationDAO;
import db.DBManager;
import model.notification.NotificationRegister;
import model.user.Traveler;
import model.user.User;

import java.sql.*;
import java.util.*;

public class ConcreteTravelerDAO implements TravelerDAO {
    private final DBManager dbManager = DBManager.getInstance();
    private UserDAO userDAO;
    private NotificationDAO notificationDAO;

    // Costruttore di default
    public ConcreteTravelerDAO() {
        // Default constructor
    }

    // Costruttore con dependency injection
    public ConcreteTravelerDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // Costruttore completo con tutte le dipendenze
    public ConcreteTravelerDAO(UserDAO userDAO, NotificationDAO notificationDAO) {
        this.userDAO = userDAO;
        this.notificationDAO = notificationDAO;
    }

    // Setter per dependency injection
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setNotificationDAO(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    @Override
    public Traveler getById(int id) {
        return findById(id);
    }

    @Override
    public Traveler findById(int id) {
        String sql = "SELECT * FROM traveler WHERE traveler_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return createTravelerFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Traveler findByUserId(int userId) {
        String sql = "SELECT * FROM traveler WHERE user_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return createTravelerFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Traveler> getAll() {
        List<Traveler> travelers = new ArrayList<>();
        String sql = "SELECT * FROM traveler";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                travelers.add(createTravelerFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return travelers;
    }

    @Override
    public void save(Traveler traveler) {
        if (traveler.getTravelerId() == 0) {
            // Nuovo viaggiatore
            insertTraveler(traveler);
        } else {
            // Aggiornamento
            update(traveler);
        }
    }

    private void insertTraveler(Traveler traveler) {
        String sql = "INSERT INTO traveler (user_id) VALUES (?) RETURNING traveler_id";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, traveler.getOwner().getUserId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                traveler.setTravelerId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Traveler traveler) {
        String sql = "UPDATE traveler SET user_id = ? WHERE traveler_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, traveler.getOwner().getUserId());
            stmt.setInt(2, traveler.getTravelerId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM traveler WHERE traveler_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Traveler traveler) {
        delete(traveler.getTravelerId());
    }

    // Metodo helper per creare un oggetto Traveler da un ResultSet
    private Traveler createTravelerFromResultSet(ResultSet rs) throws SQLException {
        int travelerId = rs.getInt("traveler_id");
        int userId = rs.getInt("user_id");

        // Carica l'utente associato
        User owner = null;
        if (userDAO != null) {
            owner = userDAO.findById(userId);
        }

        // Crea un registro notifiche
        NotificationRegister notificationRegister = new NotificationRegister();

        // Carica le notifiche se il DAO è disponibile
        Traveler traveler = new Traveler(travelerId, owner, notificationRegister);

        if (notificationDAO != null) {
            notificationDAO.loadNotificationsForRecipient(traveler);
        }

        return traveler;
    }
}
