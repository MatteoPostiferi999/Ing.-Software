package dao.interfaces;

import model.notification.Notification;
import model.notification.Notifiable;
import model.user.Guide;
import model.user.Traveler;
import java.util.List;

public interface NotificationDAO {
    void save(Notification notification);
    void update(Notification notification);  // per marcare come letta
    void delete(Notification notification);  // per eliminare una notifica

    Notification findById(int id);  // trova una notifica specifica per ID

    List<Notification> getByUserId(String userId); // per caricarle da DB

    // Metodi per caricare notifiche per entità specifiche
    List<Notification> getByRecipientId(int recipientId, String recipientType);
    List<Notification> getByGuide(Guide guide);
    List<Notification> getByTraveler(Traveler traveler);
    List<Notification> getUnreadByRecipient(int recipientId, String recipientType);

    /**
     * Carica le notifiche per un destinatario e le aggiunge al suo registro
     * @param recipient Il destinatario delle notifiche
     */
    void loadNotificationsForRecipient(Notifiable recipient);

    /**
     * Marca una notifica come letta
     * @param notificationId L'ID della notifica da marcare come letta
     */
    void markAsRead(int notificationId);

    /**
     * Marca tutte le notifiche di un destinatario come lette
     * @param recipientId L'ID del destinatario
     * @param recipientType Il tipo di destinatario (GUIDE, TRAVELER)
     */
    void markAllAsRead(int recipientId, String recipientType);
}
