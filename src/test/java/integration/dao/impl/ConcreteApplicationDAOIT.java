// ConcreteApplicationDAOIT.java
package integration.dao.impl;

import dao.impl.ConcreteApplicationDAO;
import db.DBManager;
import model.application.Application;
import model.application.ApplicationStatus;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConcreteApplicationDAOIT {

    private static ConcreteApplicationDAO dao;
    private static final int TEST_GUIDE_ID = 999;
    private static final int TEST_TRIP_ID  = 999;
    private static final int TEST_USER_ID  = TEST_GUIDE_ID;

    private static Application testApplication;

    @BeforeAll
    static void setupAll() {
        dao = new ConcreteApplicationDAO();

        try (Connection conn = DBManager.getInstance().getConnection()) {
            System.out.println("🔧 Inizio setup test...");

            // 0) Cleanup preventivo per evitare conflitti
            cleanupTestData(conn);

            // 1) Creo l'utente
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (user_id, username, email, password) VALUES (?, 'testuser999', 'test999@test.com', 'testpass')"
            )) {
                ps.setInt(1, TEST_USER_ID);
                ps.executeUpdate();
                System.out.println("✅ Utente test creato");
            }

            // 2) Creo la guida
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO guides (guide_id, user_id) VALUES (?, ?)"
            )) {
                ps.setInt(1, TEST_GUIDE_ID);
                ps.setInt(2, TEST_USER_ID);
                ps.executeUpdate();
                System.out.println("✅ Guida test creata");
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO trips (trip_id, title, description, price, date, min_travelers, max_travelers, max_guides) " +
                            "VALUES (?, ?, ?, ?, CURRENT_DATE, ?, ?, ?)"
            )) {
                ps.setInt(1, TEST_TRIP_ID);
                ps.setString(2, "Test Trip");
                ps.setString(3, "Integration-test trip");
                ps.setDouble(4, 100.0);
                ps.setInt(5, 1);
                ps.setInt(6, 10);
                ps.setInt(7, 2);
                ps.executeUpdate();
                System.out.println("✅ Viaggio test creato");
            }

            System.out.println("🎉 Setup completato con successo");
        } catch (SQLException e) {
            System.err.println("❌ Errore nel setup: " + e.getMessage());
            throw new RuntimeException("Errore nel setup iniziale", e);
        }
    }

    @AfterAll
    static void cleanupAll() {
        try (Connection conn = DBManager.getInstance().getConnection()) {
            System.out.println("🧹 Inizio cleanup finale...");
            cleanupTestData(conn);
            System.out.println("✅ Cleanup finale completato");
        } catch (SQLException e) {
            System.err.println("❌ Errore durante cleanupAll: " + e.getMessage());
            throw new RuntimeException("Errore nel cleanup finale", e);
        }
    }

    private static void cleanupTestData(Connection conn) throws SQLException {
        // Elimina in ordine inverso per rispettare i foreign key constraints

        // 1) Elimina le applications
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM applications WHERE guide_id = ? OR trip_id = ?"
        )) {
            ps.setInt(1, TEST_GUIDE_ID);
            ps.setInt(2, TEST_TRIP_ID);
            int count = ps.executeUpdate();
            System.out.println("🗑️ Applications eliminate: " + count);
        }

        // 2) Elimina la guida
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM guides WHERE guide_id = ?"
        )) {
            ps.setInt(1, TEST_GUIDE_ID);
            int count = ps.executeUpdate();
            System.out.println("🗑️ Guide eliminate: " + count);
        }

        // 3) Elimina il viaggio
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM trips WHERE trip_id = ?"
        )) {
            ps.setInt(1, TEST_TRIP_ID);
            int count = ps.executeUpdate();
            System.out.println("🗑️ Viaggi eliminati: " + count);
        }

        // 4) Elimina l'utente
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM users WHERE user_id = ?"
        )) {
            ps.setInt(1, TEST_USER_ID);
            int count = ps.executeUpdate();
            System.out.println("🗑️ Utenti eliminati: " + count);
        }
    }

    @Test
    @Order(1)
    void testCreateApplication() {
        System.out.println("🧪 Test 1: Creazione application");

        // Creo una nuova Application
        testApplication = new Application(
                0,                         // 0 → nuovo record
                "Test CV Content - Integration Test",
                TEST_GUIDE_ID,
                TEST_TRIP_ID,
                ApplicationStatus.PENDING
        );

        // Verifico che inizialmente l'ID sia 0
        assertEquals(0, testApplication.getApplicationId(), "ID dovrebbe essere 0 prima del save");

        // Salvo l'application
        dao.save(testApplication);

        // Verifico che l'ID sia stato generato automaticamente
        assertTrue(testApplication.getApplicationId() > 0, "ID dovrebbe essere generato automaticamente dopo il save");

        // Verifico che la ricerca funzioni
        Application result = dao.findByGuideAndTrip(TEST_GUIDE_ID, TEST_TRIP_ID);
        assertNotNull(result, "L'application creata dovrebbe essere trovata");
        assertEquals("Test CV Content - Integration Test", result.getCV());
        assertEquals(ApplicationStatus.PENDING, result.getStatus());
        assertEquals(TEST_GUIDE_ID, result.getGuideId());
        assertEquals(TEST_TRIP_ID, result.getTripId());

        System.out.println("✅ Application creata con ID: " + testApplication.getApplicationId());
    }

    @Test
    @Order(2)
    void testFindByGuideAndTrip() {
        System.out.println("🧪 Test 2: Ricerca per guide e trip");

        Application result = dao.findByGuideAndTrip(TEST_GUIDE_ID, TEST_TRIP_ID);
        assertNotNull(result, "Dovrebbe esistere l'application creata nel test precedente");
        assertEquals("Test CV Content - Integration Test", result.getCV());
        assertEquals(ApplicationStatus.PENDING, result.getStatus());

        System.out.println("✅ Application trovata correttamente");
    }

    @Test
    @Order(3)
    void testUpdateApplicationStatus() {
        System.out.println("🧪 Test 3: Aggiornamento status");

        // Aggiorno lo status
        dao.updateStatus(testApplication, ApplicationStatus.ACCEPTED);

        // Verifico l'aggiornamento
        Application result = dao.findByGuideAndTrip(TEST_GUIDE_ID, TEST_TRIP_ID);
        assertNotNull(result, "L'application dovrebbe ancora esistere");
        assertEquals(ApplicationStatus.ACCEPTED, result.getStatus());

        System.out.println("✅ Status aggiornato correttamente");
    }

    @Test
    @Order(4)
    void testCountMethods() {
        System.out.println("🧪 Test 4: Metodi di conteggio");

        // Test count totale
        int totalCount = dao.countApplicationsByTripId(TEST_TRIP_ID);
        assertEquals(1, totalCount, "Dovrebbe esserci 1 application per questo trip");

        // Test count pending (dovrebbe essere 0 perché abbiamo cambiato lo status ad ACCEPTED)
        int pendingCount = dao.countPendingApplicationsByTripId(TEST_TRIP_ID);
        assertEquals(0, pendingCount, "Non dovrebbero esserci application PENDING");

        System.out.println("✅ Metodi di conteggio funzionano correttamente");
    }

    @Test
    @Order(5)
    void testHasGuideAppliedForTrip() {
        System.out.println("🧪 Test 5: Verifica esistenza candidatura");

        boolean hasApplied = dao.hasGuideAppliedForTrip(TEST_GUIDE_ID, TEST_TRIP_ID);
        assertTrue(hasApplied, "La guida dovrebbe aver fatto candidatura per questo viaggio");

        boolean hasNotApplied = dao.hasGuideAppliedForTrip(TEST_GUIDE_ID + 1, TEST_TRIP_ID);
        assertFalse(hasNotApplied, "Una guida inesistente non dovrebbe aver fatto candidatura");

        System.out.println("✅ Verifica esistenza candidatura funziona correttamente");
    }

    @Test
    @Order(6)
    void testRemoveApplication() {
        System.out.println("🧪 Test 6: Eliminazione application");

        // Verifico che esista prima di eliminarla
        Application beforeDelete = dao.findByGuideAndTrip(TEST_GUIDE_ID, TEST_TRIP_ID);
        assertNotNull(beforeDelete, "L'application dovrebbe esistere prima della delete");

        // Elimino l'application
        dao.delete(testApplication);

        // Verifico che sia stata eliminata
        Application afterDelete = dao.findByGuideAndTrip(TEST_GUIDE_ID, TEST_TRIP_ID);
        assertNull(afterDelete, "Dopo la delete, l'application non dovrebbe più esistere");

        // Verifico anche con i metodi di conteggio
        int count = dao.countApplicationsByTripId(TEST_TRIP_ID);
        assertEquals(0, count, "Il conteggio dovrebbe essere 0 dopo la delete");

        System.out.println("✅ Application eliminata correttamente");
    }
}