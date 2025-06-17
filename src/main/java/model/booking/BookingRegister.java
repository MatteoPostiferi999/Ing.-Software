package model.booking;

import model.user.Traveler;

import java.util.ArrayList;
import java.util.List;

public class BookingRegister {
    private List<Booking> bookings = new ArrayList<>();
    private int minTrav;
    private int maxTrav;
    //private int tripId;

    // Constructor for reconstruction from database
    public BookingRegister(List<Booking> bookings, int minTrav, int maxTrav) {
        this.bookings = bookings;
        this.minTrav = minTrav;
        this.maxTrav = maxTrav;
    }

    public BookingRegister(int minTrav, int maxTrav) {
        this.minTrav = minTrav;
        this.maxTrav = maxTrav;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public int getMinTrav() {
        return minTrav;
    }

    public void setMinTrav(int minTrav) {
        this.minTrav = minTrav;
    }

    public int getMaxTrav() {
        return maxTrav;
    }

    public void setMaxTrav(int maxTrav) {
        this.maxTrav = maxTrav;
    }


    public void addBooking(Booking booking) {
        bookings.add(booking);
    }


    public void removeBooking(Booking booking) {
        bookings.remove(booking);
    }

    public int getAvailableSpots() {
        return maxTrav - bookings.size();
    }

    public boolean hasBooking(Traveler traveler) {
        for (Booking booking : bookings) {
            if (booking.getTraveler().equals(traveler)) {
                return true;
            }
        }
        return false;
    }

    public Booking getBookingByTraveler(Traveler traveler) {
    for (Booking booking : bookings) {
        if (booking.getTraveler().equals(traveler)) {
            return booking;
        }
    }
    return null;
    }
}
