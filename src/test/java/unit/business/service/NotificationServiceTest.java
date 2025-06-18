package unit.business.service;

import business.service.NotificationService;
import model.notification.Notification;
import model.notification.NotificationRegister;
import model.notification.Notifiable;
import model.user.Guide;
import model.user.Traveler;
import dao.interfaces.NotificationDAO;
import dao.interfaces.GuideDAO;
import dao.interfaces.TravelerDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationDAO notificationDAO;

    @Mock
    private GuideDAO guideDAO;

    @Mock
    private TravelerDAO travelerDAO;

    @Mock
    private Notifiable recipient;

    @Mock
    private NotificationRegister notificationRegister;

    @Mock
    private Guide guide;

    @Mock
    private Traveler traveler;

    private NotificationService notificationService;
    private List<Notification> mockNotifications;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationService = new NotificationService(notificationDAO, guideDAO, travelerDAO);

        // Setup mock notifications
        mockNotifications = new ArrayList<>();
        when(recipient.getNotificationRegister()).thenReturn(notificationRegister);
        when(notificationRegister.getNotifications()).thenReturn(mockNotifications);
    }

    @Test
    @DisplayName("Costruttore vuoto dovrebbe creare istanza")
    void testDefaultConstructor() {
        NotificationService service = new NotificationService();
        assertNotNull(service);
    }

    @Test
    @DisplayName("Costruttore con solo NotificationDAO dovrebbe funzionare")
    void testConstructorWithNotificationDAO() {
        NotificationService service = new NotificationService(notificationDAO);
        assertNotNull(service);
    }

    @Test
    @DisplayName("Setter per GuideDAO dovrebbe funzionare")
    void testSetGuideDAO() {
        NotificationService service = new NotificationService();
        service.setGuideDAO(guideDAO);
        // Test indiretto attraverso altre operazioni che usano guideDAO
        assertNotNull(service);
    }

    @Test
    @DisplayName("Setter per TravelerDAO dovrebbe funzionare")
    void testSetTravelerDAO() {
        NotificationService service = new NotificationService();
        service.setTravelerDAO(travelerDAO);
        // Test indiretto attraverso altre operazioni che usano travelerDAO
        assertNotNull(service);
    }

    @Test
    @DisplayName("sendNotification dovrebbe creare e salvare una notifica")
    void testSendNotification() {
        String message = "Test message";

        notificationService.sendNotification(recipient, message);

        verify(recipient).getNotificationRegister();
        verify(notificationRegister).addNotification(any(Notification.class));
        verify(notificationDAO).save(any(Notification.class));
    }

    @Test
    @DisplayName("sendNotification senza DAO non dovrebbe lanciare eccezioni")
    void testSendNotificationWithoutDAO() {
        NotificationService serviceWithoutDAO = new NotificationService();
        String message = "Test message";

        assertDoesNotThrow(() -> serviceWithoutDAO.sendNotification(recipient, message));
        verify(recipient).getNotificationRegister();
        verify(notificationRegister).addNotification(any(Notification.class));
    }

    @Test
    @DisplayName("getUnreadNotifications dovrebbe restituire solo notifiche non lette")
    void testGetUnreadNotifications() {
        // Setup
        Notification readNotification = mock(Notification.class);
        Notification unreadNotification1 = mock(Notification.class);
        Notification unreadNotification2 = mock(Notification.class);

        when(readNotification.isRead()).thenReturn(true);
        when(unreadNotification1.isRead()).thenReturn(false);
        when(unreadNotification2.isRead()).thenReturn(false);

        mockNotifications.add(readNotification);
        mockNotifications.add(unreadNotification1);
        mockNotifications.add(unreadNotification2);

        // Test
        List<Notification> result = notificationService.getUnreadNotifications(recipient);

        // Verify
        assertEquals(2, result.size());
        assertTrue(result.contains(unreadNotification1));
        assertTrue(result.contains(unreadNotification2));
        assertFalse(result.contains(readNotification));

        // Verify that unread notifications were marked as read
        verify(unreadNotification1).markAsRead();
        verify(unreadNotification2).markAsRead();
        verify(notificationDAO, times(2)).update(any(Notification.class));
    }

    @Test
    @DisplayName("getUnreadNotifications con registro vuoto dovrebbe caricare dal database")
    void testGetUnreadNotificationsLoadsFromDatabase() {
        when(notificationRegister.getNotifications()).thenReturn(new ArrayList<>());

        notificationService.getUnreadNotifications(recipient);

        verify(notificationDAO).loadNotificationsForRecipient(recipient);
    }

    @Test
    @DisplayName("getNextUnreadNotification dovrebbe restituire la prima notifica non letta")
    void testGetNextUnreadNotification() {
        // Setup
        Notification readNotification = mock(Notification.class);
        Notification unreadNotification1 = mock(Notification.class);
        Notification unreadNotification2 = mock(Notification.class);

        when(readNotification.isRead()).thenReturn(true);
        when(unreadNotification1.isRead()).thenReturn(false);
        when(unreadNotification2.isRead()).thenReturn(false);

        mockNotifications.add(readNotification);
        mockNotifications.add(unreadNotification1);
        mockNotifications.add(unreadNotification2);

        // Test
        Notification result = notificationService.getNextUnreadNotification(recipient);

        // Verify
        assertEquals(unreadNotification1, result);
        verify(unreadNotification1).markAsRead();
        verify(notificationDAO).update(unreadNotification1);
    }

    @Test
    @DisplayName("getNextUnreadNotification senza notifiche non lette dovrebbe restituire null")
    void testGetNextUnreadNotificationWhenNoneUnread() {
        Notification readNotification = mock(Notification.class);
        when(readNotification.isRead()).thenReturn(true);
        mockNotifications.add(readNotification);

        Notification result = notificationService.getNextUnreadNotification(recipient);

        assertNull(result);
        verify(notificationDAO, never()).update(any(Notification.class));
    }

    @Test
    @DisplayName("markAsRead dovrebbe marcare la notifica e aggiornare il database")
    void testMarkAsRead() {
        Notification notification = mock(Notification.class);

        notificationService.markAsRead(notification);

        verify(notification).markAsRead();
        verify(notificationDAO).update(notification);
    }

    @Test
    @DisplayName("markAllAsRead per Guide dovrebbe funzionare correttamente")
    void testMarkAllAsReadForGuide() {
        when(guide.getGuideId()).thenReturn(123);
        when(guide.getNotificationRegister()).thenReturn(notificationRegister);

        notificationService.markAllAsRead(guide);

        verify(notificationRegister).markAllAsRead();
        verify(notificationDAO).markAllAsRead(123, "GUIDE");
    }

    @Test
    @DisplayName("markAllAsRead per Traveler dovrebbe funzionare correttamente")
    void testMarkAllAsReadForTraveler() {
        when(traveler.getTravelerId()).thenReturn(456);
        when(traveler.getNotificationRegister()).thenReturn(notificationRegister);

        notificationService.markAllAsRead(traveler);

        verify(notificationRegister).markAllAsRead();
        verify(notificationDAO).markAllAsRead(456, "TRAVELER");
    }

    @Test
    @DisplayName("markAllAsRead per tipo sconosciuto non dovrebbe chiamare il DAO")
    void testMarkAllAsReadForUnknownType() {
        notificationService.markAllAsRead(recipient);

        verify(notificationRegister).markAllAsRead();
        verify(notificationDAO, never()).markAllAsRead(anyInt(), anyString());
    }

    @Test
    @DisplayName("deleteNotification dovrebbe rimuovere dal registro e dal database")
    void testDeleteNotification() {
        Notification notification = mock(Notification.class);
        when(notification.getRecipient()).thenReturn(recipient);

        notificationService.deleteNotification(notification);

        verify(notificationRegister).removeNotification(notification);
        verify(notificationDAO).delete(notification);
    }

    @Test
    @DisplayName("deleteNotification con recipient null non dovrebbe lanciare eccezioni")
    void testDeleteNotificationWithNullRecipient() {
        Notification notification = mock(Notification.class);
        when(notification.getRecipient()).thenReturn(null);

        assertDoesNotThrow(() -> notificationService.deleteNotification(notification));
        verify(notificationDAO).delete(notification);
    }

    @Test
    @DisplayName("loadNotificationsForRecipient dovrebbe chiamare il DAO")
    void testLoadNotificationsForRecipient() {
        notificationService.loadNotificationsForRecipient(recipient);

        verify(notificationDAO).loadNotificationsForRecipient(recipient);
    }

    @Test
    @DisplayName("loadNotificationsForRecipient senza DAO non dovrebbe lanciare eccezioni")
    void testLoadNotificationsForRecipientWithoutDAO() {
        NotificationService serviceWithoutDAO = new NotificationService();

        assertDoesNotThrow(() -> serviceWithoutDAO.loadNotificationsForRecipient(recipient));
    }

    @Test
    @DisplayName("getNotificationById dovrebbe restituire la notifica dal DAO")
    void testGetNotificationById() {
        int notificationId = 123;
        Notification expectedNotification = mock(Notification.class);
        when(notificationDAO.findById(notificationId)).thenReturn(expectedNotification);

        Notification result = notificationService.getNotificationById(notificationId);

        assertEquals(expectedNotification, result);
        verify(notificationDAO).findById(notificationId);
    }

    @Test
    @DisplayName("getNotificationById senza DAO dovrebbe restituire null")
    void testGetNotificationByIdWithoutDAO() {
        NotificationService serviceWithoutDAO = new NotificationService();

        Notification result = serviceWithoutDAO.getNotificationById(123);

        assertNull(result);
    }

    @Test
    @DisplayName("getNotificationsByRecipient dovrebbe restituire tutte le notifiche")
    void testGetNotificationsByRecipient() {
        Notification notification1 = mock(Notification.class);
        Notification notification2 = mock(Notification.class);
        mockNotifications.add(notification1);
        mockNotifications.add(notification2);

        List<Notification> result = notificationService.getNotificationsByRecipient(recipient);

        assertEquals(2, result.size());
        assertTrue(result.contains(notification1));
        assertTrue(result.contains(notification2));
    }

    @Test
    @DisplayName("markAsReadById dovrebbe marcare la notifica esistente come letta")
    void testMarkAsReadByIdSuccess() {
        int notificationId = 123;
        Notification notification = mock(Notification.class);
        when(notificationDAO.findById(notificationId)).thenReturn(notification);

        boolean result = notificationService.markAsReadById(notificationId);

        assertTrue(result);
        verify(notification).markAsRead();
        verify(notificationDAO).update(notification);
    }

    @Test
    @DisplayName("markAsReadById con ID inesistente dovrebbe restituire false")
    void testMarkAsReadByIdNotFound() {
        int notificationId = 123;
        when(notificationDAO.findById(notificationId)).thenReturn(null);

        boolean result = notificationService.markAsReadById(notificationId);

        assertFalse(result);
        verify(notificationDAO, never()).update(any(Notification.class));
    }

    @Test
    @DisplayName("markAsReadById senza DAO dovrebbe restituire false")
    void testMarkAsReadByIdWithoutDAO() {
        NotificationService serviceWithoutDAO = new NotificationService();

        boolean result = serviceWithoutDAO.markAsReadById(123);

        assertFalse(result);
    }
}