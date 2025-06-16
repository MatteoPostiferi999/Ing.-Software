package core.model;

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

    public void addNotification(Notification notification) {
        notifications.add(notification);
        numUnread++; // tolgo?
    }

    public void removeNotification(Notification notification) {
        if (notifications.remove(notification)) {
            numUnread--; // qui o nel service?
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
