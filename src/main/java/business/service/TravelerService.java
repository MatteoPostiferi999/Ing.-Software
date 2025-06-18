package business.service;

import dao.interfaces.TravelerDAO;
import dao.interfaces.BookingDAO;
import dao.interfaces.AssignmentDAO;
import model.user.Traveler;
import model.user.Guide;
import model.user.User;
import model.booking.Booking;
import model.trip.Trip;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TravelerService {
    private final TravelerDAO travelerDAO;
    private final BookingDAO bookingDAO;
    private final AssignmentDAO assignmentDAO;

    public TravelerService(TravelerDAO travelerDAO, BookingDAO bookingDAO, AssignmentDAO assignmentDAO) {
        this.travelerDAO = travelerDAO;
        this.bookingDAO = bookingDAO;
        this.assignmentDAO = assignmentDAO;
    }

    // Metodi base CRUD per Traveler
    public void addTraveler(Traveler traveler) {
        travelerDAO.save(traveler);
    }

    public Traveler getTravelerById(int id) {
        return travelerDAO.findById(id);
    }

    public void updateTraveler(Traveler traveler) {
        travelerDAO.update(traveler);
    }

    public void deleteTraveler(int id) {
        travelerDAO.delete(id);
    }

    /**
     * Ottiene tutte le prenotazioni di un viaggiatore.
     * @param traveler Il viaggiatore
     * @return Lista di prenotazioni
     */
    public List<Booking> getBookings(Traveler traveler) {
        if (traveler == null) return new ArrayList<>();
        return bookingDAO.getByTraveler(traveler);
    }

    /**
     * Verifica se un viaggiatore ha completato un viaggio specifico.
     * Un viaggio è considerato completato se:
     * - Il viaggiatore ha una prenotazione per quel viaggio
     * - La data di inizio del viaggio è passata
     *
     * @param traveler Il viaggiatore
     * @param trip Il viaggio
     * @return true se il viaggiatore ha completato il viaggio, false altrimenti
     */
    public boolean hasCompletedTrip(Traveler traveler, Trip trip) {
        if (traveler == null || trip == null) return false;

        // Verifica che il viaggio sia concluso (data passata)
        if (trip.getDate().isAfter(LocalDate.now())) {
            return false;
        }

        // Ottieni le prenotazioni del viaggiatore
        List<Booking> bookings = getBookings(traveler);

        // Cerca una prenotazione per il viaggio specificato
        for (Booking booking : bookings) {
            if (booking.getTrip().getTripId() == trip.getTripId()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Verifica se un viaggiatore ha incontrato una specifica guida.
     * Un viaggiatore ha incontrato una guida se:
     * - Il viaggiatore ha partecipato a un viaggio
     * - La guida è stata assegnata a quel viaggio
     * - La data del viaggio è passata
     *
     * @param traveler Il viaggiatore
     * @param guide La guida
     * @return true se il viaggiatore ha incontrato la guida, false altrimenti
     */
    public boolean hasMetGuide(Traveler traveler, Guide guide) {
        if (traveler == null || guide == null) return false;

        // Ottieni le prenotazioni del viaggiatore
        List<Booking> bookings = getBookings(traveler);

        // Verifica se il viaggiatore ha partecipato a un viaggio con questa guida
        for (Booking booking : bookings) {
            Trip trip = booking.getTrip();

            // Controlla che il viaggio sia concluso
            if (trip.getDate().isAfter(LocalDate.now())) {
                continue;
            }

            // Verifica se la guida era assegnata a questo viaggio
            if (trip.getAssignmentRegister().hasGuide(guide)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Ottiene tutti i viaggi futuri prenotati da un viaggiatore.
     * @param traveler Il viaggiatore
     * @return Lista di viaggi futuri
     */
    public List<Trip> getUpcomingTrips(Traveler traveler) {
        if (traveler == null) return new ArrayList<>();

        List<Trip> upcomingTrips = new ArrayList<>();
        List<Booking> bookings = getBookings(traveler);

        LocalDate today = LocalDate.now();
        for (Booking booking : bookings) {
            Trip trip = booking.getTrip();
            if (trip.getDate().isAfter(today) || trip.getDate().isEqual(today)) {
                upcomingTrips.add(trip);
            }
        }

        return upcomingTrips;
    }

    /**
     * Ottiene tutti i viaggi passati di un viaggiatore.
     * @param traveler Il viaggiatore
     * @return Lista di viaggi passati
     */
    public List<Trip> getPastTrips(Traveler traveler) {
        if (traveler == null) return new ArrayList<>();

        List<Trip> pastTrips = new ArrayList<>();
        List<Booking> bookings = getBookings(traveler);

        LocalDate today = LocalDate.now();
        for (Booking booking : bookings) {
            Trip trip = booking.getTrip();
            if (trip.getDate().isBefore(today)) {
                pastTrips.add(trip);
            }
        }

        return pastTrips;
    }

    /**
     * Verifica se un viaggiatore può cancellare una prenotazione.
     * Una prenotazione può essere cancellata se:
     * - Il viaggio non è ancora iniziato
     * - La prenotazione appartiene effettivamente al viaggiatore
     *
     * @param traveler Il viaggiatore
     * @param booking La prenotazione
     * @return true se la prenotazione può essere cancellata, false altrimenti
     */
    public boolean canCancelBooking(Traveler traveler, Booking booking) {
        if (traveler == null || booking == null) return false;

        // Verifica che la prenotazione appartenga al viaggiatore
        if (booking.getTraveler().getTravelerId() != traveler.getTravelerId()) {
            return false;
        }

        // Verifica che il viaggio non sia già iniziato
        if (booking.getTrip().getDate().isBefore(LocalDate.now())) {
            return false;
        }

        return true;
    }
}
