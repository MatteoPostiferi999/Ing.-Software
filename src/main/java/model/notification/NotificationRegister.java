package model.notification;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationRegister rappresenta una collezione di notifiche ricevute da un utente.
 * Relazione di aggregazione: le notifiche sono create esternamente.
 */
public class NotificationRegister {
    private List<Notification> notifications = new ArrayList<>();
    private int numUnread = 0;

    public NotificationRegister() {
    }

    // Constructor for reconstruction from database
    public NotificationRegister(List<Notification> notifications, int numUnread) {
        this.notifications = notifications;
        this.numUnread = numUnread;
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
        if (!notification.isRead()) {
            numUnread++;
        }
    }

    public void removeNotification(Notification notification) {
        notifications.remove(notification);
        if (!notification.isRead()) {
            numUnread--;
        }
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public int getNumUnread() {
        return numUnread;
    }

    public void setNumUnread(int numUnread) {
        this.numUnread = numUnread;
    }
}
