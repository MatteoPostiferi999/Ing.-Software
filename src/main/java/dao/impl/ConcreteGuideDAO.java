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

    // Costruttore predefinito
    public ConcreteGuideDAO() {
        // Default constructor
    }

    // Costruttore con dipendenze
    public ConcreteGuideDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // Costruttore completo
    public ConcreteGuideDAO(UserDAO userDAO, ReviewDAO reviewDAO, NotificationDAO notificationDAO) {
        this.userDAO = userDAO;
        this.reviewDAO = reviewDAO;
        this.notificationDAO = notificationDAO;
    }

    // Setter per le dipendenze
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
        String sql = "SELECT * FROM guide WHERE guide_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return createGuideFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Guide findByUserId(int userId) {
        String sql = "SELECT * FROM guide WHERE user_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return createGuideFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Guide> getAll() {
        List<Guide> guides = new ArrayList<>();
        String sql = "SELECT * FROM guide";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                guides.add(createGuideFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guides;
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
        String sql = "UPDATE guide SET user_id = ? WHERE guide_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guide.getOwner().getUserId());
            stmt.setInt(2, guide.getGuideId());
            stmt.executeUpdate();

            // Aggiornamento delle skill
            updateGuideSkills(guide);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        // Prima elimina le relazioni con le skill
        String skillSql = "DELETE FROM guide_skill WHERE guide_id = ?";
        String guideSql = "DELETE FROM guide WHERE guide_id = ?";

        try (Connection conn = dbManager.getConnection()) {
            // Elimina le relazioni con le skill
            try (PreparedStatement stmt = conn.prepareStatement(skillSql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            // Elimina la guida
            try (PreparedStatement stmt = conn.prepareStatement(guideSql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
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
        List<Trip> assignedTrips = new ArrayList<>();
        String sql = "SELECT t.* FROM trip t " +
                     "JOIN assignment a ON t.trip_id = a.trip_id " +
                     "WHERE a.guide_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, guideId);
            ResultSet rs = stmt.executeQuery();

            // Assume che ci sia una classe TripDAO con un metodo createTripFromResultSet
            // Se questa non esiste, potrebbe essere necessario creare questo metodo qui
            while (rs.next()) {
                // Usa il TripDAO per creare oggetti Trip o implementa la logica qui
                int tripId = rs.getInt("trip_id");
                // Ottieni i dettagli del viaggio dal TripDAO
                // Per semplicità, assumiamo che ci sia un metodo in qualche altra classe
                Trip trip = getTripById(tripId);
                if (trip != null) {
                    assignedTrips.add(trip);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assignedTrips;
    }

    // Metodo di supporto per ottenere un Trip dal TripDAO
    private Trip getTripById(int tripId) {
        // Questo è un metodo di supporto che dovrebbe utilizzare TripDAO
        // O puoi implementare qui la logica per caricare un Trip dal database
        try {
            // Assumo che ci sia un'istanza di TripDAO accessibile
            // Se non è disponibile, dovrai iniettare TripDAO in questa classe
            dao.interfaces.TripDAO tripDAO = new dao.impl.ConcreteTripDAO();
            return tripDAO.findById(tripId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Metodi di supporto

    private void insertGuide(Guide guide) {
        String sql = "INSERT INTO guide (user_id) VALUES (?) RETURNING guide_id";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, guide.getOwner().getUserId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                guide.setGuideId(rs.getInt(1));

                // Salva le relazioni con le skill
                saveGuideSkills(guide);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveGuideSkills(Guide guide) {
        if (guide.getSkillIds() == null || guide.getSkillIds().isEmpty()) {
            return; // Nessuna skill da salvare
        }

        String sql = "INSERT INTO guide_skill (guide_id, skill_id) VALUES (?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Integer skillId : guide.getSkillIds()) {
                stmt.setInt(1, guide.getGuideId());
                stmt.setInt(2, skillId);
                stmt.addBatch();
            }

            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateGuideSkills(Guide guide) {
        // Prima elimina tutte le skill esistenti
        String deleteSql = "DELETE FROM guide_skill WHERE guide_id = ?";

        try (Connection conn = dbManager.getConnection()) {
            // Elimina le skill esistenti
            try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                stmt.setInt(1, guide.getGuideId());
                stmt.executeUpdate();
            }

            // Inserisce le nuove skill
            saveGuideSkills(guide);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Guide createGuideFromResultSet(ResultSet rs) throws SQLException {
        int guideId = rs.getInt("guide_id");
        int userId = rs.getInt("user_id");

        // Carica l'utente associato
        User owner = userDAO.findById(userId);

        // Carica le skill della guida
        List<Skill> skills = loadGuideSkills(guideId);
        List<Integer> skillIds = new ArrayList<>();
        for (Skill skill : skills) {
            skillIds.add(skill.getSkillId());
        }

        // Carica i registri delle recensioni e delle notifiche
        ReviewRegister reviewRegister = loadReviewRegister(guideId);
        NotificationRegister notificationRegister = loadNotificationRegister(guideId);

        // Crea l'oggetto Guide utilizzando il costruttore appropriato
        return new Guide(guideId, skillIds, skills, owner, reviewRegister, notificationRegister);
    }

    private List<Skill> loadGuideSkills(int guideId) {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT s.* FROM skill s JOIN guide_skill gs ON s.skill_id = gs.skill_id WHERE gs.guide_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guideId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int skillId = rs.getInt("skill_id");
                String name = rs.getString("name");
                String description = rs.getString("description");

                Skill skill = new Skill(skillId, name, description);
                skills.add(skill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return skills;
    }

    private ReviewRegister loadReviewRegister(int guideId) {
        if (reviewDAO != null) {
            // Creiamo una guida temporanea solo per la query (pattern Proxy)
            Guide tempGuide = new Guide(null);
            tempGuide.setGuideId(guideId);

            // Utilizza il reviewDAO per caricare le recensioni
            try {
                List<Review> reviews = reviewDAO.getByTarget(tempGuide);
                return new ReviewRegister(reviews);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Fallback: ritorna un registro vuoto
        return new ReviewRegister();
    }

    private NotificationRegister loadNotificationRegister(int guideId) {
        if (notificationDAO != null) {
            // Creiamo una guida temporanea solo per la query (pattern Proxy)
            Guide tempGuide = new Guide(null);
            tempGuide.setGuideId(guideId);

            // Utilizza il metodo getByGuide del notificationDAO invece di getByTarget
            try {
                List<Notification> notifications = notificationDAO.getByGuide(tempGuide);

                // Calcola il numero di notifiche non lette
                int numUnread = 0;
                for (Notification notification : notifications) {
                    if (!notification.isRead()) {
                        numUnread++;
                    }
                }

                return new NotificationRegister(notifications, numUnread);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Fallback: ritorna un registro vuoto
        return new NotificationRegister();
    }
}
