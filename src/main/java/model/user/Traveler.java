package model.user;

import model.notification.Notifiable;
import model.notification.Notification;
import model.notification.NotificationRegister;

public class Traveler implements Notifiable {
    private int travelerId;
    private NotificationRegister notifications;
    private User owner;

    // Costruttore per oggetti recuperati dal database
    public Traveler(int travelerId, User owner, NotificationRegister notifications) {
        this.travelerId = travelerId;
        this.owner = owner;
        this.notifications = notifications;
    }

    // Costruttore per la creazione iniziale (senza ID, ID verrà assegnato dal DB)
    public Traveler(User owner) {
        this.owner = owner;
        this.travelerId = 0; // Valore predefinito, verrà assegnato dal DB
        this.notifications = new NotificationRegister();
    }

    // Getters and Setters
    public int getTravelerId() {
        return travelerId;
    }

    public void setTravelerId(int travelerId) {
        this.travelerId = travelerId;
    }

    public void setNotificationRegister(NotificationRegister notificationRegister) {
        this.notifications = notificationRegister;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

     public String getUserName() {
    return owner.getUserName();
    }

    public String getEmail() {
        return owner.getEmail();
    }

    public String getPassword() {
        return owner.getPassword();
    }

    // Notifiable interface implementation
    @Override
    public void receiveNotification(Notification notification) {
        notifications.addNotification(notification);
    }

    @Override
    public NotificationRegister getNotificationRegister() {
        return notifications;
    }
}


