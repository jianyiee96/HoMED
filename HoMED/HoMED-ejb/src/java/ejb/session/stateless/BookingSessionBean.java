/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Booking;
import entity.BookingSlot;
import entity.ConsultationPurpose;
import entity.FormInstance;
import entity.FormTemplate;
import entity.Notification;
import entity.Serviceman;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.BookingStatusEnum;
import util.enumeration.ConsultationStatusEnum;
import util.enumeration.FormInstanceStatusEnum;
import util.enumeration.NotificationTypeEnum;
import util.exceptions.AttachFormInstancesException;
import util.exceptions.CancelBookingException;
import util.exceptions.ConvertBookingException;
import util.exceptions.CreateBookingException;
import util.exceptions.CreateConsultationException;
import util.exceptions.CreateNotificationException;
import util.exceptions.DeleteFormInstanceException;
import util.exceptions.GenerateFormInstanceException;
import util.exceptions.MarkBookingAbsentException;
import util.exceptions.MarkBookingAttendanceException;
import util.exceptions.ScheduleBookingSlotException;
import util.exceptions.ServicemanNotFoundException;
import util.exceptions.UpdateBookingCommentException;

@Stateless
public class BookingSessionBean implements BookingSessionBeanLocal {

    @EJB
    private SlotSessionBeanLocal slotSessionBeanLocal;

    @EJB
    private ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    @EJB
    private ConsultationPurposeSessionBeanLocal consultationPurposeSessionBeanLocal;

    @EJB
    private FormInstanceSessionBeanLocal formInstanceSessionBeanLocal;

    @EJB
    private ConsultationSessionBeanLocal consultationSessionBeanLocal;
    
    @EJB
    private NotificationSessionBeanLocal notificationSessionBeanLocal;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    @Override
    public List<Booking> retrieveQueueBookingsByMedicalCentre(Long medicalCentreId) {
        Query query = em.createQuery("SELECT b FROM Booking b "
                + "WHERE b.bookingSlot.medicalCentre.medicalCentreId = :id "
                + "AND b.consultation IS NOT NULL and b.consultation.consultationStatusEnum != :status "
                + "ORDER BY b.consultation.joinQueueDateTime ASC");
        query.setParameter("id", medicalCentreId);
        query.setParameter("status", ConsultationStatusEnum.COMPLETED);
        return query.getResultList();
    }

    @Override
    public Booking createBooking(Long servicemanId, Long consultationPurposeId, Long bookingSlotId, String bookingComment, Boolean isForReview) throws CreateBookingException {

        try {

            Serviceman serviceman = servicemanSessionBeanLocal.retrieveServicemanById(servicemanId);
            ConsultationPurpose consultationPurpose = consultationPurposeSessionBeanLocal.retrieveConsultationPurpose(consultationPurposeId);
            BookingSlot bookingSlot = slotSessionBeanLocal.retrieveBookingSlotById(bookingSlotId);

            if (consultationPurpose == null) {
                throw new CreateBookingException("Consultation Purpose Id not valid");
            } else if (bookingSlot == null) {
                throw new CreateBookingException("Booking Slot Id not valid");
            } else if (bookingSlot.getBooking() != null) {
                throw new CreateBookingException("Booking Slot not valid: Has existing booking");
            } else if (bookingSlot.getStartDateTime().before(new Date())) {
                throw new CreateBookingException("Booking Slot not valid: Invalid Start Date");
            }

            if (bookingComment == null) {
                bookingComment = "";
            }

            Booking newBooking = new Booking(serviceman, consultationPurpose, bookingSlot, bookingComment, isForReview);

            bookingSlot.setBooking(newBooking);
            serviceman.getBookings().add(newBooking);
            consultationPurpose.getBookings().add(newBooking);

            em.persist(newBooking);
            em.flush();

            // Generate and form templates that are linked to cosultation purpose
            for (FormTemplate ft : consultationPurpose.getFormTemplates()) {
                try {
                    Long formInstanceId = formInstanceSessionBeanLocal.generateFormInstance(serviceman.getServicemanId(), ft.getFormTemplateId());
                    FormInstance fi = formInstanceSessionBeanLocal.retrieveFormInstance(formInstanceId);
                    newBooking.getFormInstances().add(fi);
                    fi.setBooking(newBooking);

                } catch (GenerateFormInstanceException ex) {
                    continue;
                }
            }

            Notification n = new Notification("Booking Created Successfully", "Your booking with Booking Id[" + newBooking.getBookingId() + "]has been created successfully.", NotificationTypeEnum.BOOKING, newBooking.getBookingId());
            notificationSessionBeanLocal.createNewNotification(n, serviceman.getServicemanId(), true);
            return newBooking;

        } catch (ServicemanNotFoundException | CreateNotificationException ex) {
            throw new CreateBookingException("Serviceman Id not valid");
        }

    }

