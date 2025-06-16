package core.model;

/**
 * Classe astratta che rappresenta una recensione tra un Traveler e un Reviewable (Trip o Guide).
 * Nessuna logica: solo struttura dati.
 */
public abstract class Review {
    private int rating;
    private String text;
    private Traveler author;
    private Reviewable target;

    public Review(int rating, String text, Traveler author, Reviewable target) {
        this.rating = rating;
        this.text = text;
        this.author = author;
        this.target = target;
    }

    // Getter e Setter
    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Traveler getAuthor() {
        return author;
    }

    public void setAuthor(Traveler author) {
        this.author = author;
    }

    public Reviewable getTarget() {
        return target;
    }

    public void setTarget(Reviewable target) {
        this.target = target;
    }
}
