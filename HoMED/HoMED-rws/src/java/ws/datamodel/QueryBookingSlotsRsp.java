/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

import entity.BookingSlot;
import java.util.List;

/**
 *
 * @author User
 */
public class QueryBookingSlotsRsp {
    
    List<BookingSlot> bookingSlots;

    public QueryBookingSlotsRsp() {
    }

    public QueryBookingSlotsRsp(List<BookingSlot> bookingSlots) {
        this.bookingSlots = bookingSlots;
    }

    public List<BookingSlot> getBookingSlots() {
        return bookingSlots;
    }

    public void setBookingSlots(List<BookingSlot> bookingSlots) {
        this.bookingSlots = bookingSlots;
    }
    
}
