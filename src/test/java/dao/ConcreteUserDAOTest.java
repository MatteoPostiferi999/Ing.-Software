package dao;

import dao.impl.ConcreteUserDAO;
import dao.interfaces.UserDAO;
import model.user.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ConcreteUserDAOTest {

    private static UserDAO userDAO;

    @BeforeAll
    public static void setup() {
        userDAO = new ConcreteUserDAO();
    }

    @Test
    public void testSaveAndFindById() {
        User user = new User(0, "junit", "junit@example.com", "test123");
        userDAO.save(user);
        assertTrue(user.getUserId() > 0, "User ID should be generated");

        User loaded = userDAO.findById(user.getUserId());
        assertNotNull(loaded, "User should be found");
        assertEquals("junit", loaded.getUserName());
        assertEquals("junit@example.com", loaded.getEmail());
    }

    @Test
    public void testFindByEmail() {
        User user = new User(1, "findme", "find@example.com", "pw");
        userDAO.save(user);

        User found = userDAO.findByEmail("find@example.com");
        assertNotNull(found);
        assertEquals("findme", found.getUserName());
    }

    @Test
    public void testUpdate() {
        User user = new User(2, "update", "update@example.com", "pw");
        userDAO.save(user);
        user.setUserName("updated_name");
        userDAO.update(user);

        User updated = userDAO.findById(user.getUserId());
        assertEquals("updated_name", updated.getUserName());
    }

    @Test
    public void testDelete() {
        User user = new User(3, "delete", "delete@example.com", "pw");
        userDAO.save(user);
        userDAO.delete(user);

        User deleted = userDAO.findById(user.getUserId());
        assertNull(deleted, "User should be deleted");
    }
}