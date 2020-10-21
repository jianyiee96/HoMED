/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

/**
 *
 * @author User
 */
public class CancelBookingReq {
    
    Long bookingId;
    String cancellationComment;

    public CancelBookingReq() {
    }

    public CancelBookingReq(Long bookingId, String cancellationComment) {
        this.bookingId = bookingId;
        this.cancellationComment = cancellationComment;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getCancellationComment() {
        return cancellationComment;
    }

    public void setCancellationComment(String cancellationComment) {
        this.cancellationComment = cancellationComment;
    }

}
