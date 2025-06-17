package business.service;

import model.trip.Trip;
import model.assignment.AssignmentRegister;
import model.assignment.Assignment;
import model.user.Guide;
import model.application.Application;
import model.application.ApplicationRegister;
import dao.interfaces.AssignmentDAO;

import java.util.Comparator;
import java.util.List;

public class AssignmentService {

    private final ApplicationRegister applicationRegister;
    private final AssignmentDAO assignmentDAO;

    public AssignmentService(ApplicationRegister applicationRegister, AssignmentDAO assignmentDAO) {
        this.applicationRegister = applicationRegister;
        this.assignmentDAO = assignmentDAO;
    }

    public void assignBestGuidesToTrip(Trip trip) {
        List<Application> acceptedApplications = applicationRegister.getAcceptedApplicationsForTrip(trip);

        int maxGuides = trip.getMaxGuides();
        int alreadyAssigned = trip.getAssignmentRegister().getAssignments().size();
        int remaining = maxGuides - alreadyAssigned;

        if (remaining <= 0) return;

        acceptedApplications.stream()
            .sorted(Comparator.comparingDouble((Application a) -> a.getGuide().getRating()).reversed())
            .limit(remaining)
            .forEach(app -> {
                Guide guide = app.getGuide();
                Assignment assignment = new Assignment(guide, trip);
                trip.getAssignmentRegister().addAssignment(assignment);
                assignmentDAO.save(assignment);
            });
    }
}
