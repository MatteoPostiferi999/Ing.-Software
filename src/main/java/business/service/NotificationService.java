package business.service;

import model.notification.Notification;
import model.notification.Notifiable;
import model.user.Guide;
import model.user.Traveler;
import dao.interfaces.NotificationDAO;
import dao.interfaces.GuideDAO;
import dao.interfaces.TravelerDAO;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationService {

    private NotificationDAO notificationDAO;
    private GuideDAO guideDAO;
    private TravelerDAO travelerDAO;

    public NotificationService() {
    }

    public NotificationService(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    public NotificationService(NotificationDAO notificationDAO, GuideDAO guideDAO, TravelerDAO travelerDAO) {
        this.notificationDAO = notificationDAO;
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

    /**
     * Invia una notifica a un destinatario
     * @param recipient destinatario della notifica
     * @param message messaggio della notifica
     */
    public void sendNotification(Notifiable recipient, String message) {
        Notification notification = new Notification(message, recipient);
        recipient.getNotificationRegister().addNotification(notification);

        if (notificationDAO != null) {
            notificationDAO.save(notification);
        }
    }

    /**
     * Ottiene tutte le notifiche non lette di un destinatario e le marca come lette
     * @param recipient destinatario delle notifiche
     * @return lista delle notifiche non lette
     */
    public List<Notification> getUnreadNotifications(Notifiable recipient) {
        // Prima carichiamo le notifiche dal database se necessario
        loadNotificationsIfEmpty(recipient);

        List<Notification> unread = recipient.getNotificationRegister().getNotifications().stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());

        for (Notification n : unread) {
            markAsRead(n);
        }
        return unread;
    }

    /**
     * Ottiene la prossima notifica non letta di un destinatario e la marca come letta
     * @param recipient destinatario della notifica
     * @return la prossima notifica non letta o null se non ce ne sono
     */
    public Notification getNextUnreadNotification(Notifiable recipient) {
        // Prima carichiamo le notifiche dal database se necessario
        loadNotificationsIfEmpty(recipient);

        Notification next = recipient.getNotificationRegister().getNotifications().stream()
                .filter(n -> !n.isRead())
                .findFirst()
                .orElse(null);

        if (next != null) {
            markAsRead(next);
        }
        return next;
    }

    /**
     * Marca una notifica come letta
     * @param notification notifica da marcare come letta
     */
    public void markAsRead(Notification notification) {
        notification.markAsRead();

        if (notificationDAO != null) {
            notificationDAO.update(notification);
        }
    }

    /**
     * Marca tutte le notifiche di un destinatario come lette
     * @param recipient destinatario delle notifiche
     */
    public void markAllAsRead(Notifiable recipient) {
        // Prima carichiamo le notifiche dal database se necessario
        loadNotificationsIfEmpty(recipient);

        // Marca le notifiche come lette localmente
        recipient.getNotificationRegister().markAllAsRead();

        // Aggiorna il database
        if (notificationDAO != null) {
            int recipientId = -1;
            String recipientType = null;

            if (recipient instanceof Guide) {
                recipientId = ((Guide) recipient).getGuideId();
                recipientType = "GUIDE";
            } else if (recipient instanceof Traveler) {
                recipientId = ((Traveler) recipient).getTravelerId();
                recipientType = "TRAVELER";
            }

            if (recipientId > 0 && recipientType != null) {
                notificationDAO.markAllAsRead(recipientId, recipientType);
            }
        }
    }

    /**
     * Elimina una notifica
     * @param notification notifica da eliminare
     */
    public void deleteNotification(Notification notification) {
        // Rimuovi dalla collezione locale
        Notifiable recipient = notification.getRecipient();
        if (recipient != null) {
            recipient.getNotificationRegister().removeNotification(notification);
        }

        // Rimuovi dal database
        if (notificationDAO != null) {
            notificationDAO.delete(notification);
        }
    }

    /**
     * Carica le notifiche per un destinatario dal database
     * @param recipient destinatario delle notifiche
     */
    public void loadNotificationsForRecipient(Notifiable recipient) {
        if (notificationDAO != null) {
            notificationDAO.loadNotificationsForRecipient(recipient);
        }
    }

    /**
     * Carica le notifiche dal database solo se il registro è vuoto
     * @param recipient destinatario delle notifiche
     */
    private void loadNotificationsIfEmpty(Notifiable recipient) {
        if (recipient.getNotificationRegister().getNotifications().isEmpty() && notificationDAO != null) {
            loadNotificationsForRecipient(recipient);
        }
    }

    /**
     * Ottiene una notifica specifica dal database
     * @param notificationId ID della notifica
     * @return la notifica trovata o null se non esiste
     */
    public Notification getNotificationById(int notificationId) {
        if (notificationDAO != null) {
            return notificationDAO.findById(notificationId);
        }
        return null;
    }

    /**
     * Ottiene tutte le notifiche per un destinatario specifico
     * @param recipient destinatario delle notifiche
     * @return lista delle notifiche del destinatario
     */
    public List<Notification> getNotificationsByRecipient(Notifiable recipient) {
        // Prima carichiamo le notifiche dal database se necessario
        loadNotificationsIfEmpty(recipient);

        // Ritorna tutte le notifiche del destinatario
        return recipient.getNotificationRegister().getNotifications();
    }

    /**
     * Marca una notifica come letta utilizzando il suo ID
     * @param notificationId ID della notifica da marcare come letta
     * @return true se la notifica è stata trovata e marcata come letta, false altrimenti
     */
    public boolean markAsReadById(int notificationId) {
        Notification notification = getNotificationById(notificationId);
        if (notification != null) {
            markAsRead(notification);
            return true;
        }
        return false;
    }
}
