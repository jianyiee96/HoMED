/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.BookingSlot;
import entity.MedicalBoardCase;
import entity.MedicalBoardSlot;
import entity.MedicalCentre;
import entity.Notification;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.BookingStatusEnum;
import util.enumeration.MedicalBoardCaseStatusEnum;
import util.enumeration.MedicalBoardSlotStatusEnum;
import util.enumeration.NotificationTypeEnum;
import util.exceptions.CreateNotificationException;
import util.exceptions.EndMedicalBoardSessionException;
import util.exceptions.ExpireSlotException;
import util.exceptions.MedicalCentreNotFoundException;
import util.exceptions.RemoveSlotException;
import util.exceptions.ScheduleBookingSlotException;
import util.exceptions.ScheduleMedicalBoardSlotException;
import util.exceptions.StartMedicalBoardSessionException;
import util.exceptions.UpdateMedicalBoardSlotException;
import util.security.CryptographicHelper;

@Stateless
public class SlotSessionBean implements SlotSessionBeanLocal {

    @EJB
    private MedicalCentreSessionBeanLocal medicalCentreSessionbeanLocal;

    @EJB
    private NotificationSessionBeanLocal notificationSessionBeanLocal;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    /* Booking Slots */
    @Override
    public List<BookingSlot> createBookingSlots(Long medicalCentreId, Date rangeStart, Date rangeEnd) throws ScheduleBookingSlotException {
        try {
            MedicalCentre mc = medicalCentreSessionbeanLocal.retrieveMedicalCentreById(medicalCentreId);

//            rangeStart = floorDate15Minute(rangeStart); //For testing.
//            rangeEnd = ceilDate15Minute(rangeEnd);      //For testing.
            rangeStart = roundDate15Minute(rangeStart);
            rangeEnd = roundDate15Minute(rangeEnd);

            if (!rangeStart.before(rangeEnd)) {
                throw new ScheduleBookingSlotException("Invalid Date Range: start date is not before end date");
            }
            Calendar rangeStartCalendar = Calendar.getInstance();
            rangeStartCalendar.setTime(rangeStart);
            Calendar rangeEndCalendar = Calendar.getInstance();
            rangeEndCalendar.setTime(rangeEnd);

//            rangeEndCalendar.add(Calendar.MINUTE, 150); // For testing: mass populate slots.
            List<BookingSlot> availableBookingSlots = this.retrieveBookingSlotsByMedicalCentre(medicalCentreId);
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            List<BookingSlot> createdBookingSlots = new ArrayList<>();

            while (rangeStartCalendar.before(rangeEndCalendar)) {
                Date currStart = rangeStartCalendar.getTime();
                rangeStartCalendar.add(Calendar.MINUTE, 15);
                Date currEnd = rangeStartCalendar.getTime();

                BookingSlot newBs = new BookingSlot(mc, currStart, currEnd);
                Boolean isInvalid = Boolean.FALSE;
                for (BookingSlot bs : availableBookingSlots) {
                    if (bs.getStartDateTime().equals(currStart) && bs.getEndDateTime().equals(currEnd)) {
                        // If there is no booking attached to the slot, means there is an available slot for booking, and should not create a new slot.
                        if (bs.getBooking() == null) {

                            isInvalid = Boolean.TRUE;
                            break;

                            // If there is a booking attached to the slot but the booking is not cancelled, means the slot is already booked (upcoming/absent) or past, and should not create a new slot.
                        } else if (bs.getBooking() != null && bs.getBooking().getBookingStatusEnum() != BookingStatusEnum.CANCELLED) {

                            isInvalid = Boolean.TRUE;
                            break;

                        }
                    }
                }

                if (isInvalid) {
                    continue;
                }

                mc.getBookingSlots().add(newBs);
                em.persist(newBs);
                em.flush();
                createdBookingSlots.add(newBs);
            }

//            System.out.println("Created " + createdBookingSlots.size() + " Booking Slots");
            return createdBookingSlots;

        } catch (MedicalCentreNotFoundException ex) {
            throw new ScheduleBookingSlotException("Invalid Medical Centre Id");
        }
    }

