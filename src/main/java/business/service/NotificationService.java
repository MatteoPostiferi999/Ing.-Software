package business.service;

import model.notification.Notification;
import model.notification.Notifiable;
import dao.interfaces.NotificationDAO;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationService {

    private NotificationDAO notificationDAO;

    public NotificationService() {
    }

    public NotificationService(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    public void sendNotification(Notifiable recipient, String message) {
        Notification notification = new Notification (message, recipient);
        recipient.getNotificationRegister().addNotification(notification);
        notificationDAO.save(notification);
    }

    public List<Notification> getUnreadNotifications(Notifiable recipient) {
        return recipient.getNotificationRegister().getNotifications().stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());
    }

    public void markAsRead(Notification notification) {
        notification.markAsRead();
        notificationDAO.update(notification);
    }
}
