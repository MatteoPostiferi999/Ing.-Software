package dao.interfaces;

import model.assignment.Assignment;
import model.trip.Trip;
import model.user.Guide;

import java.util.List;

public interface AssignmentDAO {
    void save(Assignment assignment);
    void update(Assignment assignment);
    void delete(Assignment assignment);

    Assignment findById(int assignmentId);
    List<Assignment> findAll();

    List<Assignment> findByGuideId(int guideId);
    List<Assignment> findByTripId(int tripId);

    Assignment findByGuideAndTrip(int guideId, int tripId);

    /**
     * Carica le assegnazioni per un viaggio e le aggiunge al registro delle assegnazioni del viaggio
     * @param trip Il viaggio di cui caricare le assegnazioni
     */
    void loadAssignmentsForTrip(Trip trip);

    /**
     * Carica le assegnazioni di una guida
     * @param guide La guida di cui caricare le assegnazioni
     * @return Lista delle assegnazioni della guida
     */
    List<Assignment> loadAssignmentsForGuide(Guide guide);
}
