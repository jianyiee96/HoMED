/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.BookingSlot;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.RemoveSlotException;
import util.exceptions.ScheduleBookingSlotException;

@Local
public interface SlotSessionBeanLocal {

    public List<BookingSlot> createBookingSlots(Long medicalCentreId, Date rangeStart, Date rangeEnd) throws ScheduleBookingSlotException;

    public void createBookingSlotsDataInit(Long medicalCentreId, Date date) throws ScheduleBookingSlotException;
    
    public List<BookingSlot> retrieveBookingSlotsByMedicalCentre(Long medicalCentreId);
    
    public List<BookingSlot> retrieveMedicalCentreBookingSlotsByDate(Long medicalCentreId, Date date);
    
    public void removeBookingSlot(Long bookingSlotId) throws RemoveSlotException;
    
    public BookingSlot retrieveBookingSlotById(Long id);
    
}
