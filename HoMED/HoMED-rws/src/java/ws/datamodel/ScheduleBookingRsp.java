/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

/**
 *
 * @author sunag
 */
public class ScheduleBookingRsp {
    Long bookingId;

    public ScheduleBookingRsp() {
    }

    public ScheduleBookingRsp(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }
    
    
            
}
