package dao.impl;

import dao.interfaces.NotificationDAO;
import dao.interfaces.GuideDAO;
import dao.interfaces.TravelerDAO;
import db.DBManager;
import model.notification.Notification;
import model.notification.Notifiable;
import model.notification.NotificationRegister;
import model.user.Guide;
import model.user.Traveler;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConcreteNotificationDAO implements NotificationDAO {

    private final DBManager dbManager = DBManager.getInstance();
    private GuideDAO guideDAO;
    private TravelerDAO travelerDAO;

    // Costruttore predefinito
    public ConcreteNotificationDAO() {
        // Default constructor
    }

    // Costruttore con dependency injection
    public ConcreteNotificationDAO(GuideDAO guideDAO, TravelerDAO travelerDAO) {
        this.guideDAO = guideDAO;
        this.travelerDAO = travelerDAO;
    }

    // Setter per dependency injection
    public void setGuideDAO(GuideDAO guideDAO) {
        this.guideDAO = guideDAO;
    }

    public void setTravelerDAO(TravelerDAO travelerDAO) {
        this.travelerDAO = travelerDAO;
    }

    @Override
    public void save(Notification notification) {
        if (notification.getId() == 0) {
            insertNotification(notification);
        } else {
            update(notification);
        }
    }

    private void insertNotification(Notification notification) {
        String sql = "INSERT INTO notification (message, is_read, recipient_id, recipient_type) " +
                     "VALUES (?, ?, ?, ?) RETURNING notification_id";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, notification.getText());
            stmt.setBoolean(2, notification.isRead());
            stmt.setInt(3, notification.getRecipientId());
            stmt.setString(4, notification.getRecipientType());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                notification.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Notification notification) {
        String sql = "UPDATE notification SET message = ?, is_read = ?, " +
                     "recipient_id = ?, recipient_type = ? WHERE notification_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, notification.getText());
            stmt.setBoolean(2, notification.isRead());
            stmt.setInt(3, notification.getRecipientId());
            stmt.setString(4, notification.getRecipientType());
            stmt.setInt(5, notification.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Notification notification) {
        String sql = "DELETE FROM notification WHERE notification_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, notification.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Notification findById(int id) {
        String sql = "SELECT * FROM notification WHERE notification_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return createNotificationFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Notification> getByUserId(String userId) {
        // In questo caso, userId è una stringa e dobbiamo convertirla in int
        try {
            int recipientId = Integer.parseInt(userId);
            // Assumiamo che userID possa riferirsi sia a una guida che a un viaggiatore
            List<Notification> guideNotifications = getByRecipientId(recipientId, "GUIDE");
            List<Notification> travelerNotifications = getByRecipientId(recipientId, "TRAVELER");

            List<Notification> allNotifications = new ArrayList<>();
            allNotifications.addAll(guideNotifications);
            allNotifications.addAll(travelerNotifications);

            return allNotifications;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Notification> getByRecipientId(int recipientId, String recipientType) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE recipient_id = ? AND recipient_type = ? ORDER BY notification_id DESC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recipientId);
            stmt.setString(2, recipientType);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                notifications.add(createNotificationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notifications;
    }

    @Override
    public List<Notification> getByGuide(Guide guide) {
        return getByRecipientId(guide.getGuideId(), "GUIDE");
    }

    @Override
    public List<Notification> getByTraveler(Traveler traveler) {
        return getByRecipientId(traveler.getTravelerId(), "TRAVELER");
    }

    @Override
    public List<Notification> getUnreadByRecipient(int recipientId, String recipientType) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE recipient_id = ? AND recipient_type = ? AND is_read = false ORDER BY notification_id DESC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recipientId);
            stmt.setString(2, recipientType);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                notifications.add(createNotificationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notifications;
    }

    @Override
    public void loadNotificationsForRecipient(Notifiable recipient) {
        // Determina il tipo e l'ID del destinatario
        int recipientId;
        String recipientType;

        if (recipient instanceof Guide) {
            recipientId = ((Guide) recipient).getGuideId();
            recipientType = "GUIDE";
        } else if (recipient instanceof Traveler) {
            recipientId = ((Traveler) recipient).getTravelerId();
            recipientType = "TRAVELER";
        } else {
            // Tipo di destinatario non supportato
            return;
        }

        // Carica le notifiche
        List<Notification> notifications = getByRecipientId(recipientId, recipientType);

        // Aggiungi le notifiche al registro del destinatario
        NotificationRegister register = recipient.getNotificationRegister();
        if (register != null) {
            for (Notification notification : notifications) {
                register.addNotification(notification);
            }
        }
    }

    @Override
    public void markAsRead(int notificationId) {
        String sql = "UPDATE notification SET is_read = true WHERE notification_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, notificationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void markAllAsRead(int recipientId, String recipientType) {
        String sql = "UPDATE notification SET is_read = true WHERE recipient_id = ? AND recipient_type = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recipientId);
            stmt.setString(2, recipientType);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo helper per creare un oggetto Notification da un ResultSet
    private Notification createNotificationFromResultSet(ResultSet rs) throws SQLException {
        int notificationId = rs.getInt("notification_id");
        String text = rs.getString("message");
        boolean isRead = rs.getBoolean("is_read");
        int recipientId = rs.getInt("recipient_id");
        String recipientType = rs.getString("recipient_type");

        // Crea la notifica senza destinatario (lo imposteremo dopo se necessario)
        Notification notification = new Notification(text, null);
        notification.setId(notificationId);
        notification.setRead(isRead);
        notification.setRecipientId(recipientId);
        notification.setRecipientType(recipientType);

        // Carica il destinatario se richiesto
        if ("GUIDE".equals(recipientType) && guideDAO != null) {
            notification.setRecipient(guideDAO.findById(recipientId));
        } else if ("TRAVELER".equals(recipientType) && travelerDAO != null) {
            notification.setRecipient(travelerDAO.findById(recipientId));
        }

        return notification;
    }
}
