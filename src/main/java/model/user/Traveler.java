package model.user;

import model.notification.Notifiable;
import model.notification.Notification;
import model.notification.NotificationRegister;

public class Traveler implements Notifiable {
    private int travelerId;
    private NotificationRegister notifications;
    private User owner;

    // Constructor
    public Traveler(int travelerId, User owner) {
        this.travelerId = travelerId;
        this.notifications = new NotificationRegister();
        this.owner = owner;

    }

    public Traveler(User owner) {
        this.owner = owner;
        this.travelerId = 0; // Default value, can be set later
        this.notifications = new NotificationRegister();
    }

    // Getters and Setters
    public int getTravelerId() {
        return travelerId;
    }

    public void setTravelerId(int travelerId) {
        this.travelerId = travelerId;
    }

    public NotificationRegister getNotificationRegister() {
        return notifications;
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
}
