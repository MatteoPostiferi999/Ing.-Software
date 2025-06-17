package model.notification;

public class Notification {
    private int id;
    private String text;
    private Notifiable recipient;
    private Boolean read;

    public Notification(String text, Notifiable recipient) {
        this.id = 0; // Default ID, will be assigned by the database
        this.text = text;
        this.read = false; 
        this.recipient = recipient;
    }

    public Notification(int id, String text, Notifiable recipient, Boolean read) {
        this.id = id;
        this.text = text;
        this.recipient = recipient;
        this.read = read;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public Boolean isRead() {
        return read;
    }
    public void setRead(Boolean read) {
        this.read = read;
    }

    public Notifiable getRecipient() {
        return recipient;
    }
    public void setRecipient(Notifiable recipient) {
        this.recipient = recipient;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void markAsRead() {
        this.read = true;
    }

}