    @Override
    // TO SUPPORT INIT CREATE OF HISTORICAL DATA
    public Booking createBookingByInit(Long servicemanId, Long consultationPurposeId, Long bookingSlotId, String bookingComment, Boolean isForReview) throws CreateBookingException {

        try {

            Serviceman serviceman = servicemanSessionBeanLocal.retrieveServicemanById(servicemanId);
            ConsultationPurpose consultationPurpose = consultationPurposeSessionBeanLocal.retrieveConsultationPurpose(consultationPurposeId);
            BookingSlot bookingSlot = slotSessionBeanLocal.retrieveBookingSlotById(bookingSlotId);

            if (consultationPurpose == null) {
                throw new CreateBookingException("Consultation Purpose Id not valid");
            } else if (bookingSlot == null) {
                throw new CreateBookingException("Booking Slot Id not valid");
            } else if (bookingSlot.getBooking() != null) {
                throw new CreateBookingException("Booking Slot not valid: Has existing booking");
            }
//            The DIFFERENCE        
//            else if (bookingSlot.getStartDateTime().before(new Date())) {
//                throw new CreateBookingException("Booking Slot not valid: Invalid Start Date");
//            }

            if (bookingComment == null) {
                bookingComment = "";
            }

            Booking newBooking = new Booking(serviceman, consultationPurpose, bookingSlot, bookingComment, isForReview);

            bookingSlot.setBooking(newBooking);
            serviceman.getBookings().add(newBooking);
            consultationPurpose.getBookings().add(newBooking);

            em.persist(newBooking);
            em.flush();

            // Generate and form templates that are linked to cosultation purpose
            for (FormTemplate ft : consultationPurpose.getFormTemplates()) {
                try {
                    Long formInstanceId = formInstanceSessionBeanLocal.generateFormInstance(serviceman.getServicemanId(), ft.getFormTemplateId());
                    FormInstance fi = formInstanceSessionBeanLocal.retrieveFormInstance(formInstanceId);
                    newBooking.getFormInstances().add(fi);
                    fi.setBooking(newBooking);

                } catch (GenerateFormInstanceException ex) {
                    continue;
                }
            }

            // Notification module can fire here
            return newBooking;

        } catch (ServicemanNotFoundException ex) {
            throw new CreateBookingException("Serviceman Id not valid");
        }

    }

    @Override
    public Booking createBookingByClerk(Long servicemanId, Long consultationPurposeId, Long bookingSlotId, List<Long> additionalFormTemplateIds, String bookingComment, Boolean isForReview) throws CreateBookingException {

        try {
            Booking newBooking = createBooking(servicemanId, consultationPurposeId, bookingSlotId, bookingComment, isForReview);
            if (!additionalFormTemplateIds.isEmpty()) {
                attachFormInstancesByClerk(newBooking.getBookingSlot().getSlotId(), additionalFormTemplateIds);
            }

            // Notification module can fire here
            Notification n = new Notification("Booking has been created for you", "Your have a new booking with Booking Id[" + newBooking.getBookingId() + "] created for you.", NotificationTypeEnum.BOOKING, newBooking.getBookingId());
            notificationSessionBeanLocal.createNewNotification(n, servicemanId, true);
            return newBooking;

        } catch (CreateBookingException | AttachFormInstancesException | CreateNotificationException ex) {
            throw new CreateBookingException("Failed to create booking: " + ex.getMessage());
        }

    }

