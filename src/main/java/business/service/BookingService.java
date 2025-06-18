package business.service;
import dao.interfaces.BookingDAO;
import dao.interfaces.TripDAO;
import dao.interfaces.TravelerDAO;

import model.trip.Trip;
import model.user.Traveler;
import model.booking.Booking;
import model.booking.BookingRegister;
import model.notification.Notification;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class BookingService {

    private BookingDAO bookingDAO;
    private TripDAO tripDAO;
    private TravelerDAO travelerDAO;
    private NotificationService notificationService;

    public BookingService(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }

    // Costruttore completo con tutti i DAO necessari
    public BookingService(BookingDAO bookingDAO, TripDAO tripDAO, TravelerDAO travelerDAO,
                         NotificationService notificationService) {
        this.bookingDAO = bookingDAO;
        this.tripDAO = tripDAO;
        this.travelerDAO = travelerDAO;
        this.notificationService = notificationService;
    }

    // Setter per dependency injection
    public void setTripDAO(TripDAO tripDAO) {
        this.tripDAO = tripDAO;
    }

    public void setTravelerDAO(TravelerDAO travelerDAO) {
        this.travelerDAO = travelerDAO;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Prenota un viaggio per un viaggiatore
     * @param traveler il viaggiatore che prenota
     * @param trip il viaggio da prenotare
     * @return true se la prenotazione è avvenuta con successo, false altrimenti
     */
    public boolean bookTrip(Traveler traveler, Trip trip) {
        BookingRegister register = trip.getBookingRegister();
        if (register.getAvailableSpots() > 0 && !register.hasBooking(traveler)) {
            Booking booking = new Booking(traveler, trip);
            register.addBooking(booking);
            bookingDAO.save(booking);

            // Notifica il viaggiatore
            if (notificationService != null) {
                notificationService.sendNotification(traveler,
                    "La tua prenotazione per il viaggio \"" + trip.getTitle() + "\" è stata confermata.");
            }

            return true;
        }
        return false;
    }

    /**
     * Prenota un viaggio utilizzando gli ID
     * @param travelerId l'ID del viaggiatore
     * @param tripId l'ID del viaggio
     * @return true se la prenotazione è avvenuta con successo, false altrimenti
     */
    public boolean bookTripById(int travelerId, int tripId) {
        // Carica il viaggiatore e il viaggio dal database
        Traveler traveler = null;
        Trip trip = null;

        if (travelerDAO != null) {
            traveler = travelerDAO.findById(travelerId);
        }

        if (tripDAO != null) {
            trip = tripDAO.findById(tripId);
        }

        if (traveler != null && trip != null) {
            return bookTrip(traveler, trip);
        }

        return false;
    }

    /**
     * Annulla una prenotazione
     * @param traveler il viaggiatore
     * @param trip il viaggio
     * @return true se la cancellazione è avvenuta con successo, false altrimenti
     */
    public boolean cancelBooking(Traveler traveler, Trip trip) {
        BookingRegister register = trip.getBookingRegister();
        Booking booking = register.getBookingByTraveler(traveler);
        if (booking != null) {
            register.removeBooking(booking);
            bookingDAO.delete(booking);

            // Notifica il viaggiatore
            if (notificationService != null) {
                notificationService.sendNotification(traveler,
                    "La tua prenotazione per il viaggio \"" + trip.getTitle() + "\" è stata cancellata.");
            }

            return true;
        }
        return false;
    }

    /**
     * Annulla una prenotazione utilizzando gli ID
     * @param travelerId l'ID del viaggiatore
     * @param tripId l'ID del viaggio
     * @return true se la cancellazione è avvenuta con successo, false altrimenti
     */
    public boolean cancelBookingById(int travelerId, int tripId) {
        // Carica il viaggiatore e il viaggio dal database
        Traveler traveler = null;
        Trip trip = null;

        if (travelerDAO != null) {
            traveler = travelerDAO.findById(travelerId);
        }

        if (tripDAO != null) {
            trip = tripDAO.findById(tripId);
        }

        if (traveler != null && trip != null) {
            return cancelBooking(traveler, trip);
        }

        return false;
    }

    public List<Booking> getBookingsForTrip(Trip trip) {
        return trip.getBookingRegister().getBookings();
    }

    /**
     * Carica le prenotazioni per un viaggio dal database
     * @param trip il viaggio di cui caricare le prenotazioni
     * @return la lista delle prenotazioni caricate
     */
    public List<Booking> loadBookingsForTrip(Trip trip) {
        bookingDAO.loadBookingsForTrip(trip);
        return trip.getBookingRegister().getBookings();
    }

    /**
     * Carica le prenotazioni per un viaggiatore dal database
     * @param traveler il viaggiatore di cui caricare le prenotazioni
     * @return la lista delle prenotazioni caricate
     */
    public List<Booking> getBookingsForTraveler(Traveler traveler) {
        return bookingDAO.getByTraveler(traveler);
    }

    /**
     * Ottiene una prenotazione specifica tramite l'ID
     * @param bookingId l'ID della prenotazione
     * @return la prenotazione trovata o null
     */
    public Booking getBookingById(int bookingId) {
        return bookingDAO.getById(bookingId);
    }

    /**
     * Verifica se un viaggio ha raggiunto il numero minimo di partecipanti
     * @param trip il viaggio da verificare
     * @return true se il viaggio ha almeno il numero minimo di partecipanti
     */
    public boolean hasMinimumParticipants(Trip trip) {
        // Se il registro non è caricato, caricalo prima
        if (trip.getBookingRegister().getBookings().isEmpty()) {
            loadBookingsForTrip(trip);
        }

        return trip.getBookingRegister().hasMinimumTravelers();
    }

    /**
     * Verifica se un viaggio è completo (ha raggiunto il numero massimo di partecipanti)
     * @param trip il viaggio da verificare
     * @return true se il viaggio è al completo
     */
    public boolean isTripFull(Trip trip) {
        // Se il registro non è caricato, caricalo prima
        if (trip.getBookingRegister().getBookings().isEmpty()) {
            loadBookingsForTrip(trip);
        }

        return trip.getBookingRegister().getAvailableSpots() == 0;
    }

    /**
     * Ottiene il numero di posti disponibili per un viaggio
     * @param trip il viaggio da verificare
     * @return il numero di posti ancora disponibili
     */
    public int getAvailableSpotsForTrip(Trip trip) {
        // Se il registro non è caricato, caricalo prima
        if (trip.getBookingRegister().getBookings().isEmpty()) {
            loadBookingsForTrip(trip);
        }

        return trip.getBookingRegister().getAvailableSpots();
    }

    /**
     * Ottiene tutti i viaggi prenotati da un viaggiatore
     * @param traveler il viaggiatore
     * @return la lista dei viaggi prenotati
     */
    public List<Trip> getTripsBookedByTraveler(Traveler traveler) {
        List<Booking> bookings = bookingDAO.getByTraveler(traveler);
        List<Trip> trips = new ArrayList<>();

        for (Booking booking : bookings) {
            if (booking.getTrip() == null && tripDAO != null) {
                // Se il viaggio non è caricato, caricalo
                Trip trip = tripDAO.findById(booking.getTripId());
                booking.setTrip(trip);
            }

            if (booking.getTrip() != null) {
                trips.add(booking.getTrip());
            }
        }

        return trips;
    }
}
