package business.service;

import java.util.List;

import dao.interfaces.NotificationDAO;
import model.notification.Notification;
import model.notification.Notifiable;

public class NotificationService {
    private final NotificationDAO notificationDAO;

    public NotificationService(NotificationDAO dao) {
        this.notificationDAO = dao;
    }

    // Invia una notifica a un destinatario (Guide o Traveler)
    public void sendNotification(Notification notification, Notifiable receiver) {
        receiver.receiveNotification(notification);         // in-app
        notificationDAO.save(notification);                 // persistente
    }

    // Marca una notifica come letta
    public void readNotification(Notification notification) {
        notification.setRead(true);                         // modifica in-app
        notificationDAO.update(notification);               // aggiorna nel DB
    }

        /**
     * Restituisce tutte le notifiche ricevute da un utente (Guide o Traveler),
     * senza modificarne lo stato (letto/non letto).
     */
    public List<Notification> getAllNotifications(Notifiable target) {
        return target.getNotificationRegister().getNotifications();

    
    }

        /**
     * Segna una singola notifica come letta, aggiornando sia lo stato
     * in memoria che nel database.
     */
    public void markNotificationAsRead(Notification notification) {
        notification.setRead(true);
        notificationDAO.update(notification);
    }

        /**
     * Segna come lette tutte le notifiche ricevute da un utente,
     * aggiornando anche il database.
     */
    public void markAllAsRead(Notifiable target) {
        List<Notification> list = target.getNotificationRegister().getNotifications();
        for (Notification n : list) {
            if (!n.isRead()) {
                n.setRead(true);
                notificationDAO.update(n);
            }
        }
    }


}
