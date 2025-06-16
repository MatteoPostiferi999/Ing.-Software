package business.service;

import dao.interfaces.AssignmentDAO;
import core.model.Assignment;
import core.model.Trip;

public class AssignmentService {
    private final AssignmentDAO assignmentDAO;

    public AssignmentService(AssignmentDAO assignmentDAO) {
        this.assignmentDAO = assignmentDAO;
    }

    public void assignGuide(Assignment assignment) {
        Trip trip = assignment.getTrip();

        // Aggiunta all'interno dell'app (in-memory)
        trip.getAssignmentRegister().addAssignment(assignment);

        // Persistenza su database
        assignmentDAO.save(assignment);
    }
}
