package core.model;

import java.time.LocalDateTime;

public class Notification {
    private Notifiable recipient;
    private String message;
    private LocalDateTime timestamp;
    private boolean read;

    public Notification(Notifiable recipient, String message) {
        this.recipient = recipient;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }

    // Getter e Setter

    public Notifiable getRecipient() {
        return recipient;
    }

    public void setRecipient(Notifiable recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void markAsRead() {
        this.read = true;
    }
}
