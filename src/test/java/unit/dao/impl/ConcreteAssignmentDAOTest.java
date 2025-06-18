// ConcreteAssignmentDAOTest.java
package unit.dao.impl;

import dao.impl.ConcreteAssignmentDAO;
import dao.interfaces.GuideDAO;
import dao.interfaces.TripDAO;
import model.assignment.Assignment;
import model.assignment.AssignmentRegister;
import model.trip.Trip;
import model.user.Guide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConcreteAssignmentDAOTest {

    @Mock private GuideDAO guideDAO;
    @Mock private TripDAO tripDAO;

    private ConcreteAssignmentDAO dao;

    @Mock private Trip trip;
    @Mock private Guide guide;
    @Mock private Assignment assignment;

    @BeforeEach
    void setUp() {
        // Creiamo uno spy per stubbare i metodi findBy*
        dao = spy(new ConcreteAssignmentDAO(guideDAO, tripDAO));
    }

    @Test
    void loadAssignmentsForTrip_shouldPopulateRegisterAndGuide() {
        // Arrange
        when(trip.getTripId()).thenReturn(42);
        // Stub del metodo DAO.findByTripId
        doReturn(List.of(assignment)).when(dao).findByTripId(42);

        // L'assignment non ha ancora la guide
        when(assignment.getGuide()).thenReturn(null);
        when(assignment.getGuideId()).thenReturn(99);
        // guideDAO fornisce la Guide
        when(guideDAO.findById(99)).thenReturn(guide);

        // Mock del registro delle assegnazioni
        AssignmentRegister register = mock(AssignmentRegister.class);
        when(trip.getAssignmentRegister()).thenReturn(register);

        // Act
        dao.loadAssignmentsForTrip(trip);

        // Assert
        // Deve aver settato la guide e il trip sull'objet Assignment
        verify(assignment).setGuide(guide);
        verify(assignment).setTrip(trip);
        // Deve aver aggiunto l'Assignment al registro
        verify(register).addAssignment(assignment);
    }

    @Test
    void loadAssignmentsForGuide_shouldPopulateTrip() {
        // Arrange
        Guide guideParam = mock(Guide.class);
        when(guideParam.getGuideId()).thenReturn(77);
        // Stub del metodo DAO.findByGuideId
        doReturn(List.of(assignment)).when(dao).findByGuideId(77);

        // L'assignment non ha ancora il trip
        when(assignment.getTrip()).thenReturn(null);
        when(assignment.getTripId()).thenReturn(123);
        // tripDAO fornisce il Trip
        when(tripDAO.findById(123)).thenReturn(trip);

        // Act
        List<Assignment> result = dao.loadAssignmentsForGuide(guideParam);

        // Assert
        assertEquals(1, result.size());
        // Deve aver settato il trip sull'objet Assignment
        verify(assignment).setTrip(trip);
    }
}
