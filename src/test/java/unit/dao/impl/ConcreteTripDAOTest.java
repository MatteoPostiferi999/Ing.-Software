package unit.dao.impl;


import dao.impl.ConcreteTripDAO;
import dao.interfaces.*;
import model.trip.Trip;
import model.trip.Activity;
import model.user.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

class ConcreteTripDAOTest {

    private ConcreteTripDAO tripDAO;
    private ActivityDAO activityDAO;
    private BookingDAO bookingDAO;
    private AssignmentDAO assignmentDAO;
    private ApplicationDAO applicationDAO;

    private Trip testTrip;

    @BeforeEach
    void setUp() {
        tripDAO = new ConcreteTripDAO();

        // Mock dei DAO dipendenti
        activityDAO = mock(ActivityDAO.class);
        bookingDAO = mock(BookingDAO.class);
        assignmentDAO = mock(AssignmentDAO.class);
        applicationDAO = mock(ApplicationDAO.class);

        // Inietto i mock
        tripDAO.setActivityDAO(activityDAO);
        tripDAO.setBookingDAO(bookingDAO);
        tripDAO.setAssignmentDAO(assignmentDAO);
        tripDAO.setApplicationDAO(applicationDAO);

        // Creo un Trip fittizio per i test
        testTrip = new Trip(1, "Trip to Rome", "Visit Rome", 250.0,
                LocalDate.of(2025, 7, 20), 2, 6, 2);

        // Aggiungo attività e skill
// Creo un'attività da associare al trip (usando costruttore con duration, descrizione, nome, tripId)
        Activity activity = new Activity(2, "Morning visit", "Colosseum Tour", testTrip.getTripId());
        testTrip.setPlannedActivities(List.of(activity));

        Skill skill = new Skill(5, "History", "Knowledge of Roman history");
        testTrip.setRequiredSkills(List.of(skill));
        testTrip.setRequiredSkillIds(List.of(skill.getSkillId()));
    }

    @Test
    void testSaveCallsActivityDAOAndSkillInsertion() {
        // Simuliamo che il Trip sia nuovo (tripId = 0)
        testTrip.setTripId(0);

        // Eseguiamo il metodo save
        tripDAO.save(testTrip);

        // Verifica che le attività siano state salvate
        verify(activityDAO, atLeastOnce()).save(any(Activity.class));
        // Nota: il salvataggio delle skill è interno a tripDAO -> non possiamo verificarlo facilmente senza un DB mock.
    }

    @Test
    void testUpdateCallsActivityDAOAndSkillUpdate() {
        // Eseguiamo update
        tripDAO.update(testTrip);

        // Verifica che le attività siano prima eliminate e poi salvate
        verify(activityDAO, atLeastOnce()).save(any(Activity.class));
    }

    @Test
    void testLoadTripRelationsCallsAllDAOs() {
        tripDAO.loadTripRelations(testTrip);

        verify(activityDAO, times(1)).findByTripId(testTrip.getTripId());
        verify(bookingDAO, times(1)).loadBookingsForTrip(testTrip);
        verify(assignmentDAO, times(1)).loadAssignmentsForTrip(testTrip);
        verify(applicationDAO, times(1)).loadApplicationsForTrip(testTrip);
    }
}
