/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.BookingSlot;
import entity.ConsultationPurpose;
import entity.MedicalBoardSlot;
import entity.MedicalCentre;
import entity.Serviceman;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exceptions.CreateBookingException;
import util.exceptions.MedicalCentreNotFoundException;
import util.exceptions.RemoveSlotException;
import util.exceptions.ScheduleBookingSlotException;
import util.exceptions.ServicemanNotFoundException;

@Stateless
public class SlotSessionBean implements SlotSessionBeanLocal {

    @EJB(name = "BookingSessionBeanLocal")
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    @EJB(name = "ConsultationPurposeSessionBeanLocal")
    private ConsultationPurposeSessionBeanLocal consultationPurposeSessionBeanLocal;

    @EJB(name = "ServicemanSessionBeanLocal")
    private ServicemanSessionBeanLocal servicemanSessionBeanLocal;

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

//            rangeEndCalendar.add(Calendar.MINUTE, 150); // For testing: mass populate slots.
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

            System.out.println("Created " + createdBookingSlots.size());
            return createdBookingSlots;

        } catch (MedicalCentreNotFoundException ex) {
            throw new ScheduleBookingSlotException("Invalid Medical Centre Id");
        }

    }

    @Override
    public void createBookingSlotsDataInit(Long medicalCentreId, Date date, Long servicemanId, Long consultationPurposeId) throws ScheduleBookingSlotException {

        try {
            MedicalCentre mc = medicalCentreSessionbeanLocal.retrieveMedicalCentreById(medicalCentreId);
            Serviceman serviceman = servicemanSessionBeanLocal.retrieveServicemanById(servicemanId);
            ConsultationPurpose consultationPurpose = consultationPurposeSessionBeanLocal.retrieveConsultationPurpose(consultationPurposeId);

            Calendar rangeStartCalendar = Calendar.getInstance();
            rangeStartCalendar.setTime(date);
            rangeStartCalendar.set(Calendar.HOUR_OF_DAY, 8);
            rangeStartCalendar.set(Calendar.MINUTE, 0);
            rangeStartCalendar.set(Calendar.SECOND, 0);
            rangeStartCalendar.set(Calendar.MILLISECOND, 0);

            Calendar rangeEndCalendar = Calendar.getInstance();
            rangeEndCalendar.setTime(date);
            rangeEndCalendar.set(Calendar.HOUR_OF_DAY, 18);
            rangeEndCalendar.set(Calendar.MINUTE, 0);
            rangeEndCalendar.set(Calendar.SECOND, 0);
            rangeEndCalendar.set(Calendar.MILLISECOND, 0);

            while (rangeStartCalendar.before(rangeEndCalendar)) {
                Date currStart = rangeStartCalendar.getTime();
                rangeStartCalendar.add(Calendar.MINUTE, 15);
                Date currEnd = rangeStartCalendar.getTime();
                System.out.println("Booking Slot Created: Start[" + currStart + "+] End[" + currEnd + "]");
                BookingSlot bs = new BookingSlot(mc, currStart, currEnd);
                mc.getBookingSlots().add(bs);
                em.persist(bs);
                em.flush();
            }

            rangeStartCalendar.add(Calendar.DATE, 1);
            rangeStartCalendar.set(Calendar.HOUR_OF_DAY, 8);
            rangeStartCalendar.set(Calendar.MINUTE, 0);
            rangeStartCalendar.set(Calendar.SECOND, 0);
            rangeStartCalendar.set(Calendar.MILLISECOND, 0);

            rangeEndCalendar.add(Calendar.DATE, 1);
            rangeEndCalendar.set(Calendar.HOUR_OF_DAY, 18);
            rangeEndCalendar.set(Calendar.MINUTE, 0);
            rangeEndCalendar.set(Calendar.SECOND, 0);
            rangeEndCalendar.set(Calendar.MILLISECOND, 0);

            while (rangeStartCalendar.before(rangeEndCalendar)) {
                Date currStart = rangeStartCalendar.getTime();
                rangeStartCalendar.add(Calendar.MINUTE, 15);
                Date currEnd = rangeStartCalendar.getTime();
                System.out.println("Booking Slot Created: Start[" + currStart + "+] End[" + currEnd + "]");
                BookingSlot bs = new BookingSlot(mc, currStart, currEnd);
                mc.getBookingSlots().add(bs);
                em.persist(bs);
                em.flush();
                if (Math.random() < 0.2) {
                    try {
                        bookingSessionBeanLocal.createBooking(servicemanId, consultationPurposeId, bs.getSlotId());
                        System.out.println("Booking Created: Start[" + currStart + "+] End[" + currEnd + "]");
                    } catch (CreateBookingException ex) {
                        System.out.println("Failed to create booking slot for serviceman");
                    }
                }
            }

        } catch (MedicalCentreNotFoundException ex) {
            throw new ScheduleBookingSlotException("Invalid Medical Centre Id");
        } catch (ServicemanNotFoundException ex) {
            throw new ScheduleBookingSlotException("Invalid Serviceman Id");
        }

    }

    @Override
    public void createMedicalBoardSlotsDataInit(Date date) {
        Calendar rangeStartCalendar = Calendar.getInstance();
        rangeStartCalendar.setTime(date);
        rangeStartCalendar.set(Calendar.HOUR_OF_DAY, 8);
        rangeStartCalendar.set(Calendar.MINUTE, 0);
        rangeStartCalendar.set(Calendar.SECOND, 0);
        rangeStartCalendar.set(Calendar.MILLISECOND, 0);

        Calendar rangeEndCalendar = Calendar.getInstance();
        rangeEndCalendar.setTime(date);
        rangeEndCalendar.set(Calendar.HOUR_OF_DAY, 18);
        rangeEndCalendar.set(Calendar.MINUTE, 0);
        rangeEndCalendar.set(Calendar.SECOND, 0);
        rangeEndCalendar.set(Calendar.MILLISECOND, 0);

        while (rangeStartCalendar.before(rangeEndCalendar)) {
            Date currStart = rangeStartCalendar.getTime();
            rangeStartCalendar.add(Calendar.MINUTE, 30);
            Date currEnd = rangeStartCalendar.getTime();
            System.out.println("Medical Board Slot Created: Start[" + currStart + "+] End[" + currEnd + "]");
            MedicalBoardSlot mbs = new MedicalBoardSlot(currStart, currEnd);
            em.persist(mbs);
            em.flush();
        }

        rangeStartCalendar.add(Calendar.DATE, 1);
        rangeStartCalendar.set(Calendar.HOUR_OF_DAY, 8);
        rangeStartCalendar.set(Calendar.MINUTE, 0);
        rangeStartCalendar.set(Calendar.SECOND, 0);
        rangeStartCalendar.set(Calendar.MILLISECOND, 0);

        rangeEndCalendar.add(Calendar.DATE, 1);
        rangeEndCalendar.set(Calendar.HOUR_OF_DAY, 18);
        rangeEndCalendar.set(Calendar.MINUTE, 0);
        rangeEndCalendar.set(Calendar.SECOND, 0);
        rangeEndCalendar.set(Calendar.MILLISECOND, 0);

        while (rangeStartCalendar.before(rangeEndCalendar)) {
            Date currStart = rangeStartCalendar.getTime();
            rangeStartCalendar.add(Calendar.MINUTE, 30);
            Date currEnd = rangeStartCalendar.getTime();
            System.out.println("Medical Board Slot Created: Start[" + currStart + "+] End[" + currEnd + "]");
            MedicalBoardSlot mbs = new MedicalBoardSlot(currStart, currEnd);
            em.persist(mbs);
            em.flush();
        }
    }

    public List<MedicalBoardSlot> createMedicalBoardSlots(Date rangeStart, Date rangeEnd) throws ScheduleBookingSlotException {

        rangeStart = roundDate30Minute(rangeStart);
        rangeEnd = roundDate30Minute(rangeEnd);

        if (!rangeStart.before(rangeEnd)) {
            throw new ScheduleBookingSlotException("Invalid Date Range: start not before end");
        }
        Calendar rangeStartCalendar = Calendar.getInstance();
        rangeStartCalendar.setTime(rangeStart);
        Calendar rangeEndCalendar = Calendar.getInstance();
        rangeEndCalendar.setTime(rangeEnd);

        List<MedicalBoardSlot> createdMedicalBoardSlots = new ArrayList<>();

        while (rangeStartCalendar.before(rangeEndCalendar)) {
            Date currStart = rangeStartCalendar.getTime();
            rangeStartCalendar.add(Calendar.MINUTE, 30);
            Date currEnd = rangeStartCalendar.getTime();

            MedicalBoardSlot mbs = new MedicalBoardSlot(currStart, currEnd);
            em.persist(mbs);
            em.flush();
            createdMedicalBoardSlots.add(mbs);
        }

        return createdMedicalBoardSlots;
    }

    @Override
    public List<MedicalBoardSlot> retrieveMedicalBoardSlots() {
        Query query = em.createQuery("SELECT mb FROM MedicalBoardSlot mb");
        return query.getResultList();
    }

    @Override
    public List<BookingSlot> retrieveBookingSlotsByMedicalCentre(Long medicalCentreId) {
        Query query = em.createQuery("SELECT b FROM BookingSlot b WHERE b.medicalCentre.medicalCentreId = :id ");
        query.setParameter("id", medicalCentreId);
        return query.getResultList();
    }

    @Override
    public List<BookingSlot> retrieveBookingSlotsWithBookingsByMedicalCentre(Long medicalCentreId) {
        Query query = em.createQuery("SELECT b FROM BookingSlot b WHERE b.medicalCentre.medicalCentreId = :id AND b.booking IS NOT NULL");
        query.setParameter("id", medicalCentreId);
        return query.getResultList();
    }

    @Override
    public List<BookingSlot> retrieveMedicalCentreBookingSlotsByDate(Long medicalCentreId, Date date) {

        List<BookingSlot> bookingSlots = retrieveBookingSlotsByMedicalCentre(medicalCentreId);
        List<BookingSlot> filteredBookingSlot = new ArrayList<>();

        for (BookingSlot b : bookingSlots) {
            if (isSameDay(date, b.getStartDateTime())) {
                filteredBookingSlot.add(b);
            }
        }

        return filteredBookingSlot;
    }

    @Override
    public void removeBookingSlot(Long bookingSlotId) throws RemoveSlotException {
        BookingSlot bookingSlot = retrieveBookingSlotById(bookingSlotId);
        if (bookingSlot.getBooking() == null) {
            em.remove(bookingSlot);
            bookingSlot.getMedicalCentre().getBookingSlots().remove(bookingSlot);
        } else {
            throw new RemoveSlotException("Unable to remove BookingSlot: Booking Exist");
        }
    }

    @Override
    public BookingSlot retrieveBookingSlotById(Long id) {
        BookingSlot bookingSlot = em.find(BookingSlot.class,
                id);
        return bookingSlot;
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
        minutes += mod < 8 ? (-mod) : (15 - mod);

        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public Date roundDate30Minute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int minutes = calendar.get(Calendar.MINUTE);
        int mod = (minutes % 30);
        minutes += mod < 15 ? (-mod) : (30 - mod);

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
