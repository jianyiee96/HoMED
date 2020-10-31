/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.BookingSlot;
import entity.MedicalBoardSlot;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.RemoveSlotException;
import util.exceptions.ScheduleBookingSlotException;
import util.exceptions.ScheduleMedicalBoardSlotException;

@Local
public interface SlotSessionBeanLocal {

    public List<BookingSlot> createBookingSlots(Long medicalCentreId, Date rangeStart, Date rangeEnd) throws ScheduleBookingSlotException;

    public List<BookingSlot> retrieveBookingSlotsByMedicalCentre(Long medicalCentreId);

    public List<BookingSlot> retrieveMedicalCentreBookingSlotsByDate(Long medicalCentreId, Date date);

    public void removeBookingSlot(Long bookingSlotId) throws RemoveSlotException;

    public BookingSlot retrieveBookingSlotById(Long id);

    public List<BookingSlot> retrieveBookingSlotsWithBookingsByMedicalCentre(Long medicalCentreId);

    public MedicalBoardSlot createMedicalBoardSlot(Date startDate, Date endDate) throws ScheduleMedicalBoardSlotException;

    public List<MedicalBoardSlot> retrieveMedicalBoardSlots();
    
    public MedicalBoardSlot retrieveMedicalBoardSlotById(Long medicalBoardSlotId);

    public void removeMedicalBoardSlot(Long medicalBoardSlotId) throws RemoveSlotException;


}
