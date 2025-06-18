package model.notification;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationRegister rappresenta una collezione di notifiche ricevute da un utente.
 * Relazione di aggregazione: le notifiche sono create esternamente.
 */
public class NotificationRegister {
    private List<Notification> notifications = new ArrayList<>();
    private List<Integer> notificationIds = new ArrayList<>(); // Lista degli ID per la persistenza
    private int numUnread = 0;

    public NotificationRegister() {
    }

    // Constructor for reconstruction from database
    public NotificationRegister(List<Notification> notifications, int numUnread) {
        this.notifications = notifications;
        this.numUnread = numUnread;

        // Estrai gli ID dalle notifiche
        this.notificationIds = new ArrayList<>();
        if (notifications != null) {
            for (Notification notification : notifications) {
                if (notification.getId() > 0) {
                    this.notificationIds.add(notification.getId());
                }
            }
        }
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);

        // Aggiungi anche l'ID alla lista se disponibile
        if (notification.getId() > 0) {
            notificationIds.add(notification.getId());
        }

        if (!notification.isRead()) {
            numUnread++;
        }
    }

    public void removeNotification(Notification notification) {
        notifications.remove(notification);

        // Rimuovi anche l'ID dalla lista
        if (notification.getId() > 0) {
            notificationIds.remove(Integer.valueOf(notification.getId()));
        }

        if (!notification.isRead()) {
            numUnread--;
        }
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;

        // Aggiorna anche la lista degli ID
        this.notificationIds.clear();
        if (notifications != null) {
            for (Notification notification : notifications) {
                if (notification.getId() > 0) {
                    this.notificationIds.add(notification.getId());
                }
            }
        }

        // Ricalcola il numero di notifiche non lette
        this.numUnread = 0;
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                this.numUnread++;
            }
        }
    }

    public List<Integer> getNotificationIds() {
        return notificationIds;
    }

    public void setNotificationIds(List<Integer> notificationIds) {
        this.notificationIds = notificationIds;
    }

    public void addNotificationId(int notificationId) {
        if (!this.notificationIds.contains(notificationId)) {
            this.notificationIds.add(notificationId);
        }
    }

    public int getNumUnread() {
        return numUnread;
    }

    public void setNumUnread(int numUnread) {
        this.numUnread = numUnread;
    }

    /**
     * Ricalcola il numero di notifiche non lette
     */
    public void recalculateUnreadCount() {
        this.numUnread = 0;
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                this.numUnread++;
            }
        }
    }

    public void markAllAsRead() {
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notification.setRead(true);
            }
        }
        numUnread = 0;
    }

    /**
     * Ottiene una notifica specifica tramite il suo ID
     * @param notificationId ID della notifica da cercare
     * @return La notifica trovata o null se non esiste
     */
    public Notification getNotificationById(int notificationId) {
        for (Notification notification : notifications) {
            if (notification.getId() == notificationId) {
                return notification;
            }
        }
        return null;
    }

    /**
     * Ottiene tutte le notifiche non lette
     * @return Lista delle notifiche non lette
     */
    public List<Notification> getUnreadNotifications() {
        List<Notification> unread = new ArrayList<>();
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                unread.add(notification);
            }
        }
        return unread;
    }
}
