package core.model;

import java.util.ArrayList;
import java.util.List;

public class AssignmentRegister {
    private List<Assignment> assignments = new ArrayList<>();
    private int maxGuides;

    public AssignmentRegister(int maxGuides) {
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
}
