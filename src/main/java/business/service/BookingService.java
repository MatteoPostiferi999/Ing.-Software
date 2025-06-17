package business.service;
import dao.interfaces.BookingDAO;

import model.trip.Trip;
import model.user.Traveler;
import model.booking.Booking;
import model.booking.BookingRegister;

import java.util.List;

public class BookingService {

    private BookingDAO bookingDAO;

    public BookingService(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }

    public boolean bookTrip(Traveler traveler, Trip trip) {
        BookingRegister register = trip.getBookingRegister();
        if (register.getAvailableSpots() > 0 && !register.hasBooking(traveler)) {
            Booking booking = new Booking(traveler, trip);
            register.addBooking(booking);
            bookingDAO.save(booking);
            return true;
        }
        return false;
    }

    public boolean cancelBooking(Traveler traveler, Trip trip) {
        BookingRegister register = trip.getBookingRegister();
        Booking booking = register.getBookingByTraveler(traveler);
        if (booking != null) {
            register.removeBooking(booking);
            bookingDAO.delete(booking);
            return true;
        }
        return false;
    }

    public List<Booking> getBookingsForTrip(Trip trip) {
        return trip.getBookingRegister().getBookings();
    }
}
