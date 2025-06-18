// ConcreteApplicationDAOTest.java
package unit.dao.impl;

import dao.impl.ConcreteApplicationDAO;
import dao.interfaces.GuideDAO;
import dao.interfaces.TripDAO;
import db.DBManager;
import model.application.Application;
import model.application.ApplicationRegister;
import model.trip.Trip;
import model.user.Guide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConcreteApplicationDAOTest {

    @Mock GuideDAO guideDAO;
    @Mock TripDAO tripDAO;

    ConcreteApplicationDAO dao;

    @Mock Trip trip;
    @Mock Guide guide;
    @Mock Application app1, app2;
    @Mock ApplicationRegister register;

    @BeforeEach
    void setUp() {
        dao = spy(new ConcreteApplicationDAO()); // NON usa @InjectMocks così evita il DBManager
        dao.setGuideDAO(guideDAO);
        dao.setTripDAO(tripDAO);
    }

    @Test
    void loadApplicationsForTrip_shouldPopulateRegisterAndGuide() {
        when(trip.getTripId()).thenReturn(7);
        when(dao.getByTripId(7)).thenReturn(Arrays.asList(app1, app2));
        when(app1.getGuide()).thenReturn(null);
        when(app2.getGuide()).thenReturn(guide);
        when(guideDAO.findById(anyInt())).thenReturn(guide);
        when(trip.getApplicationRegister()).thenReturn(register);

        dao.loadApplicationsForTrip(trip);

        verify(app1).setGuide(guide);
        verify(app1).setTrip(trip);
        verify(register).addApplication(app1);
        verify(register).addApplication(app2);
    }

    @Test
    void hasGuideAppliedForTrip_shouldReturnTrueWhenFound() {
        Application mockApp = mock(Application.class);
        ConcreteApplicationDAO spyDao = spy(new ConcreteApplicationDAO());
        doReturn(mockApp).when(spyDao).findByGuideAndTrip(3, 5);
        assertTrue(spyDao.hasGuideAppliedForTrip(3, 5));
    }

    @Test
    void hasGuideAppliedForTrip_shouldReturnFalseWhenNotFound() {
        ConcreteApplicationDAO spyDao = spy(new ConcreteApplicationDAO());
        doReturn(null).when(spyDao).findByGuideAndTrip(3, 5);
        assertFalse(spyDao.hasGuideAppliedForTrip(3, 5));
    }
}