    @Override
    public Booking attachFormInstancesByClerk(Long bookingSlotId, List<Long> additionalFormTemplateIds) throws AttachFormInstancesException {

        BookingSlot bookingSlot = slotSessionBeanLocal.retrieveBookingSlotById(bookingSlotId);

        if (bookingSlot == null) {
            throw new AttachFormInstancesException("Booking Slot Id not valid");
        }

        Booking booking = bookingSlot.getBooking();

        for (Long ftId : additionalFormTemplateIds) {
            try {
                Long formInstanceId = formInstanceSessionBeanLocal.generateFormInstance(booking.getServiceman().getServicemanId(), ftId);
                FormInstance fi = formInstanceSessionBeanLocal.retrieveFormInstance(formInstanceId);
                booking.getFormInstances().add(fi);
                fi.setBooking(booking);

            } catch (GenerateFormInstanceException ex) {
                System.out.println("> " + ex.getMessage());
            }
        }

        // Notification module can fire here
        Notification n = new Notification("There is new required form for booking", "Your booking with Booking Id[" + booking.getBookingId() + "] has a new form attached", NotificationTypeEnum.BOOKING, booking.getBookingId());
        try {
            notificationSessionBeanLocal.createNewNotification(n, booking.getServiceman().getServicemanId(), true);
        } catch (CreateNotificationException ex) {
            System.out.println("> " + ex.getMessage());
        }
        return booking;
    }

    @Override
    public List<Booking> retrieveServicemanBookings(Long servicemanId) {
        Query query = em.createQuery("SELECT b FROM Booking b WHERE b.serviceman.servicemanId = :id ");
        query.setParameter("id", servicemanId);
        return query.getResultList();
    }

    @Override
    public void markBookingAttendance(Long bookingId) throws MarkBookingAttendanceException {

        Booking booking = retrieveBookingById(bookingId);

        if (booking == null) {
            throw new MarkBookingAttendanceException("Invalid Booking Id");
        } else if (booking.getBookingStatusEnum() != BookingStatusEnum.UPCOMING) {
            throw new MarkBookingAttendanceException("Invalid Booking Status: Status must be UPCOMING. Current status: " + booking.getBookingStatusEnum().toString());
        }

        String formInstanceNames = "";
        for (FormInstance fi : booking.getFormInstances()) {

            if (fi.getFormInstanceStatusEnum() == FormInstanceStatusEnum.DRAFT) {

                if (formInstanceNames.equals("")) {
                    formInstanceNames = fi.getFormTemplateMapping().getFormTemplateName();
                } else {
                    formInstanceNames = formInstanceNames + ", " + fi.getFormTemplateMapping().getFormTemplateName();
                }

            }

        }

        if (formInstanceNames.equals("")) {

            try {

                consultationSessionBeanLocal.createConsultation(booking.getBookingId());
                booking.setBookingStatusEnum(BookingStatusEnum.PAST);

            } catch (CreateConsultationException ex) {
                throw new MarkBookingAttendanceException("Unable to create consultation for booking: " + ex.getMessage());
            }

        } else {
            throw new MarkBookingAttendanceException("Unable to mark attandance due to unsubmitted forms: " + formInstanceNames);
        }
        
        Notification n = new Notification("Your booking attendance have been marked", "Your attemdace for booking with Booking Id[" + booking.getBookingId() + "] has been marked", NotificationTypeEnum.BOOKING, booking.getBookingId());
        try {
            notificationSessionBeanLocal.createNewNotification(n, booking.getServiceman().getServicemanId(), true);
        } catch (CreateNotificationException ex) {
            System.out.println("> " + ex.getMessage());
        }

    }

    @Override
    public Booking retrieveBookingById(Long bookingId
    ) {
        Booking booking = em.find(Booking.class, bookingId);
        return booking;
    }

    @Override
    public void cancelBooking(Long bookingId, String cancellationComment) throws CancelBookingException {

        if (bookingId == null) {
            throw new CancelBookingException("Please supply a valid Booking Id");
        }

        Booking booking = retrieveBookingById(bookingId);

        if (booking != null) {

            if (booking.getBookingStatusEnum() == BookingStatusEnum.UPCOMING) {
                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(booking.getBookingSlot().getStartDateTime());
                boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
                        && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
                if (sameDay) {
                    throw new CancelBookingException("Unable to cancel booking scheduled for today");
                }
                try {
                    booking.setBookingStatusEnum(BookingStatusEnum.CANCELLED);
                    booking.setCancellationComment(cancellationComment);

                    List<Long> formInstanceIds = new ArrayList<>();

                    for (FormInstance fi : booking.getFormInstances()) {
                        formInstanceIds.add(fi.getFormInstanceId());
                    }

                    for (Long fiId : formInstanceIds) {
                        formInstanceSessionBeanLocal.deleteFormInstance(fiId, Boolean.TRUE);
                    }

                    slotSessionBeanLocal.createBookingSlots(
                            booking.getBookingSlot().getMedicalCentre().getMedicalCentreId(),
                            booking.getBookingSlot().getStartDateTime(),
                            booking.getBookingSlot().getEndDateTime());

                } catch (ScheduleBookingSlotException | DeleteFormInstanceException ex) {
                    throw new CancelBookingException("Unable to create replacement booking slot: " + ex.getMessage());

                }

            } else {
                throw new CancelBookingException("Invalid Booking Status: You can only cancel bookings with upcoming");
            }

        } else {
            throw new CancelBookingException("Invalid Booking Id");
        }

    }

