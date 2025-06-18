package model.user;

public class Skill {
    private int skillId;
    private String name;
    private String description;

    // Costanti per gli skill predefiniti (per mantenere compatibilità con il codice precedente)
    public static final String TREKKING = "TREKKING";
    public static final String CLIMBING = "CLIMBING";
    public static final String HISTORY_EXPERT = "HISTORY_EXPERT";
    public static final String LANGUAGE_ENGLISH = "LANGUAGE_ENGLISH";
    public static final String LANGUAGE_SPANISH = "LANGUAGE_SPANISH";
    public static final String FIRST_AID = "FIRST_AID";

    // Costruttore per nuova skill
    public Skill(String name, String description) {
        this.skillId = 0; // Sarà impostato dal database
        this.name = name;
        this.description = description;
    }

    // Costruttore per skill esistente
    public Skill(int skillId, String name, String description) {
        this.skillId = skillId;
        this.name = name;
        this.description = description;
    }

    // Getter e Setter
    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Skill skill = (Skill) obj;
        return skillId == skill.skillId || name.equals(skill.name);
    }

    @Override
    public int hashCode() {
        return skillId != 0 ? skillId : name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
