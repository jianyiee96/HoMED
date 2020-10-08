/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.BookingSlot;
import entity.MedicalCentre;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exceptions.MedicalCentreNotFoundException;
import util.exceptions.ScheduleBookingSlotException;

@Stateless
public class SlotSessionBean implements SlotSessionBeanLocal {

    @EJB
    MedicalCentreSessionBeanLocal medicalCentreSessionbeanLocal;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    @Override
    public List<BookingSlot> createBookingSlots(Long medicalCentreId, Date rangeStart, Date rangeEnd) throws ScheduleBookingSlotException {

        try {
            MedicalCentre mc = medicalCentreSessionbeanLocal.retrieveMedicalCentreById(medicalCentreId);

//            rangeStart = floorDate15Minute(rangeStart); //For testing.
//            rangeEnd = ceilDate15Minute(rangeEnd);      //For testing.
            
            rangeStart = roundDate15Minute(rangeStart);
            rangeEnd = roundDate15Minute(rangeEnd);

            if (!rangeStart.before(rangeEnd)) {
                throw new ScheduleBookingSlotException("Invalid Date Range: start not before end");
            }
            Calendar rangeStartCalendar = Calendar.getInstance();
            rangeStartCalendar.setTime(rangeStart);
            Calendar rangeEndCalendar = Calendar.getInstance();
            rangeEndCalendar.setTime(rangeEnd);

//            rangeStartCalendar.add(Calendar.MINUTE, -150); // For testing: mass populate slots.
            
            List<BookingSlot> createdBookingSlots = new ArrayList<>();

            while (rangeStartCalendar.before(rangeEndCalendar)) {
                Date currStart = rangeStartCalendar.getTime();
                rangeStartCalendar.add(Calendar.MINUTE, 15);
                Date currEnd = rangeStartCalendar.getTime();

                BookingSlot bs = new BookingSlot(mc, currStart, currEnd);
                mc.getBookingSlots().add(bs);
                em.persist(bs);
                em.flush();
                createdBookingSlots.add(bs);
            }
            
            return createdBookingSlots;

        } catch (MedicalCentreNotFoundException ex) {
            throw new ScheduleBookingSlotException("Invalid Medical Centre Id");
        }

    }

    // Helper functions.
    public boolean isSameDay(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
                && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);

    }

    public Date roundDate15Minute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int minutes = calendar.get(Calendar.MINUTE);
        int mod = (minutes % 15);
        minutes += mod < 8 ? (-mod) : (15-mod);

        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public Date floorDate15Minute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int minutes = calendar.get(Calendar.MINUTE);
        minutes = minutes - (minutes % 15);

        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public Date ceilDate15Minute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int minutes = calendar.get(Calendar.MINUTE);
        minutes = minutes - (minutes % 15) + 15;

        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}
