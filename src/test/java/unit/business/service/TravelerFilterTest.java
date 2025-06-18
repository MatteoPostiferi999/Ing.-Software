package unit.business.service;

import business.service.TravelerFilter;
import model.user.Traveler;
import model.trip.Trip;
import model.booking.BookingRegister;
import model.booking.Booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TravelerFilterTest {

    @Mock
    private Traveler traveler;

    @Mock
    private Trip trip1;

    @Mock
    private Trip trip2;

    @Mock
    private Trip trip3;

    @Mock
    private BookingRegister bookingRegister1;

    @Mock
    private BookingRegister bookingRegister2;

    @Mock
    private BookingRegister bookingRegister3;

    @Mock
    private Booking booking1;

    @Mock
    private Booking booking2;

    private TravelerFilter travelerFilter;
    private LocalDate baseDate;
    private LocalDate minDate;
    private LocalDate maxDate;
    private Double maxPrice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        baseDate = LocalDate.of(2024, 6, 15);
        minDate = LocalDate.of(2024, 6, 1);
        maxDate = LocalDate.of(2024, 6, 30);
        maxPrice = 100.0;

        travelerFilter = new TravelerFilter(traveler, minDate, maxDate, maxPrice);
    }

    @Test
    @DisplayName("Costruttore dovrebbe inizializzare correttamente i parametri")
    void testConstructor() {
        LocalDate testMinDate = LocalDate.of(2024, 7, 1);
        LocalDate testMaxDate = LocalDate.of(2024, 7, 31);
        Double testMaxPrice = 150.0;

        TravelerFilter filter = new TravelerFilter(traveler, testMinDate, testMaxDate, testMaxPrice);

        assertNotNull(filter);
        // Test indiretto tramite il comportamento del filtro
    }

    @Test
    @DisplayName("setMinDate dovrebbe aggiornare la data minima")
    void testSetMinDate() {
        LocalDate newMinDate = LocalDate.of(2024, 7, 1);
        travelerFilter.setMinDate(newMinDate);

        // Setup trip che sarebbe filtrato con la nuova data
        setupTripWithDate(trip1, bookingRegister1, LocalDate.of(2024, 6, 15), 50.0, 2, 5);
        List<Trip> trips = Arrays.asList(trip1);

        List<Trip> result = travelerFilter.filterTrips(trips);

        // Il trip dovrebbe essere filtrato perché la sua data è prima della nuova minDate
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("setMaxDate dovrebbe aggiornare la data massima")
    void testSetMaxDate() {
        LocalDate newMaxDate = LocalDate.of(2024, 6, 10);
        travelerFilter.setMaxDate(newMaxDate);

        // Setup trip che sarebbe filtrato con la nuova data
        setupTripWithDate(trip1, bookingRegister1, LocalDate.of(2024, 6, 15), 50.0, 2, 5);
        List<Trip> trips = Arrays.asList(trip1);

        List<Trip> result = travelerFilter.filterTrips(trips);

        // Il trip dovrebbe essere filtrato perché la sua data è dopo la nuova maxDate
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("setMaxPrice dovrebbe aggiornare il prezzo massimo")
    void testSetMaxPrice() {
        Double newMaxPrice = 30.0;
        travelerFilter.setMaxPrice(newMaxPrice);

        // Setup trip che sarebbe filtrato con il nuovo prezzo
        setupTripWithDate(trip1, bookingRegister1, baseDate, 50.0, 2, 5);
        List<Trip> trips = Arrays.asList(trip1);

        List<Trip> result = travelerFilter.filterTrips(trips);

        // Il trip dovrebbe essere filtrato perché il suo prezzo è superiore al nuovo maxPrice
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("filterTrips dovrebbe restituire trip che soddisfano tutti i criteri")
    void testFilterTripsAllCriteriaMet() {
        // Setup trip valido: ha posti liberi, data ok, prezzo ok
        setupTripWithDate(trip1, bookingRegister1, baseDate, 80.0, 2, 5);
        List<Trip> trips = Arrays.asList(trip1);

        List<Trip> result = travelerFilter.filterTrips(trips);

        assertEquals(1, result.size());
        assertTrue(result.contains(trip1));
    }

    @Test
    @DisplayName("filterTrips dovrebbe escludere trip senza posti liberi")
    void testFilterTripsNoFreeSpots() {
        // Setup trip senza posti liberi: 3 bookings su max 3
        setupTripWithDate(trip1, bookingRegister1, baseDate, 80.0, 3, 3);
        List<Trip> trips = Arrays.asList(trip1);

        List<Trip> result = travelerFilter.filterTrips(trips);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("filterTrips dovrebbe escludere trip con data troppo vecchia")
    void testFilterTripsDateTooEarly() {
        LocalDate tooEarlyDate = minDate.minusDays(1);
        setupTripWithDate(trip1, bookingRegister1, tooEarlyDate, 80.0, 2, 5);
        List<Trip> trips = Arrays.asList(trip1);

        List<Trip> result = travelerFilter.filterTrips(trips);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("filterTrips dovrebbe escludere trip con data troppo recente")
    void testFilterTripsDateTooLate() {
        LocalDate tooLateDate = maxDate.plusDays(1);
        setupTripWithDate(trip1, bookingRegister1, tooLateDate, 80.0, 2, 5);
        List<Trip> trips = Arrays.asList(trip1);

        List<Trip> result = travelerFilter.filterTrips(trips);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("filterTrips dovrebbe escludere trip con prezzo troppo alto")
    void testFilterTripsPriceTooHigh() {
        Double tooHighPrice = maxPrice + 10.0;
        setupTripWithDate(trip1, bookingRegister1, baseDate, tooHighPrice, 2, 5);
        List<Trip> trips = Arrays.asList(trip1);

        List<Trip> result = travelerFilter.filterTrips(trips);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("filterTrips dovrebbe accettare trip con data uguale al minDate")
    void testFilterTripsDateEqualsMinDate() {
        setupTripWithDate(trip1, bookingRegister1, minDate, 80.0, 2, 5);
        List<Trip> trips = Arrays.asList(trip1);

        List<Trip> result = travelerFilter.filterTrips(trips);

        assertEquals(1, result.size());
        assertTrue(result.contains(trip1));
    }

    @Test
    @DisplayName("filterTrips dovrebbe accettare trip con data uguale al maxDate")
    void testFilterTripsDateEqualsMaxDate() {
        setupTripWithDate(trip1, bookingRegister1, maxDate, 80.0, 2, 5);
        List<Trip> trips = Arrays.asList(trip1);

        List<Trip> result = travelerFilter.filterTrips(trips);

        assertEquals(1, result.size());
        assertTrue(result.contains(trip1));
    }

    @Test
    @DisplayName("filterTrips dovrebbe accettare trip con prezzo uguale al maxPrice")
    void testFilterTripsPriceEqualsMaxPrice() {
        setupTripWithDate(trip1, bookingRegister1, baseDate, maxPrice, 2, 5);
        List<Trip> trips = Arrays.asList(trip1);

        List<Trip> result = travelerFilter.filterTrips(trips);

        assertEquals(1, result.size());
        assertTrue(result.contains(trip1));
    }

    @Test
    @DisplayName("filterTrips con minDate null dovrebbe accettare qualsiasi data passata")
    void testFilterTripsMinDateNull() {
        TravelerFilter filterWithNullMinDate = new TravelerFilter(traveler, null, maxDate, maxPrice);
        LocalDate veryOldDate = LocalDate.of(2020, 1, 1);
        setupTripWithDate(trip1, bookingRegister1, veryOldDate, 80.0, 2, 5);
        List<Trip> trips = Arrays.asList(trip1);

        List<Trip> result = filterWithNullMinDate.filterTrips(trips);

        assertEquals(1, result.size());
        assertTrue(result.contains(trip1));
    }

    @Test
    @DisplayName("filterTrips con maxDate null dovrebbe accettare qualsiasi data futura")
    void testFilterTripsMaxDateNull() {
        TravelerFilter filterWithNullMaxDate = new TravelerFilter(traveler, minDate, null, maxPrice);
        LocalDate veryFutureDate = LocalDate.of(2030, 12, 31);
        setupTripWithDate(trip1, bookingRegister1, veryFutureDate, 80.0, 2, 5);
        List<Trip> trips = Arrays.asList(trip1);

        List<Trip> result = filterWithNullMaxDate.filterTrips(trips);

        assertEquals(1, result.size());
        assertTrue(result.contains(trip1));
    }

    @Test
    @DisplayName("filterTrips con maxPrice null dovrebbe accettare qualsiasi prezzo")
    void testFilterTripsMaxPriceNull() {
        TravelerFilter filterWithNullMaxPrice = new TravelerFilter(traveler, minDate, maxDate, null);
        Double veryHighPrice = 10000.0;
        setupTripWithDate(trip1, bookingRegister1, baseDate, veryHighPrice, 2, 5);
        List<Trip> trips = Arrays.asList(trip1);

        List<Trip> result = filterWithNullMaxPrice.filterTrips(trips);

        assertEquals(1, result.size());
        assertTrue(result.contains(trip1));
    }

    @Test
    @DisplayName("filterTrips con tutti i parametri null dovrebbe filtrare solo per posti liberi")
    void testFilterTripsAllParametersNull() {
        TravelerFilter filterAllNull = new TravelerFilter(traveler, null, null, null);

        // Trip 1: con posti liberi
        setupTripWithDate(trip1, bookingRegister1, LocalDate.of(2020, 1, 1), 10000.0, 2, 5);
        // Trip 2: senza posti liberi
        setupTripWithDate(trip2, bookingRegister2, LocalDate.of(2030, 12, 31), 10000.0, 3, 3);

        List<Trip> trips = Arrays.asList(trip1, trip2);

        List<Trip> result = filterAllNull.filterTrips(trips);

        assertEquals(1, result.size());
        assertTrue(result.contains(trip1));
        assertFalse(result.contains(trip2));
    }

    @Test
    @DisplayName("filterTrips dovrebbe gestire lista vuota")
    void testFilterTripsEmptyList() {
        List<Trip> emptyTrips = new ArrayList<>();

        List<Trip> result = travelerFilter.filterTrips(emptyTrips);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("filterTrips dovrebbe filtrare correttamente trip multipli")
    void testFilterTripsMultipleTrips() {
        // Trip 1: valido
        setupTripWithDate(trip1, bookingRegister1, baseDate, 80.0, 2, 5);
        // Trip 2: prezzo troppo alto
        setupTripWithDate(trip2, bookingRegister2, baseDate, 150.0, 2, 5);
        // Trip 3: senza posti liberi
        setupTripWithDate(trip3, bookingRegister3, baseDate, 80.0, 4, 4);

        List<Trip> trips = Arrays.asList(trip1, trip2, trip3);

        List<Trip> result = travelerFilter.filterTrips(trips);

        assertEquals(1, result.size());
        assertTrue(result.contains(trip1));
        assertFalse(result.contains(trip2));
        assertFalse(result.contains(trip3));
    }

    @Test
    @DisplayName("filterTrips dovrebbe restituire tutti i trip validi")
    void testFilterTripsMultipleValidTrips() {
        // Trip 1: valido
        setupTripWithDate(trip1, bookingRegister1, baseDate, 80.0, 1, 5);
        // Trip 2: valido
        setupTripWithDate(trip2, bookingRegister2, baseDate.plusDays(5), 90.0, 2, 5);
        // Trip 3: valido
        setupTripWithDate(trip3, bookingRegister3, baseDate.minusDays(5), 100.0, 0, 3);

        List<Trip> trips = Arrays.asList(trip1, trip2, trip3);

        List<Trip> result = travelerFilter.filterTrips(trips);

        assertEquals(3, result.size());
        assertTrue(result.contains(trip1));
        assertTrue(result.contains(trip2));
        assertTrue(result.contains(trip3));
    }

    // Helper method per setup dei trip
    private void setupTripWithDate(Trip trip, BookingRegister bookingRegister,
                                   LocalDate date, Double price, int currentBookings, int maxTravelers) {
        when(trip.getBookingRegister()).thenReturn(bookingRegister);
        when(trip.getDate()).thenReturn(date);
        when(trip.getPrice()).thenReturn(price);

        // Setup bookings
        List<Booking> bookings = new ArrayList<>();
        for (int i = 0; i < currentBookings; i++) {
            bookings.add(mock(Booking.class));
        }
        when(bookingRegister.getBookings()).thenReturn(bookings);
        when(bookingRegister.getMaxTrav()).thenReturn(maxTravelers);
    }
}