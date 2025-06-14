package core.model;

public class Skills {
    private String type;
    private String level;
    
    public Skills() {}
    
    public Skills(String type, String level) {
        this.type = type;
        this.level = level;
    }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Skills skill = (Skills) obj;
        return type.equals(skill.type);
    }
    
    @Override
    public int hashCode() {
        return type.hashCode();
    }
    
    @Override
    public String toString() {
        return type + " (" + level + ")";
    }
}