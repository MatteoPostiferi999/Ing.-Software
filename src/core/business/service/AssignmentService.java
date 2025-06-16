package core.business.service;

import core.model.Assignment;
import core.model.Trip;

public class AssignmentService {

    public void assignGuide(Assignment assignment) {
        Trip trip = assignment.getTrip();
        trip.getAssignmentRegister().addAssignment(assignment);
    }
}