    @Override
    public void cancelBookingByClerk(Long bookingId, String cancellationComment) throws CancelBookingException {

        if (bookingId == null) {
            throw new CancelBookingException("Please supply a valid Booking Id");
        }

        Booking booking = retrieveBookingById(bookingId);

        if (booking != null) {

            if (booking.getBookingStatusEnum() == BookingStatusEnum.UPCOMING) {
                try {
                    booking.setBookingStatusEnum(BookingStatusEnum.CANCELLED);
                    booking.setCancellationComment(cancellationComment);

                    List<Long> formInstanceIds = new ArrayList<>();

                    for (FormInstance fi : booking.getFormInstances()) {
                        formInstanceIds.add(fi.getFormInstanceId());
                    }

                    for (Long fiId : formInstanceIds) {
                        formInstanceSessionBeanLocal.deleteFormInstance(fiId, Boolean.TRUE);
                    }

                    slotSessionBeanLocal.createBookingSlots(
                            booking.getBookingSlot().getMedicalCentre().getMedicalCentreId(),
                            booking.getBookingSlot().getStartDateTime(),
                            booking.getBookingSlot().getEndDateTime());

                } catch (ScheduleBookingSlotException | DeleteFormInstanceException ex) {
                    throw new CancelBookingException("Unable to create replacement booking slot: " + ex.getMessage());

                }

            } else {
                throw new CancelBookingException("Invalid Booking Status: You can only cancel bookings with upcoming");
            }

        } else {
            throw new CancelBookingException("Invalid Booking Id");
        }

    }

    @Override
    public void markBookingAbsent(Long bookingId, String cancellationComment) throws MarkBookingAbsentException {

        Booking booking = retrieveBookingById(bookingId);

        if (booking != null) {

            if (booking.getBookingStatusEnum() == BookingStatusEnum.UPCOMING) {

                try {
                    booking.setBookingStatusEnum(BookingStatusEnum.ABSENT);
                    booking.setCancellationComment(cancellationComment);

                    List<Long> formInstanceIds = new ArrayList<>();

                    for (FormInstance fi : booking.getFormInstances()) {
                        formInstanceIds.add(fi.getFormInstanceId());
                    }

                    for (Long fiId : formInstanceIds) {
                        formInstanceSessionBeanLocal.deleteFormInstance(fiId, Boolean.TRUE);
                    }

                } catch (DeleteFormInstanceException ex) {
                    throw new MarkBookingAbsentException("Unable to delete form instances linked to booking slot: " + ex.getMessage());

                }

            } else {
                throw new MarkBookingAbsentException("Invalid Booking Status: Booking has no upcoming status");
            }
        }
    }

    @Override
    public List<Booking> retrieveAllBookings() {
        Query query = em.createQuery("SELECT b FROM Booking b");
        return query.getResultList();
    }

    @Override
    public List<Booking> retrieveAllUpcomingBookings() {
        Query query = em.createQuery("SELECT b FROM Booking b WHERE b.bookingStatusEnum = :status ");
        query.setParameter("status", BookingStatusEnum.UPCOMING);
        return query.getResultList();
    }

    @Override
    public void updateBookingComment(Long bookingId, String bookingComment) throws UpdateBookingCommentException {

        Booking booking = retrieveBookingById(bookingId);

        if (booking == null) {
            throw new UpdateBookingCommentException("Invalid Booking id");
        }

        booking.setBookingComment(bookingComment);

    }

    @Override
    public void convertBookingToReview(Long bookingId) throws ConvertBookingException {

        Booking booking = retrieveBookingById(bookingId);

        if (booking == null) {
            throw new ConvertBookingException("Invalid Booking id");
        }
        
        booking.setIsForReview(Boolean.TRUE);
        
    }

}
