package model.booking;

import model.user.Traveler;

import java.util.ArrayList;
import java.util.List;

public class BookingRegister {
    private List<Booking> bookings = new ArrayList<>();
    private List<Integer> bookingIds = new ArrayList<>(); // Lista degli ID per la persistenza
    private int minTrav;
    private int maxTrav;

    // Constructor for reconstruction from database
    public BookingRegister(List<Booking> bookings, int minTrav, int maxTrav) {
        this.bookings = bookings;
        this.minTrav = minTrav;
        this.maxTrav = maxTrav;

        // Estrai gli ID dalle prenotazioni
        this.bookingIds = new ArrayList<>();
        if (bookings != null) {
            for (Booking booking : bookings) {
                if (booking.getBookingId() > 0) {
                    this.bookingIds.add(booking.getBookingId());
                }
            }
        }
    }

    public BookingRegister(int minTrav, int maxTrav) {
        this.minTrav = minTrav;
        this.maxTrav = maxTrav;
        this.bookingIds = new ArrayList<>();
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;

        // Aggiorna anche la lista degli ID
        this.bookingIds.clear();
        if (bookings != null) {
            for (Booking booking : bookings) {
                if (booking.getBookingId() > 0) {
                    this.bookingIds.add(booking.getBookingId());
                }
            }
        }
    }

    public List<Integer> getBookingIds() {
        return bookingIds;
    }

    public void setBookingIds(List<Integer> bookingIds) {
        this.bookingIds = bookingIds;
    }

    public void addBookingId(int bookingId) {
        if (!this.bookingIds.contains(bookingId)) {
            this.bookingIds.add(bookingId);
        }
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
        if (!bookings.contains(booking)) {
            bookings.add(booking);

            // Aggiorna anche la lista degli ID
            if (booking.getBookingId() > 0) {
                addBookingId(booking.getBookingId());
            }
        }
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);

        // Aggiorna anche la lista degli ID
        if (booking.getBookingId() > 0) {
            bookingIds.remove(Integer.valueOf(booking.getBookingId()));
        }
    }

    public int getAvailableSpots() {
        return maxTrav - bookings.size();
    }

    public boolean canAddMoreTravelers() {
        return bookings.size() < maxTrav;
    }

    public boolean hasMinimumTravelers() {
        return bookings.size() >= minTrav;
    }

    public boolean hasBooking(Traveler traveler) {
        for (Booking booking : bookings) {
            if (booking.getTraveler() != null && booking.getTraveler().equals(traveler)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBookingById(int travelerId) {
        for (Booking booking : bookings) {
            if (booking.getTravelerId() == travelerId) {
                return true;
            }
        }
        return false;
    }

    public Booking getBookingByTraveler(Traveler traveler) {
        for (Booking booking : bookings) {
            if (booking.getTraveler() != null && booking.getTraveler().equals(traveler)) {
                return booking;
            }
        }
        return null;
    }

    public Booking getBookingById(int bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                return booking;
            }
        }
        return null;
    }

    public Booking getBookingByTravelerId(int travelerId) {
        for (Booking booking : bookings) {
            if (booking.getTravelerId() == travelerId) {
                return booking;
            }
        }
        return null;
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public int getBookingsCount() {
        return bookings.size();
    }

    public List<Booking> getConfirmedBookings() {
        List<Booking> confirmed = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.isConfirmed()) {
                confirmed.add(booking);
            }
        }
        return confirmed;
    }

    public List<Booking> getCanceledBookings() {
        List<Booking> canceled = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.isCanceled()) {
                canceled.add(booking);
            }
        }
        return canceled;
    }
}
