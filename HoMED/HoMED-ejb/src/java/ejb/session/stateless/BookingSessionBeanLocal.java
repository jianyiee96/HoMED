/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Booking;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.AttachFormInstancesException;
import util.exceptions.CancelBookingException;
import util.exceptions.CreateBookingException;
import util.exceptions.MarkBookingAbsentException;
import util.exceptions.MarkBookingAttendanceException;
import util.exceptions.UpdateBookingCommentException;

@Local
public interface BookingSessionBeanLocal {

    public Booking createBooking(Long servicemanId, Long consultationPurposeId, Long bookingSlotId, String bookingComment) throws CreateBookingException;
    
    public void cancelBooking(Long bookingId, String cancellationComment) throws CancelBookingException;

    public void markBookingAbsent(Long bookingId) throws MarkBookingAbsentException;
    
    public List<Booking> retrieveServicemanBookings(Long servicemanId);

    public void markBookingAttendance(Long bookingId) throws MarkBookingAttendanceException;
    
    public Booking retrieveBookingById(Long bookingId);

    public List<Booking> retrieveAllBookings();
    
    public List<Booking> retrieveAllUpcomingBookings();

    public Booking createBookingByClerk(Long servicemanId, Long consultationPurposeId, Long bookingSlotId, List<Long> additionalFormTemplateIds, String bookingComment) throws CreateBookingException;

    public Booking attachFormInstancesByClerk(Long bookingSlotId, List<Long> additionalFormTemplateIds) throws AttachFormInstancesException;

    public void cancelBookingByClerk(Long bookingId, String cancellationComment) throws CancelBookingException;
    
    public void updateBookingComment(Long bookingId, String bookingComment) throws UpdateBookingCommentException;
    
}
