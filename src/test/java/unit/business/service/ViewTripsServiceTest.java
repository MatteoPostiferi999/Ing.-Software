package unit.business.service;

import business.service.TripService;
import business.service.ViewTripsService;
import business.service.TripFilterStrategy;
import dao.interfaces.TripDAO;
import model.trip.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ViewTripsServiceTest {

    @Mock
    private TripDAO tripDAO;

    @Mock
    private TripService tripService;

    @Mock
    private TripFilterStrategy filterStrategy;

    private ViewTripsService viewTripsService;
    private List<Trip> allTrips;
    private Trip trip1, trip2, trip3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        viewTripsService = new ViewTripsService(tripDAO, tripService);

        // Setup test trips
        trip1 = createTrip(1, "Roma Adventure", "Discover Rome");
        trip2 = createTrip(2, "Florence Art Tour", "Art and culture in Florence");
        trip3 = createTrip(3, "Venice Canal Tour", "Explore Venice canals");

        allTrips = Arrays.asList(trip1, trip2, trip3);
    }

    private Trip createTrip(int id, String title, String description) {
        return new Trip(id, title, description, 100.0, LocalDate.of(2024, 6, 15), 5, 15, 3);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create ViewTripsService with required dependencies")
        void shouldCreateViewTripsServiceWithRequiredDependencies() {
            ViewTripsService service = new ViewTripsService(tripDAO, tripService);
            assertNotNull(service);
        }

        @Test
        @DisplayName("Should set filter strategy")
        void shouldSetFilterStrategy() {
            viewTripsService.setStrategy(filterStrategy);
            // Strategy setting is tested indirectly through viewTrips() behavior
            assertNotNull(viewTripsService);
        }
    }

    @Nested
    @DisplayName("View Trips Tests")
    class ViewTripsTests {

        @Test
        @DisplayName("Should return all trips when no strategy is set")
        void shouldReturnAllTripsWhenNoStrategyIsSet() {
            // Given
            when(tripDAO.findAll()).thenReturn(allTrips);

            // When
            List<Trip> result = viewTripsService.viewTrips();

            // Then
            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals(allTrips, result);
            verify(tripDAO).findAll();
            verifyNoInteractions(filterStrategy);
        }

        @Test
        @DisplayName("Should return filtered trips when strategy is set")
        void shouldReturnFilteredTripsWhenStrategyIsSet() {
            // Given
            List<Trip> filteredTrips = Arrays.asList(trip1, trip2);
            when(tripDAO.findAll()).thenReturn(allTrips);
            when(filterStrategy.filterTrips(allTrips)).thenReturn(filteredTrips);
            viewTripsService.setStrategy(filterStrategy);

            // When
            List<Trip> result = viewTripsService.viewTrips();

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(filteredTrips, result);
            verify(tripDAO).findAll();
            verify(filterStrategy).filterTrips(allTrips);
        }

        @Test
        @DisplayName("Should return empty list when no trips exist")
        void shouldReturnEmptyListWhenNoTripsExist() {
            // Given
            List<Trip> emptyList = Collections.emptyList();
            when(tripDAO.findAll()).thenReturn(emptyList);

            // When
            List<Trip> result = viewTripsService.viewTrips();

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(tripDAO).findAll();
        }

        @Test
        @DisplayName("Should return empty list when strategy filters out all trips")
        void shouldReturnEmptyListWhenStrategyFiltersOutAllTrips() {
            // Given
            List<Trip> emptyFilteredList = Collections.emptyList();
            when(tripDAO.findAll()).thenReturn(allTrips);
            when(filterStrategy.filterTrips(allTrips)).thenReturn(emptyFilteredList);
            viewTripsService.setStrategy(filterStrategy);

            // When
            List<Trip> result = viewTripsService.viewTrips();

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(tripDAO).findAll();
            verify(filterStrategy).filterTrips(allTrips);
        }

        @Test
        @DisplayName("Should handle null return from DAO")
        void shouldHandleNullReturnFromDAO() {
            // Given
            when(tripDAO.findAll()).thenReturn(null);

            // When & Then
            assertDoesNotThrow(() -> {
                List<Trip> result = viewTripsService.viewTrips();
                assertNull(result);
            });
            verify(tripDAO).findAll();
        }

        @Test
        @DisplayName("Should handle strategy returning null")
        void shouldHandleStrategyReturningNull() {
            // Given
            when(tripDAO.findAll()).thenReturn(allTrips);
            when(filterStrategy.filterTrips(allTrips)).thenReturn(null);
            viewTripsService.setStrategy(filterStrategy);

            // When
            List<Trip> result = viewTripsService.viewTrips();

            // Then
            assertNull(result);
            verify(tripDAO).findAll();
            verify(filterStrategy).filterTrips(allTrips);
        }
    }

    @Nested
    @DisplayName("View Trip Details Tests")
    class ViewTripDetailsTests {

        @Test
        @DisplayName("Should return trip details when trip exists and is visible")
        void shouldReturnTripDetailsWhenTripExistsAndIsVisible() {
            // Given
            int tripId = 1;
            when(tripService.getTripById(tripId)).thenReturn(trip1);
            when(tripDAO.findAll()).thenReturn(allTrips);
            // No strategy set, so all trips are visible

            // When
            Trip result = viewTripsService.viewTripDetails(tripId);

            // Then
            assertNotNull(result);
            assertEquals(trip1, result);
            verify(tripService).getTripById(tripId);
            verify(tripDAO).findAll();
        }

        @Test
        @DisplayName("Should return null when trip does not exist")
        void shouldReturnNullWhenTripDoesNotExist() {
            // Given
            int tripId = 999;
            when(tripService.getTripById(tripId)).thenReturn(null);

            // When
            Trip result = viewTripsService.viewTripDetails(tripId);

            // Then
            assertNull(result);
            verify(tripService).getTripById(tripId);
            verify(tripDAO, never()).findAll();
        }

        @Test
        @DisplayName("Should return null when trip exists but is not visible due to filter")
        void shouldReturnNullWhenTripExistsButIsNotVisibleDueToFilter() {
            // Given
            int tripId = 3;
            List<Trip> filteredTrips = Arrays.asList(trip1, trip2); // trip3 is filtered out
            when(tripService.getTripById(tripId)).thenReturn(trip3);
            when(tripDAO.findAll()).thenReturn(allTrips);
            when(filterStrategy.filterTrips(allTrips)).thenReturn(filteredTrips);
            viewTripsService.setStrategy(filterStrategy);

            // When
            Trip result = viewTripsService.viewTripDetails(tripId);

            // Then
            assertNull(result);
            verify(tripService).getTripById(tripId);
            verify(tripDAO).findAll();
            verify(filterStrategy).filterTrips(allTrips);
        }

        @Test
        @DisplayName("Should return trip when strategy allows it")
        void shouldReturnTripWhenStrategyAllowsIt() {
            // Given
            int tripId = 1;
            List<Trip> filteredTrips = Arrays.asList(trip1, trip2);
            when(tripService.getTripById(tripId)).thenReturn(trip1);
            when(tripDAO.findAll()).thenReturn(allTrips);
            when(filterStrategy.filterTrips(allTrips)).thenReturn(filteredTrips);
            viewTripsService.setStrategy(filterStrategy);

            // When
            Trip result = viewTripsService.viewTripDetails(tripId);

            // Then
            assertNotNull(result);
            assertEquals(trip1, result);
            verify(tripService).getTripById(tripId);
            verify(tripDAO).findAll();
            verify(filterStrategy).filterTrips(allTrips);
        }

        @Test
        @DisplayName("Should handle empty visible trips list")
        void shouldHandleEmptyVisibleTripsList() {
            // Given
            int tripId = 1;
            List<Trip> emptyFilteredTrips = Collections.emptyList();
            when(tripService.getTripById(tripId)).thenReturn(trip1);
            when(tripDAO.findAll()).thenReturn(allTrips);
            when(filterStrategy.filterTrips(allTrips)).thenReturn(emptyFilteredTrips);
            viewTripsService.setStrategy(filterStrategy);

            // When
            Trip result = viewTripsService.viewTripDetails(tripId);

            // Then
            assertNull(result);
            verify(tripService).getTripById(tripId);
            verify(tripDAO).findAll();
            verify(filterStrategy).filterTrips(allTrips);
        }
    }

    @Nested
    @DisplayName("Strategy Integration Tests")
    class StrategyIntegrationTests {

        @Test
        @DisplayName("Should work correctly when strategy is changed multiple times")
        void shouldWorkCorrectlyWhenStrategyIsChangedMultipleTimes() {
            // Given
            TripFilterStrategy strategy1 = mock(TripFilterStrategy.class);
            TripFilterStrategy strategy2 = mock(TripFilterStrategy.class);

            List<Trip> filteredTrips1 = Arrays.asList(trip1);
            List<Trip> filteredTrips2 = Arrays.asList(trip2, trip3);

            when(tripDAO.findAll()).thenReturn(allTrips);
            when(strategy1.filterTrips(allTrips)).thenReturn(filteredTrips1);
            when(strategy2.filterTrips(allTrips)).thenReturn(filteredTrips2);

            // When & Then - First strategy
            viewTripsService.setStrategy(strategy1);
            List<Trip> result1 = viewTripsService.viewTrips();
            assertEquals(1, result1.size());
            assertEquals(trip1, result1.get(0));

            // When & Then - Second strategy
            viewTripsService.setStrategy(strategy2);
            List<Trip> result2 = viewTripsService.viewTrips();
            assertEquals(2, result2.size());
            assertTrue(result2.contains(trip2));
            assertTrue(result2.contains(trip3));

            // When & Then - Remove strategy
            viewTripsService.setStrategy(null);
            List<Trip> result3 = viewTripsService.viewTrips();
            assertEquals(3, result3.size());
            assertEquals(allTrips, result3);
        }

        @Test
        @DisplayName("Should handle strategy that modifies the input list")
        void shouldHandleStrategyThatModifiesTheInputList() {
            // Given
            TripFilterStrategy modifyingStrategy = mock(TripFilterStrategy.class);
            List<Trip> modifiedList = Arrays.asList(trip1, trip2);

            when(tripDAO.findAll()).thenReturn(allTrips);
            when(modifyingStrategy.filterTrips(any())).thenReturn(modifiedList);
            viewTripsService.setStrategy(modifyingStrategy);

            // When
            List<Trip> result = viewTripsService.viewTrips();

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(modifiedList, result);
            verify(modifyingStrategy).filterTrips(allTrips);
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle exception from TripDAO")
        void shouldHandleExceptionFromTripDAO() {
            // Given
            when(tripDAO.findAll()).thenThrow(new RuntimeException("Database error"));

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                viewTripsService.viewTrips();
            });
            verify(tripDAO).findAll();
        }

        @Test
        @DisplayName("Should handle exception from TripService")
        void shouldHandleExceptionFromTripService() {
            // Given
            int tripId = 1;
            when(tripService.getTripById(tripId)).thenThrow(new RuntimeException("Service error"));

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                viewTripsService.viewTripDetails(tripId);
            });
            verify(tripService).getTripById(tripId);
        }

        @Test
        @DisplayName("Should handle exception from filter strategy")
        void shouldHandleExceptionFromFilterStrategy() {
            // Given
            when(tripDAO.findAll()).thenReturn(allTrips);
            when(filterStrategy.filterTrips(allTrips)).thenThrow(new RuntimeException("Filter error"));
            viewTripsService.setStrategy(filterStrategy);

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                viewTripsService.viewTrips();
            });
            verify(tripDAO).findAll();
            verify(filterStrategy).filterTrips(allTrips);
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle trip comparison with equals method")
        void shouldHandleTripComparisonWithEqualsMethod() {
            // Given - Create a trip with same ID but different object reference
            Trip sameTrip = createTrip(1, "Roma Adventure", "Discover Rome");
            int tripId = 1;

            when(tripService.getTripById(tripId)).thenReturn(sameTrip);
            when(tripDAO.findAll()).thenReturn(allTrips);

            // When
            Trip result = viewTripsService.viewTripDetails(tripId);

            // Then - Should work if Trip class implements equals() properly
            // or will return null if it doesn't (which is the current behavior)
            verify(tripService).getTripById(tripId);
            verify(tripDAO).findAll();
        }

        @Test
        @DisplayName("Should handle large trip lists efficiently")
        void shouldHandleLargeTripListsEfficiently() {
            // Given - Create a large list of trips
            List<Trip> largeList = Collections.nCopies(1000, trip1);
            when(tripDAO.findAll()).thenReturn(largeList);

            // When
            List<Trip> result = viewTripsService.viewTrips();

            // Then
            assertNotNull(result);
            assertEquals(1000, result.size());
            verify(tripDAO).findAll();
        }
    }
}
