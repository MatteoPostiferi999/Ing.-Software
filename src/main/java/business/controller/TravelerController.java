package business.controller;

import business.service.BookingService;
import business.service.NotificationService;
import business.service.ReviewService;
import business.service.TravelerFilter;
import business.service.TravelerService;
import business.service.ViewTripsService;
import model.booking.Booking;
import model.review.Reviewable;
import model.trip.Trip;
import model.user.Guide;
import model.user.Traveler;

import java.time.LocalDate;
import java.util.List;

public class TravelerController {

    private final Traveler traveler;
    private final ViewTripsService viewTripsService;
    private final BookingService bookingService;
    private final NotificationService notificationService;
    private final ReviewService reviewService;
    private final TravelerService travelerService;

    public TravelerController(Traveler traveler, ViewTripsService viewTripsService, BookingService bookingService,
                              NotificationService notificationService, ReviewService reviewService,
                              TravelerService travelerService) {
        this.traveler = traveler;
        this.viewTripsService = viewTripsService;
        this.bookingService = bookingService;
        this.notificationService = notificationService;
        this.reviewService = reviewService;
        this.travelerService = travelerService;
    }

    /**
     * Visualizza i viaggi disponibili applicando i filtri specificati.
     * @param minDate Data minima di inizio viaggio
     * @param maxDate Data massima di inizio viaggio
     * @param maxPrice Prezzo massimo del viaggio
     * @return Lista dei viaggi che soddisfano i criteri
     */
    public List<Trip> viewAvailableTrips(LocalDate minDate, LocalDate maxDate, Double maxPrice) {
        viewTripsService.setStrategy(new TravelerFilter(traveler, minDate, maxDate, maxPrice));
        return viewTripsService.viewTrips();
    }

    /**
     * Visualizza i dettagli di un viaggio specifico.
     * @param tripId ID del viaggio
     * @return Il viaggio con tutti i dettagli o null se non trovato
     */
    public Trip viewTripDetails(int tripId) {
        return viewTripsService.viewTripDetails(tripId);
    }

    /**
     * Prenota un viaggio per il viaggiatore.
     * @param trip Il viaggio da prenotare
     * @return true se la prenotazione è andata a buon fine, false altrimenti
     */
    public boolean bookTrip(Trip trip) {
        try {
            bookingService.bookTrip(traveler, trip);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Restituisce tutte le prenotazioni del viaggiatore.
     * @return Lista delle prenotazioni
     */
    public List<Booking> viewBookedTrips() {
        return travelerService.getBookings(traveler);
    }

    /**
     * Cancella una prenotazione per un viaggio.
     * @param booking La prenotazione da cancellare
     * @return true se la cancellazione è andata a buon fine, false altrimenti
     */
    public boolean cancelBooking(Booking booking) {
        // Verifica che il viaggio non sia già iniziato
        if (booking.getTrip().getDate().isBefore(LocalDate.now())) {
            return false;
        }

        try {
            bookingService.cancelBooking(traveler, booking.getTrip());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Lascia una recensione per un viaggio.
     * @param trip Il viaggio da recensire
     * @param rating Valutazione (da 1 a 5)
     * @param comment Commento testuale
     * @return true se la recensione è stata aggiunta con successo
     */
    public boolean leaveReviewForTrip(Trip trip, int rating, String comment) {
        // Verifica che il viaggiatore abbia effettivamente partecipato al viaggio
        if (!travelerService.hasCompletedTrip(traveler, trip)) {
            return false;
        }

        try {
            reviewService.createAndAddReview(rating, comment, trip, traveler);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Lascia una recensione per una guida.
     * @param guide La guida da recensire
     * @param rating Valutazione (da 1 a 5)
     * @param comment Commento testuale
     * @return true se la recensione è stata aggiunta con successo
     */
    public boolean leaveReviewForGuide(Guide guide, int rating, String comment) {
        // Verifica che il viaggiatore abbia effettivamente partecipato a un viaggio con questa guida
        if (!travelerService.hasMetGuide(traveler, guide)) {
            return false;
        }

        try {
            reviewService.createAndAddReview(rating, comment, guide, traveler);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Restituisce tutte le notifiche del viaggiatore.
     * @return Lista delle notifiche
     */
    public List<model.notification.Notification> getNotifications() {
        return notificationService.getNotificationsByRecipient(traveler);
    }

    /**
     * Marca una notifica come letta.
     * @param notificationId ID della notifica
     * @return true se la notifica è stata trovata e marcata come letta, false altrimenti
     */
    public boolean markNotificationAsRead(int notificationId) {
        return notificationService.markAsReadById(notificationId);
    }

    /**
     * Marca tutte le notifiche del viaggiatore come lette.
     */
    public void markAllNotificationsAsRead() {
        notificationService.markAllAsRead(traveler);
    }
}
