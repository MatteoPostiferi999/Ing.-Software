package unit.dao.impl;

import dao.impl.ConcreteNotificationDAO;
import dao.interfaces.GuideDAO;
import dao.interfaces.TravelerDAO;
import model.notification.Notification;
import model.notification.NotificationRegister;
import model.notification.Notifiable;
import model.user.Guide;
import model.user.Traveler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConcreteNotificationDAOTest {

    private ConcreteNotificationDAO notificationDAO;
    private GuideDAO guideDAO;
    private TravelerDAO travelerDAO;

    @BeforeEach
    public void setUp() {
        guideDAO = mock(GuideDAO.class);
        travelerDAO = mock(TravelerDAO.class);
        notificationDAO = Mockito.spy(new ConcreteNotificationDAO(guideDAO, travelerDAO));
    }

    @Test
    public void testGetByUserId_validInteger_returnsNotifications() {
        Notification mockNotif = new Notification("Test", null);
        doReturn(List.of(mockNotif)).when(notificationDAO).getByRecipientId(123, "GUIDE");
        doReturn(List.of()).when(notificationDAO).getByRecipientId(123, "TRAVELER");

        List<Notification> result = notificationDAO.getByUserId("123");

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getText());
    }

    @Test
    public void testGetByUserId_invalidFormat_returnsEmptyList() {
        List<Notification> result = notificationDAO.getByUserId("abc");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testLoadNotificationsForRecipient_withGuide() {
        Guide guide = mock(Guide.class);
        NotificationRegister register = mock(NotificationRegister.class);
        when(guide.getGuideId()).thenReturn(1);
        when(guide.getNotificationRegister()).thenReturn(register);

        Notification notification = new Notification("Hello", null);
        doReturn(List.of(notification)).when(notificationDAO).getByRecipientId(1, "GUIDE");

        notificationDAO.loadNotificationsForRecipient(guide);

        verify(register, times(1)).addNotification(notification);
    }

    @Test
    public void testLoadNotificationsForRecipient_withTraveler() {
        Traveler traveler = mock(Traveler.class);
        NotificationRegister register = mock(NotificationRegister.class);
        when(traveler.getTravelerId()).thenReturn(2);
        when(traveler.getNotificationRegister()).thenReturn(register);

        Notification notification = new Notification("Hello", null);
        doReturn(List.of(notification)).when(notificationDAO).getByRecipientId(2, "TRAVELER");

        notificationDAO.loadNotificationsForRecipient(traveler);

        verify(register, times(1)).addNotification(notification);
    }
}
