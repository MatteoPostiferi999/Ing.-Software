package model.assignment;

import model.user.Guide;
import java.util.ArrayList;
import java.util.List;

public class AssignmentRegister {
    private List<Assignment> assignments = new ArrayList<>();
    private int maxGuides;

    // Constructor for new register
    public AssignmentRegister(int maxGuides) {
        this.maxGuides = maxGuides;
    }

    // Constructor for reconstruction from DB
    public AssignmentRegister(List<Assignment> assignments, int maxGuides) {
        this.assignments = assignments;
        this.maxGuides = maxGuides;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public int getMaxGuides() {
        return maxGuides;
    }

    public void setMaxGuides(int maxGuides) {
        this.maxGuides = maxGuides;
    }

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
    }

    public void removeAssignment(Assignment assignment) {
        assignments.remove(assignment);
    }

    public List<Assignment> getAllAssignments() {
        return new ArrayList<>(assignments);
    }

    public boolean canAddMoreGuides() {
        return assignments.size() < maxGuides;
    }

    /**
     * Verifica se una guida è assegnata a questo registro
     * @param guide La guida da verificare
     * @return true se la guida è assegnata, false altrimenti
     */
    public boolean hasGuide(Guide guide) {
        if (guide == null) return false;

        for (Assignment assignment : assignments) {
            if (assignment.getGuide().getGuideId() == guide.getGuideId()) {
                return true;
            }
        }

        return false;
    }
}
