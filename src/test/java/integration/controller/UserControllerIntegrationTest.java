package integration.controller;

import business.controller.UserController;
import business.service.UserService;
import dao.interfaces.GuideDAO;
import dao.interfaces.TravelerDAO;
import dao.interfaces.UserDAO;
import model.user.Guide;
import model.user.Traveler;
import model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerIntegrationTest {

    private UserDAO userDAO;
    private GuideDAO guideDAO;
    private TravelerDAO travelerDAO;
    private UserService userService;
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Creiamo i mock dei DAO
        userDAO = mock(UserDAO.class);
        guideDAO = mock(GuideDAO.class);
        travelerDAO = mock(TravelerDAO.class);

        // Configuriamo il servizio con i DAO mockati
        userService = new UserService(userDAO, guideDAO, travelerDAO);

        // Creiamo il controller con il servizio reale
        userController = new UserController(userService);
    }

    @Test
    public void testCompleteUserRegistrationAndLogin() {
        // Configura mock per permettere la registrazione
        when(userDAO.findByEmail("mario@example.com")).thenReturn(null);
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(1); // Simula l'assegnazione di un ID dal DB
            return null;
        }).when(userDAO).save(any(User.class));

        // Esegui la registrazione
        boolean registrationResult = userController.registerUser("Mario Rossi", "mario@example.com", "password123");
        assertTrue(registrationResult);

        // Verifica che l'utente corrente sia impostato dopo la registrazione
        User currentUser = userController.getCurrentUser();
        assertNotNull(currentUser);
        assertEquals("mario@example.com", currentUser.getEmail());

        // Configura il mock per simulare il logout e il login successivo
        when(userDAO.findByEmailAndPassword("mario@example.com", "password123"))
                .thenReturn(currentUser);

        // Esegui logout
        userController.logout();
        assertNull(userController.getCurrentUser());

        // Esegui login
        boolean loginResult = userController.login("mario@example.com", "password123");
        assertTrue(loginResult);
        assertNotNull(userController.getCurrentUser());
        assertEquals("mario@example.com", userController.getCurrentUser().getEmail());
    }

    @Test
    public void testCompleteProfileManagement() {
        // Configura mock per permettere il login
        User mockUser = new User("Mario Rossi", "mario@example.com", "password123");
        mockUser.setUserId(1);
        mockUser.setGuideProfile(null); // Inizialmente senza profili
        mockUser.setTravelerProfile(null);

        when(userDAO.findByEmailAndPassword("mario@example.com", "password123"))
                .thenReturn(mockUser);

        // Esegui login
        boolean loginResult = userController.login("mario@example.com", "password123");
        assertTrue(loginResult);

        // Verifica che inizialmente non ci siano profili
        assertFalse(userController.hasGuideProfile());
        assertFalse(userController.hasTravelerProfile());

        // Crea e verifica profilo traveler
        Traveler travelerProfile = userController.getTravelerProfile();
        assertNotNull(travelerProfile);
        verify(userDAO).update(mockUser);

        // Configura il mock per simulare che il profilo è stato salvato
        when(userService.hasTravelerProfile(mockUser)).thenReturn(true);
        assertTrue(userController.hasTravelerProfile());

        // Crea e verifica profilo guida
        Guide guideProfile = userController.getGuideProfile();
        assertNotNull(guideProfile);
        verify(userDAO, times(2)).update(mockUser);

        // Configura il mock per simulare che il profilo è stato salvato
        when(userService.hasGuideProfile(mockUser)).thenReturn(true);
        assertTrue(userController.hasGuideProfile());
    }
}
