package business.service;

import dao.interfaces.BookingDAO;
import core.model.Booking;
import core.model.BookingRegister;
import core.model.Traveler;
import core.model.Trip;

public class BookingService {
    private final BookingRegister bookingRegister;
    private final BookingDAO bookingDAO;

    public BookingService(BookingRegister register, BookingDAO dao) {
        this.bookingRegister = register;
        this.bookingDAO = dao;
    }

    // Metodo per aggiungere una prenotazione
    public void bookTrip(int bookingId, Traveler traveler, Trip trip) {
        Booking booking = new Booking(bookingId, traveler, trip);
        bookingRegister.addBooking(booking); // gestione runtime
        bookingDAO.save(booking);            // persistenza DB
    }

    // Metodo per rimuovere una prenotazione
    public void cancelBooking(Booking booking) {
        bookingRegister.removeBooking(booking); // aggiornamento in-app
        bookingDAO.delete(booking);             // rimozione dal DB
    }
}
