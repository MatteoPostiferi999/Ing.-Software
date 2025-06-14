// Activity.java
package core.model;

/**
 * Rappresenta un'attività pianificata all'interno di un viaggio.
 */
public class Activity {
    private int duration;        // in minuti o ore, a tua scelta
    private String description;
    private String name;

    // Costruttore
    public Activity(int duration, String description, String name) {
        this.duration = duration;
        this.description = description;
        this.name = name;
    }

    // Getter e Setter

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
