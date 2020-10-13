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

@Local
public interface BookingSessionBeanLocal {

    public Booking createBooking(Long servicemanId, Long consultationPurposeId, Long bookingSlotId) throws CreateBookingException;

    public void cancelBooking(Long bookingId) throws CancelBookingException;

    public List<Booking> retrieveServicemanBookings(Long servicemanId);

    public Booking retrieveBookingById(Long bookingId);

}
