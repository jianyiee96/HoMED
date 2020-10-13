/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Booking;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.CancelBookingException;
import util.exceptions.CreateBookingException;
import util.exceptions.MarkBookingAbsentException;
import util.exceptions.MarkBookingAttendanceException;

@Local
public interface BookingSessionBeanLocal {

    public Booking createBooking(Long servicemanId, Long consultationPurposeId, Long bookingSlotId) throws CreateBookingException;

    public Booking createBookingByInit(Long servicemanId, Long consultationPurposeId, Long bookingSlotId);
    
    public void cancelBooking(Long bookingId) throws CancelBookingException;

    public void markBookingAbsent(Long bookingId) throws MarkBookingAbsentException;
    
    public List<Booking> retrieveServicemanBookings(Long servicemanId);

    public void markBookingAttendance(Long bookingId) throws MarkBookingAttendanceException;
    
    public Booking retrieveBookingById(Long bookingId);

    public List<Booking> retrieveAllBookings();
    
    public List<Booking> retrieveAllUpcomingBookings();
    
}
