package unit.business.controller;

import business.controller.UserController;
import business.service.UserService;
import model.user.Guide;
import model.user.Traveler;
import model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);
    }

    @Test
    public void testRegisterUser_Success() {
        // Arrange
        when(userService.register(any(User.class))).thenReturn(true);

        // Act
        boolean result = userController.registerUser("Mario Rossi", "mario@example.com", "password123");

        // Assert
        assertTrue(result);
        assertNotNull(userController.getCurrentUser());
        assertEquals("mario@example.com", userController.getCurrentUser().getEmail());
        verify(userService).register(any(User.class));
    }

    @Test
    public void testRegisterUser_Failure() {
        // Arrange
        when(userService.register(any(User.class))).thenReturn(false);

        // Act
        boolean result = userController.registerUser("Mario Rossi", "mario@example.com", "password123");

        // Assert
        assertFalse(result);
        assertNull(userController.getCurrentUser());
        verify(userService).register(any(User.class));
    }

    @Test
    public void testLogin_Success() {
        // Arrange
        User mockUser = new User("Mario Rossi", "mario@example.com", "password123");
        when(userService.login("mario@example.com", "password123")).thenReturn(mockUser);

        // Act
        boolean result = userController.login("mario@example.com", "password123");

        // Assert
        assertTrue(result);
        assertNotNull(userController.getCurrentUser());
        assertEquals(mockUser, userController.getCurrentUser());
        verify(userService).login("mario@example.com", "password123");
    }

    @Test
    public void testLogin_Failure() {
        // Arrange
        when(userService.login("mario@example.com", "password123")).thenReturn(null);

        // Act
        boolean result = userController.login("mario@example.com", "password123");

        // Assert
        assertFalse(result);
        assertNull(userController.getCurrentUser());
        verify(userService).login("mario@example.com", "password123");
    }

    @Test
    public void testGetTravelerProfile_WhenNoCurrentUser() {
        // Act
        Traveler travelerProfile = userController.getTravelerProfile();

        // Assert
        assertNull(travelerProfile);
    }

    @Test
    public void testGetTravelerProfile_CreateIfNotExists() {
        // Arrange
        User mockUser = new User("Mario Rossi", "mario@example.com", "password123");
        mockUser.setTravelerProfile(null); // Forza il profilo a null per testare la creazione
        userController.login("mario@example.com", "password123"); // Imposta il current user
        when(userService.login("mario@example.com", "password123")).thenReturn(mockUser);

        // Act
        Traveler travelerProfile = userController.getTravelerProfile();

        // Assert
        assertNotNull(travelerProfile);
        assertEquals(mockUser, travelerProfile.getOwner());
        verify(userService).updateUser(mockUser);
    }

    @Test
    public void testGetGuideProfile_WhenNoCurrentUser() {
        // Act
        Guide guideProfile = userController.getGuideProfile();

        // Assert
        assertNull(guideProfile);
    }

    @Test
    public void testGetGuideProfile_CreateIfNotExists() {
        // Arrange
        User mockUser = new User("Mario Rossi", "mario@example.com", "password123");
        mockUser.setGuideProfile(null); // Forza il profilo a null per testare la creazione
        userController.login("mario@example.com", "password123"); // Imposta il current user
        when(userService.login("mario@example.com", "password123")).thenReturn(mockUser);

        // Act
        Guide guideProfile = userController.getGuideProfile();

        // Assert
        assertNotNull(guideProfile);
        assertEquals(mockUser, guideProfile.getOwner());
        verify(userService).updateUser(mockUser);
    }

    @Test
    public void testHasGuideProfile_WhenNoCurrentUser() {
        // Act
        boolean result = userController.hasGuideProfile();

        // Assert
        assertFalse(result);
    }

    @Test
    public void testHasGuideProfile_WhenCurrentUserExists() {
        // Arrange
        User mockUser = mock(User.class);
        userController.login("mario@example.com", "password123"); // Imposta il current user
        when(userService.login("mario@example.com", "password123")).thenReturn(mockUser);
        when(userService.hasGuideProfile(mockUser)).thenReturn(true);

        // Act
        boolean result = userController.hasGuideProfile();

        // Assert
        assertTrue(result);
        verify(userService).hasGuideProfile(mockUser);
    }

    @Test
    public void testHasTravelerProfile_WhenNoCurrentUser() {
        // Act
        boolean result = userController.hasTravelerProfile();

        // Assert
        assertFalse(result);
    }

    @Test
    public void testHasTravelerProfile_WhenCurrentUserExists() {
        // Arrange
        User mockUser = mock(User.class);
        userController.login("mario@example.com", "password123"); // Imposta il current user
        when(userService.login("mario@example.com", "password123")).thenReturn(mockUser);
        when(userService.hasTravelerProfile(mockUser)).thenReturn(true);

        // Act
        boolean result = userController.hasTravelerProfile();

        // Assert
        assertTrue(result);
        verify(userService).hasTravelerProfile(mockUser);
    }

    @Test
    public void testLogout() {
        // Arrange
        User mockUser = new User("Mario Rossi", "mario@example.com", "password123");
        userController.login("mario@example.com", "password123"); // Imposta il current user
        when(userService.login("mario@example.com", "password123")).thenReturn(mockUser);
        assertNotNull(userController.getCurrentUser()); // Verifica che l'utente sia loggato

        // Act
        userController.logout();

        // Assert
        assertNull(userController.getCurrentUser());
    }
}
