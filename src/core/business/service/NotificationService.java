package core.business.service;

import core.model.Notification;
import core.model.NotificationRegister;
import core.model.Notifiable;

public class NotificationService {

    private NotificationRegister notificationRegister;

    public NotificationService(NotificationRegister notificationRegister) {
        this.notificationRegister = notificationRegister;
    }

    public void sendNotification(String text, Notifiable target) {
        Notification notification = new Notification(text, target);
        target.receiveNotification(notification);               // invio al destinatario
        notificationRegister.addNotification(notification);      // salvataggio globale
    }

    public void readNotification(Notification notification) {
        notification.setRead(true);
    }
}
