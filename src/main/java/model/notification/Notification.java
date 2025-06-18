package model.notification;

public class Notification {
    private int id;
    private String text;
    private Notifiable recipient;
    private Boolean read;

    // ID per la persistenza
    private int recipientId;   // ID dell'entità destinataria
    private String recipientType;  // Tipo dell'entità (ad es. "GUIDE", "TRAVELER")

    public Notification(String text, Notifiable recipient) {
        this.id = 0; // Default ID, will be assigned by the database
        this.text = text;
        this.read = false; 
        this.recipient = recipient;

        // Se possibile, estrai gli ID dell'entità destinataria
        if (recipient instanceof model.user.Guide) {
            this.recipientId = ((model.user.Guide) recipient).getGuideId();
            this.recipientType = "GUIDE";
        } else if (recipient instanceof model.user.Traveler) {
            this.recipientId = ((model.user.Traveler) recipient).getTravelerId();
            this.recipientType = "TRAVELER";
        }
    }

    public Notification(int id, String text, Notifiable recipient, Boolean read) {
        this.id = id;
        this.text = text;
        this.recipient = recipient;
        this.read = read;

        // Se possibile, estrai gli ID dell'entità destinataria
        if (recipient instanceof model.user.Guide) {
            this.recipientId = ((model.user.Guide) recipient).getGuideId();
            this.recipientType = "GUIDE";
        } else if (recipient instanceof model.user.Traveler) {
            this.recipientId = ((model.user.Traveler) recipient).getTravelerId();
            this.recipientType = "TRAVELER";
        }
    }

    // Costruttore per il caricamento dal database
    public Notification(int id, String text, int recipientId, String recipientType, Boolean read) {
        this.id = id;
        this.text = text;
        this.recipientId = recipientId;
        this.recipientType = recipientType;
        this.read = read;
        this.recipient = null; // Sarà caricato successivamente dal DAO
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

        // Se possibile, estrai gli ID dell'entità destinataria
        if (recipient instanceof model.user.Guide) {
            this.recipientId = ((model.user.Guide) recipient).getGuideId();
            this.recipientType = "GUIDE";
        } else if (recipient instanceof model.user.Traveler) {
            this.recipientId = ((model.user.Traveler) recipient).getTravelerId();
            this.recipientType = "TRAVELER";
        }
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
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
