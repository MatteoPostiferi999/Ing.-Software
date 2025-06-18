// Activity.java
package model.trip;


public class Activity {
    private int activityId;       // ID univoco nel database
    private int duration;
    private String description;
    private String name;
    private int tripId;           // ID del viaggio a cui appartiene questa attività

    // Costruttore per nuova attività (senza ID)
    public Activity(int duration, String description, String name) {
        this.duration = duration;
        this.description = description;
        this.name = name;
    }

    // Costruttore con tripId (per nuova attività)
    public Activity(int duration, String description, String name, int tripId) {
        this.duration = duration;
        this.description = description;
        this.name = name;
        this.tripId = tripId;
    }

    // Costruttore per attività esistente (con ID)
    public Activity(int activityId, int duration, String description, String name) {
        this.activityId = activityId;
        this.duration = duration;
        this.description = description;
        this.name = name;
    }

    // Costruttore completo con tripId
    public Activity(int activityId, int duration, String description, String name, int tripId) {
        this.activityId = activityId;
        this.duration = duration;
        this.description = description;
        this.name = name;
        this.tripId = tripId;
    }

    // Getter e Setter

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

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

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }
}
