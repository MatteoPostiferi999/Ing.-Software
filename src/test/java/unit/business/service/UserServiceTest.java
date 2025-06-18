package unit.business.service;

import business.service.UserService;
import dao.interfaces.GuideDAO;
import dao.interfaces.TravelerDAO;
import dao.interfaces.UserDAO;
import model.user.Guide;
import model.user.Traveler;
import model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private GuideDAO guideDAO;

    @Mock
    private TravelerDAO travelerDAO;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userDAO, guideDAO, travelerDAO);
    }

    @Test
    public void testRegister_Success() {
        // Arrange
        User user = new User("Mario Rossi", "mario@example.com", "password123");
        when(userDAO.findByEmail("mario@example.com")).thenReturn(null);

        // Act
        boolean result = userService.register(user);

        // Assert
        assertTrue(result);
        verify(userDAO).save(user);
    }

    @Test
    public void testRegister_EmailExists() {
        // Arrange
        User user = new User("Mario Rossi", "mario@example.com", "password123");
        when(userDAO.findByEmail("mario@example.com")).thenReturn(new User("Existing User", "mario@example.com", "otherpassword"));

        // Act
        boolean result = userService.register(user);

        // Assert
        assertFalse(result);
        verify(userDAO, never()).save(any());
    }

    @Test
    public void testLogin_Success() {
        // Arrange
        User expectedUser = new User("Mario Rossi", "mario@example.com", "password123");
        when(userDAO.findByEmailAndPassword("mario@example.com", "password123")).thenReturn(expectedUser);

        // Act
        User result = userService.login("mario@example.com", "password123");

        // Assert
        assertEquals(expectedUser, result);
    }

    @Test
    public void testLogin_Failure() {
        // Arrange
        when(userDAO.findByEmailAndPassword("mario@example.com", "wrongpassword")).thenReturn(null);

        // Act
        User result = userService.login("mario@example.com", "wrongpassword");

        // Assert
        assertNull(result);
    }

    @Test
    public void testGetUserById() {
        // Arrange
        User expectedUser = new User("Mario Rossi", "mario@example.com", "password123");
        when(userDAO.findById(1)).thenReturn(expectedUser);

        // Act
        User result = userService.getUserById(1);

        // Assert
        assertEquals(expectedUser, result);
    }

    @Test
    public void testGetUserByEmail() {
        // Arrange
        User expectedUser = new User("Mario Rossi", "mario@example.com", "password123");
        when(userDAO.findByEmail("mario@example.com")).thenReturn(expectedUser);

        // Act
        User result = userService.getUserByEmail("mario@example.com");

        // Assert
        assertEquals(expectedUser, result);
    }

    @Test
    public void testUpdateUser() {
        // Arrange
        User user = new User("Mario Rossi", "mario@example.com", "password123");

        // Act
        userService.updateUser(user);

        // Assert
        verify(userDAO).update(user);
    }

    @Test
    public void testHasGuideProfile_WhenProfileExists() {
        // Arrange
        User user = new User("Mario Rossi", "mario@example.com", "password123");
        Guide guide = new Guide(user);
        user.setGuideProfile(guide);

        // Act
        boolean result = userService.hasGuideProfile(user);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testHasGuideProfile_WhenProfileDoesNotExist() {
        // Arrange
        User user = new User("Mario Rossi", "mario@example.com", "password123");
        user.setGuideProfile(null);
        when(guideDAO.findByUserId(user.getUserId())).thenReturn(null);

        // Act
        boolean result = userService.hasGuideProfile(user);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testHasGuideProfile_WhenProfileLoadedFromDB() {
        // Arrange
        User user = new User("Mario Rossi", "mario@example.com", "password123");
        user.setUserId(1);
        user.setGuideProfile(null);
        Guide guide = new Guide(user);
        when(guideDAO.findByUserId(1)).thenReturn(guide);

        // Act
        boolean result = userService.hasGuideProfile(user);

        // Assert
        assertTrue(result);
        assertEquals(guide, user.getGuideProfile());
    }

    @Test
    public void testHasTravelerProfile_WhenProfileExists() {
        // Arrange
        User user = new User("Mario Rossi", "mario@example.com", "password123");
        Traveler traveler = new Traveler(user);
        user.setTravelerProfile(traveler);

        // Act
        boolean result = userService.hasTravelerProfile(user);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testHasTravelerProfile_WhenProfileDoesNotExist() {
        // Arrange
        User user = new User("Mario Rossi", "mario@example.com", "password123");
        user.setTravelerProfile(null);
        when(travelerDAO.findByUserId(user.getUserId())).thenReturn(null);

        // Act
        boolean result = userService.hasTravelerProfile(user);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testGetGuideProfile_WhenProfileExists() {
        // Arrange
        User user = new User("Mario Rossi", "mario@example.com", "password123");
        Guide guide = new Guide(user);
        user.setGuideProfile(guide);
        when(guideDAO.findByUserId(user.getUserId())).thenReturn(guide);

        // Act
        Guide result = userService.getGuideProfile(user);

        // Assert
        assertEquals(guide, result);
    }

    @Test
    public void testGetGuideProfile_WhenProfileDoesNotExist() {
        // Arrange
        User user = new User("Mario Rossi", "mario@example.com", "password123");
        user.setGuideProfile(null);
        when(guideDAO.findByUserId(user.getUserId())).thenReturn(null);

        // Act
        Guide result = userService.getGuideProfile(user);

        // Assert
        assertNull(result);
    }

    @Test
    public void testDeleteUser() {
        // Arrange
        User user = new User("Mario Rossi", "mario@example.com", "password123");

        // Act
        userService.deleteUser(user);

        // Assert
        verify(userDAO).delete(user);
    }

    @Test
    public void testDeleteUser_Null() {
        // Act
        userService.deleteUser(null);

        // Assert
        verify(userDAO, never()).delete(any());
    }
}
