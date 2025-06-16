package core.model;

import java.util.ArrayList;
import java.util.List;

public class BookingRegister {
    private List<Booking> bookings = new ArrayList<>();
    private int minTrav;
    private int maxTrav;
    //private int tripId;

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

}
