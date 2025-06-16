package model.notification;

public class Notification {
    private String text;
    private Boolean read;
    private Notifiable recipient;

    public Notification(String text, Notifiable recipient) {
        this.text = text;
        this.read = false; 
        this.recipient = recipient;
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
}
