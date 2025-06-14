package core.model;

import java.util.ArrayList;
import java.util.List;

public class Traveler implements Notifiable {
    private int idTraveller;
    private List<Notification> notifications = new ArrayList<>();

    // Constructor
    public Traveler(int idTraveller) {
        this.idTraveller = idTraveller;
    }

    // Getters and Setters
    public int getIdTraveller() {
        return idTraveller;
    }

    public void setIdTraveller(int idTraveller) {
        this.idTraveller = idTraveller;
    }

    // Notifiable implementation
    @Override
    public void receiveNotification(Notification notification) {
        if (notification != null) {
            notifications.add(notification);
        }
    }

    public List<Notification> getNotifications() {
        return notifications;
    }
}
