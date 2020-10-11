/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

import entity.Booking;
import java.util.List;

/**
 *
 * @author User
 */
public class RetrieveBookingsRsp {
    
    List<Booking> bookings;

    public RetrieveBookingsRsp() {
    }

    public RetrieveBookingsRsp(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

}
