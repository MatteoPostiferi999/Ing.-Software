package integration.dao.impl;

import dao.impl.ConcreteTripDAO;
import dao.interfaces.TripDAO;
import dao.interfaces.ActivityDAO;
import dao.interfaces.BookingDAO;
import dao.interfaces.AssignmentDAO;
import dao.interfaces.ApplicationDAO;
import db.DBManager;
import model.trip.Trip;
import model.trip.Activity;
import model.user.Skill;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConcreteTripDAOIT {
    private static final int TEST_TRIP_ID = 999;
    private static final String TEST_TRIP_TITLE = "Test Trip";
    private static final String TEST_TRIP_DESCRIPTION = "Integration test trip";
    private static final double TEST_TRIP_PRICE = 100.0;
    private static final LocalDate TEST_TRIP_DATE = LocalDate.of(2024, 12, 25);
    private static final int TEST_MIN_TRAVELERS = 1;
    private static final int TEST_MAX_TRAVELERS = 10;
    private static final int TEST_MAX_GUIDES = 2;

    private static TripDAO tripDAO;
    private static DBManager dbManager;

    @BeforeAll
    static void setUp() {
        dbManager = DBManager.getInstance();
        tripDAO = new ConcreteTripDAO();

        // Setup del database per i test
        setupTestData();
    }

    @AfterAll
    static void tearDown() {
        cleanupTestData();
    }

    private static void setupTestData() {
        try (Connection conn = dbManager.getConnection()) {
            // Pulisci eventuali dati precedenti
            cleanupTestData();

            // Crea trip di test
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO trips (trip_id, title, description, price, date, min_travelers, max_travelers, max_guides) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            )) {
                ps.setInt(1, TEST_TRIP_ID);
                ps.setString(2, TEST_TRIP_TITLE);
                ps.setString(3, TEST_TRIP_DESCRIPTION);
                ps.setDouble(4, TEST_TRIP_PRICE);
                ps.setDate(5, Date.valueOf(TEST_TRIP_DATE));
                ps.setInt(6, TEST_MIN_TRAVELERS);
                ps.setInt(7, TEST_MAX_TRAVELERS);
                ps.setInt(8, TEST_MAX_GUIDES);
                ps.executeUpdate();
                System.out.println("✅ Trip test creato");
            }

            System.out.println("🎉 Setup completato con successo");

        } catch (SQLException e) {
            System.err.println("❌ Errore durante il setup: " + e.getMessage());
            fail("Setup fallito: " + e.getMessage());
        }
    }

    private static void cleanupTestData() {
        try (Connection conn = dbManager.getConnection()) {
            // Pulisci in ordine per rispettare le foreign key
            String[] cleanupQueries = {
                    "DELETE FROM trip_required_skills WHERE trip_id = " + TEST_TRIP_ID,
                    "DELETE FROM activities WHERE trip_id = " + TEST_TRIP_ID,
                    "DELETE FROM bookings WHERE trip_id = " + TEST_TRIP_ID,
                    "DELETE FROM assignments WHERE trip_id = " + TEST_TRIP_ID,
                    "DELETE FROM applications WHERE trip_id = " + TEST_TRIP_ID,
                    "DELETE FROM trips WHERE trip_id = " + TEST_TRIP_ID
            };

            for (String query : cleanupQueries) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(query);
                }
            }
            System.out.println("🧹 Cleanup completato");

        } catch (SQLException e) {
            System.err.println("⚠️ Errore durante il cleanup: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test findById - Trip esistente")
    void testFindById_ExistingTrip() {
        Trip trip = tripDAO.findById(TEST_TRIP_ID);

        assertNotNull(trip, "Il trip dovrebbe essere trovato");
        assertEquals(TEST_TRIP_ID, trip.getTripId());
        assertEquals(TEST_TRIP_TITLE, trip.getTitle());
        assertEquals(TEST_TRIP_DESCRIPTION, trip.getDescription());
        assertEquals(TEST_TRIP_PRICE, trip.getPrice(), 0.01);
        assertEquals(TEST_TRIP_DATE, trip.getDate());
        assertEquals(TEST_MIN_TRAVELERS, trip.getBookingRegister().getMinTrav());
        assertEquals(TEST_MAX_TRAVELERS, trip.getBookingRegister().getMaxTrav());
        assertEquals(TEST_MAX_GUIDES, trip.getAssignmentRegister().getMaxGuides());

        System.out.println("✅ Test findById completato");
    }

    @Test
    @Order(2)
    @DisplayName("Test findById - Trip inesistente")
    void testFindById_NonExistingTrip() {
        Trip trip = tripDAO.findById(99999);

        assertNull(trip, "Il trip non dovrebbe essere trovato");
        System.out.println("✅ Test findById (trip inesistente) completato");
    }

    @Test
    @Order(3)
    @DisplayName("Test findAll - Contiene il trip di test")
    void testFindAll_ContainsTestTrip() {
        List<Trip> trips = tripDAO.findAll();

        assertNotNull(trips, "La lista non dovrebbe essere null");
        assertTrue(trips.stream().anyMatch(t -> t.getTripId() == TEST_TRIP_ID),
                "La lista dovrebbe contenere il trip di test");

        System.out.println("✅ Test findAll completato - trovati " + trips.size() + " trip");
    }

    @Test
    @Order(4)
    @DisplayName("Test save - Nuovo trip")
    void testSave_NewTrip() {
        Trip newTrip = new Trip(
                0, // ID sarà generato automaticamente
                "Nuovo Trip Test",
                "Descrizione nuovo trip",
                200.0,
                LocalDate.of(2024, 6, 15),
                2,
                8,
                3
        );

        tripDAO.save(newTrip);

        assertTrue(newTrip.getTripId() > 0, "L'ID dovrebbe essere generato automaticamente");

        // Verifica che sia stato salvato
        Trip savedTrip = tripDAO.findById(newTrip.getTripId());
        assertNotNull(savedTrip, "Il trip salvato dovrebbe essere trovato");
        assertEquals("Nuovo Trip Test", savedTrip.getTitle());

        // Pulisci il trip creato
        tripDAO.deleteById(newTrip.getTripId());

        System.out.println("✅ Test save completato - ID generato: " + newTrip.getTripId());
    }

    @Test
    @Order(5)
    @DisplayName("Test update - Modifica trip esistente")
    void testUpdate_ExistingTrip() {
        // Trova il trip esistente
        Trip trip = tripDAO.findById(TEST_TRIP_ID);
        assertNotNull(trip, "Il trip dovrebbe esistere");

        // Modifica alcuni campi
        String newTitle = "Titolo Modificato";
        String newDescription = "Descrizione Modificata";
        double newPrice = 150.0;

        trip.setTitle(newTitle);
        trip.setDescription(newDescription);
        trip.setPrice(newPrice);

        // Esegui l'update
        tripDAO.update(trip);

        // Verifica che le modifiche siano state salvate
        Trip updatedTrip = tripDAO.findById(TEST_TRIP_ID);
        assertNotNull(updatedTrip);
        assertEquals(newTitle, updatedTrip.getTitle());
        assertEquals(newDescription, updatedTrip.getDescription());
        assertEquals(newPrice, updatedTrip.getPrice(), 0.01);

        System.out.println("✅ Test update completato");
    }

    @Test
    @Order(6)
    @DisplayName("Test deleteById - Rimozione trip")
    void testDeleteById() {
        // Crea un trip temporaneo da eliminare
        Trip tempTrip = new Trip(
                0,
                "Trip da Eliminare",
                "Questo trip sarà eliminato",
                50.0,
                LocalDate.of(2024, 3, 10),
                1,
                5,
                1
        );

        tripDAO.save(tempTrip);
        int tempTripId = tempTrip.getTripId();

        // Verifica che esista
        assertNotNull(tripDAO.findById(tempTripId), "Il trip temporaneo dovrebbe esistere");

        // Eliminalo
        tripDAO.deleteById(tempTripId);

        // Verifica che sia stato eliminato
        assertNull(tripDAO.findById(tempTripId), "Il trip dovrebbe essere stato eliminato");

        System.out.println("✅ Test deleteById completato");
    }

    @Test
    @Order(7)
    @DisplayName("Test findByIdFull - Con relazioni caricate")
    void testFindByIdFull() {
        Trip trip = tripDAO.findByIdFull(TEST_TRIP_ID);

        assertNotNull(trip, "Il trip dovrebbe essere trovato");
        assertEquals(TEST_TRIP_ID, trip.getTripId());

        // Verifica che le liste siano inizializzate (anche se vuote)
        assertNotNull(trip.getRequiredSkills(), "La lista delle skill richieste dovrebbe essere inizializzata");
        assertNotNull(trip.getPlannedActivities(), "La lista delle attività dovrebbe essere inizializzata");

        System.out.println("✅ Test findByIdFull completato");
    }

    @Test
    @Order(8)
    @DisplayName("Test findAllFull - Con tutte le relazioni")
    void testFindAllFull() {
        List<Trip> trips = tripDAO.findAllFull();

        assertNotNull(trips, "La lista non dovrebbe essere null");
        assertFalse(trips.isEmpty(), "La lista non dovrebbe essere vuota");

        // Verifica che almeno uno dei trip abbia le relazioni caricate
        Trip testTrip = trips.stream()
                .filter(t -> t.getTripId() == TEST_TRIP_ID)
                .findFirst()
                .orElse(null);

        assertNotNull(testTrip, "Il trip di test dovrebbe essere nella lista");
        assertNotNull(testTrip.getRequiredSkills(), "Le skill richieste dovrebbero essere caricate");
        assertNotNull(testTrip.getPlannedActivities(), "Le attività dovrebbero essere caricate");

        System.out.println("✅ Test findAllFull completato - trip con relazioni: " + trips.size());
    }

    @Test
    @Order(9)
    @DisplayName("Test gestione errori - Connessione database")
    void testDatabaseConnection() {
        assertNotNull(dbManager, "Il DBManager dovrebbe essere inizializzato");

        try (Connection conn = dbManager.getConnection()) {
            assertNotNull(conn, "La connessione dovrebbe essere valida");
            assertFalse(conn.isClosed(), "La connessione dovrebbe essere aperta");
            System.out.println("✅ Test connessione database completato");
        } catch (SQLException e) {
            fail("Errore nella connessione al database: " + e.getMessage());
        }
    }

    @Test
    @Order(10)
    @DisplayName("Test performance - Operazioni multiple")
    void testPerformance() {
        long startTime = System.currentTimeMillis();

        // Esegui operazioni multiple
        for (int i = 0; i < 5; i++) {
            tripDAO.findById(TEST_TRIP_ID);
        }

        List<Trip> allTrips = tripDAO.findAll();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertTrue(duration < 5000, "Le operazioni dovrebbero completarsi in meno di 5 secondi");
        System.out.println("✅ Test performance completato in " + duration + "ms");
    }
}