    @Override
    public List<BookingSlot> retrieveBookingSlotsByMedicalCentre(Long medicalCentreId) {
        Query query = em.createQuery("SELECT b FROM BookingSlot b WHERE b.medicalCentre.medicalCentreId = :id ");
        query.setParameter("id", medicalCentreId);
        List<BookingSlot> bookingSlots = query.getResultList();
        Collections.sort(bookingSlots);
        return bookingSlots;
    }

    @Override
    public List<BookingSlot> retrieveBookingSlotsWithBookingsByMedicalCentre(Long medicalCentreId) {
        Query query = em.createQuery("SELECT b FROM BookingSlot b WHERE b.medicalCentre.medicalCentreId = :id AND b.booking IS NOT NULL");
        query.setParameter("id", medicalCentreId);
        List<BookingSlot> bookingSlots = query.getResultList();
        Collections.sort(bookingSlots);
        return bookingSlots;
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
    public BookingSlot retrieveBookingSlotById(Long id) {
        BookingSlot bookingSlot = em.find(BookingSlot.class, id);
        return bookingSlot;
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

    /* Medical Board Slots */
    @Override
    public MedicalBoardSlot createMedicalBoardSlot(Date startDate, Date endDate) throws ScheduleMedicalBoardSlotException {

        if (!startDate.before(endDate)) {
            throw new ScheduleMedicalBoardSlotException("Invalid Date Range: start date is not before end date");
        }

        if (!isSameDay(startDate, endDate)) {
            throw new ScheduleMedicalBoardSlotException("Invalid Date Range: medical board slot is not scheduled for the same day");
        }

        if (startDate.getTime() < new Date().getTime()) {
            throw new ScheduleMedicalBoardSlotException("Invalid Date Range: scheduling of medical board slot earlier than the current time");
        }

        MedicalBoardSlot mbs = new MedicalBoardSlot(startDate, endDate);
        em.persist(mbs);
        em.flush();

        System.out.println("Created medical board slot [" + startDate + " to " + endDate + "]");

        return mbs;
    }

    @Override
    public MedicalBoardSlot createMedicalBoardSlotByInit(Date startDate, Date endDate) throws ScheduleMedicalBoardSlotException {

        if (!startDate.before(endDate)) {
            throw new ScheduleMedicalBoardSlotException("Invalid Date Range: start date is not before end date");
        }

        if (!isSameDay(startDate, endDate)) {
            throw new ScheduleMedicalBoardSlotException("Invalid Date Range: medical board slot is not scheduled for the same day");
        }

        MedicalBoardSlot mbs = new MedicalBoardSlot(startDate, endDate);
        em.persist(mbs);
        em.flush();

        System.out.println("Created medical board slot [" + startDate + " to " + endDate + "]");

        return mbs;
    }

    @Override
    public List<MedicalBoardSlot> retrieveMedicalBoardSlots() {
        Query query = em.createQuery("SELECT mb FROM MedicalBoardSlot mb ORDER BY mb.startDateTime");
        return query.getResultList();
    }

    @Override
    public MedicalBoardSlot retrieveMedicalBoardSlotById(Long medicalBoardSlotId) {
        MedicalBoardSlot medicalBoardSlot = em.find(MedicalBoardSlot.class, medicalBoardSlotId);
        medicalBoardSlot.getMedicalBoardCases().size();
        return medicalBoardSlot;
    }

    @Override
    public MedicalBoardSlot updateMedicalBoardSlot(MedicalBoardSlot medicalBoardSlot) throws UpdateMedicalBoardSlotException {
        String errorMessage = "Failed to update Medical Board Slot: ";

        if (medicalBoardSlot != null && medicalBoardSlot.getSlotId() != null) {
            MedicalBoardSlot medicalBoardSlotToUpdate = retrieveMedicalBoardSlotById(medicalBoardSlot.getSlotId());

            if (medicalBoardSlotToUpdate.getMedicalBoardCases().isEmpty()) {
                medicalBoardSlotToUpdate.setStartDateTime(medicalBoardSlot.getStartDateTime());
                medicalBoardSlotToUpdate.setEndDateTime(medicalBoardSlot.getEndDateTime());
                medicalBoardSlotToUpdate.setChairman(medicalBoardSlot.getChairman());
                medicalBoardSlotToUpdate.setMedicalOfficerOne(medicalBoardSlot.getMedicalOfficerOne());
                medicalBoardSlotToUpdate.setMedicalOfficerTwo(medicalBoardSlot.getMedicalOfficerTwo());

                if (medicalBoardSlotToUpdate.getChairman() != null && medicalBoardSlotToUpdate.getMedicalOfficerOne() != null && medicalBoardSlotToUpdate.getMedicalOfficerTwo() != null) {
                    medicalBoardSlotToUpdate.setMedicalBoardSlotStatusEnum(MedicalBoardSlotStatusEnum.ASSIGNED);
                } else {
                    medicalBoardSlotToUpdate.setMedicalBoardSlotStatusEnum(MedicalBoardSlotStatusEnum.UNASSIGNED);
                }

                em.flush();

                return medicalBoardSlotToUpdate;
            } else {
                throw new UpdateMedicalBoardSlotException(errorMessage + "Medical Board cases have been allocated!");
            }
        } else {
            throw new UpdateMedicalBoardSlotException(errorMessage + "Medical Board Slot ID not found!");
        }
    }

    @Override
    public void removeMedicalBoardSlot(Long medicalBoardSlotId) throws RemoveSlotException {
        String errorMessage = "Failed to remove Medical Board Slot: ";
        MedicalBoardSlot medicalBoardSlot = retrieveMedicalBoardSlotById(medicalBoardSlotId);

        if (medicalBoardSlot != null) {
            if (medicalBoardSlot.getChairman() != null || medicalBoardSlot.getMedicalOfficerOne() != null || medicalBoardSlot.getMedicalOfficerTwo() != null) {
                throw new RemoveSlotException(errorMessage + "Medical Board members have been assigned!");
            } else if (!medicalBoardSlot.getMedicalBoardCases().isEmpty()) {
                throw new RemoveSlotException(errorMessage + "Medical Board cases have been allocated!");
            } else {
                em.remove(medicalBoardSlot);
            }
        } else {
            throw new RemoveSlotException(errorMessage + "Medical Board slot not found!");
        }
    }

    @Override
    public void expireMedicalBoardSlot(Long medicalBoardSlotId) throws ExpireSlotException {
        String errorMessage = "Failed to expire Medical Board Slot: ";
        MedicalBoardSlot medicalBoardSlot = retrieveMedicalBoardSlotById(medicalBoardSlotId);

        if (medicalBoardSlot != null) {
            if (medicalBoardSlot.getMedicalBoardSlotStatusEnum() == MedicalBoardSlotStatusEnum.ONGOING
                    || medicalBoardSlot.getMedicalBoardSlotStatusEnum() == MedicalBoardSlotStatusEnum.COMPLETED
                    || medicalBoardSlot.getMedicalBoardSlotStatusEnum() == MedicalBoardSlotStatusEnum.EXPIRED) {
                throw new ExpireSlotException(errorMessage + "Status is invalid! :" + medicalBoardSlot.getMedicalBoardSlotStatusEnum());
            } else {
                medicalBoardSlot.setMedicalBoardSlotStatusEnum(MedicalBoardSlotStatusEnum.EXPIRED);
            }
        } else {
            throw new ExpireSlotException(errorMessage + "Medical Board slot not found!");
        }
    }

    @Override
    public void startMedicalBoardSession(Long medicalBoardSlotId) throws StartMedicalBoardSessionException {

        MedicalBoardSlot medicalBoardSlot = retrieveMedicalBoardSlotById(medicalBoardSlotId);

        if (medicalBoardSlot == null) {
            throw new StartMedicalBoardSessionException("Invalid Medical Board Slot Id");
        } else if (medicalBoardSlot.getMedicalBoardSlotStatusEnum() != MedicalBoardSlotStatusEnum.ALLOCATED) {
            throw new StartMedicalBoardSessionException("Invalid Status: Status has to be allocated");
        }

        medicalBoardSlot.setMedicalBoardSlotStatusEnum(MedicalBoardSlotStatusEnum.ONGOING);
        medicalBoardSlot.setActualStartDateTime(new Date());
        System.out.println("Session bean start session called");

    }

    @Override
    public void endMedicalBoardSession(Long medicalBoardSlotId) throws EndMedicalBoardSessionException {

        MedicalBoardSlot medicalBoardSlot = retrieveMedicalBoardSlotById(medicalBoardSlotId);

        if (medicalBoardSlot == null) {
            throw new EndMedicalBoardSessionException("Invalid Medical Board Slot Id");
        } else if (medicalBoardSlot.getMedicalBoardSlotStatusEnum() != MedicalBoardSlotStatusEnum.ONGOING) {
            throw new EndMedicalBoardSessionException("Invalid Status: Status has to be allocated");
        }

        medicalBoardSlot.setMedicalBoardSlotStatusEnum(MedicalBoardSlotStatusEnum.COMPLETED);
        medicalBoardSlot.setActualEndDateTime(new Date());

        List<MedicalBoardCase> signedCase = new ArrayList<MedicalBoardCase>();
        List<MedicalBoardCase> unsignedCase = new ArrayList<MedicalBoardCase>();

        medicalBoardSlot.getMedicalBoardCases().forEach(mbs -> {
            if (mbs.getIsSigned()) {
                signedCase.add(mbs);
            } else {
                unsignedCase.add(mbs);
            }
        });

        for (MedicalBoardCase mbc : unsignedCase) {

            mbc.getMedicalBoardSlot().getMedicalBoardCases().remove(mbc);
            mbc.setMedicalBoardSlot(null);
            mbc.setMedicalBoardCaseStatus(MedicalBoardCaseStatusEnum.WAITING);
            Notification n = new Notification("Medical Board Case", "You medical board case have not been reviewed by the board. The case has been placed back into the waiting list.", NotificationTypeEnum.MEDICAL_BOARD, mbc.getMedicalBoardCaseId());
            try {
                notificationSessionBeanLocal.createNewNotification(n, mbc.getConsultation().getBooking().getServiceman().getServicemanId(), Boolean.FALSE);
                notificationSessionBeanLocal.sendPushNotification("Medical Board Case", "You medical board case have not been reviewed by the board. The case has been placed back into the waiting list.", mbc.getConsultation().getBooking().getServiceman().getFcmToken());
            } catch (CreateNotificationException ex) {
                System.out.println("Error in creating notification " + ex);
            }
        }

        for (MedicalBoardCase mbc : signedCase) {
            mbc.setMedicalBoardCaseStatus(MedicalBoardCaseStatusEnum.COMPLETED);
            Notification n = new Notification("Medical Board Case", "You medical board case has been reviewed by the board. You may view the medical board results now.", NotificationTypeEnum.MEDICAL_BOARD, mbc.getMedicalBoardCaseId());
            try {
                notificationSessionBeanLocal.createNewNotification(n, mbc.getConsultation().getBooking().getServiceman().getServicemanId(), Boolean.FALSE);
                notificationSessionBeanLocal.sendPushNotification("Medical Board Case", "You medical board case has been reviewed by the board. You may view the medical board results now.", mbc.getConsultation().getBooking().getServiceman().getFcmToken());
            } catch (CreateNotificationException ex) {
                System.out.println("Error in creating notification " + ex);
            }
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
