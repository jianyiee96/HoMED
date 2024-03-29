/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Booking;
import entity.Consultation;
import entity.FormInstance;
import entity.MedicalOfficer;
import entity.Notification;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.BookingStatusEnum;
import util.enumeration.ConsultationStatusEnum;
import util.enumeration.NotificationTypeEnum;
import util.exceptions.CreateConsultationException;
import util.exceptions.CreateNotificationException;
import util.exceptions.DeferConsultationException;
import util.exceptions.DeleteFormInstanceException;
import util.exceptions.EndConsultationException;
import util.exceptions.InvalidateConsultationException;
import util.exceptions.RetrieveConsultationQueuePositionException;
import util.exceptions.StartConsultationException;

@Stateless
public class ConsultationSessionBean implements ConsultationSessionBeanLocal {

    @EJB
    private NotificationSessionBeanLocal notificationSessionBeanLocal;

    @EJB
    private BookingSessionBeanLocal bookingSessionBeanLocal;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @EJB
    private FormInstanceSessionBeanLocal formInstanceSessionBeanLocal;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    @Override
    public void createConsultation(Long bookingId) throws CreateConsultationException {

        Booking booking = bookingSessionBeanLocal.retrieveBookingById(bookingId);

        if (booking == null) {
            throw new CreateConsultationException("Invalid Booking Id");
        }

        Consultation newConsultation = new Consultation();
        newConsultation.setBooking(booking);
        booking.setConsultation(newConsultation);

        em.persist(newConsultation);
        em.flush();

    }

    @Override
    public void startConsultation(Long consultationId, Long medicalOfficerId) throws StartConsultationException {

        Consultation consultation = retrieveConsultationById(consultationId);
        MedicalOfficer medicalOfficer = employeeSessionBeanLocal.retrieveMedicalOfficerById(medicalOfficerId);

        if (consultation == null) {
            throw new StartConsultationException("Invalid Consultation Id");
        } else if (consultation.getConsultationStatusEnum() != ConsultationStatusEnum.WAITING) {
            throw new StartConsultationException("Invalid Consultation Status: Consultation is not in WAITING status");
        } else if (medicalOfficer == null) {
            throw new StartConsultationException("Invalid MedicalOfficer Id");
        } else if (medicalOfficer.getCurrentConsultation() != null) {
            throw new StartConsultationException("Unable to start Consultation: Medical Officer has a current consultation");
        } else if (medicalOfficer.getMedicalCentre() == null) {
            throw new StartConsultationException("Unable to start Consultation: Medical Officer is not attached to medical centre");
        } else if (!medicalOfficer.getMedicalCentre().equals(consultation.getBooking().getBookingSlot().getMedicalCentre())) {
            throw new StartConsultationException("Unable to start Consultation: Medical Officer's attached medical centre does not match with booking medical centre");
        }

        try {
            consultation.setStartDateTime(new Date());
            consultation.setConsultationStatusEnum(ConsultationStatusEnum.ONGOING);
            consultation.setMedicalOfficer(medicalOfficer);
            medicalOfficer.setCurrentConsultation(consultation);

            String queueNumber = String.format("%03d", consultation.getBooking().getBookingId() % 1000);
            String title = "Your consultation [Queue No. " + queueNumber + "] is ready";
            String body = "Medical Officer: Dr " + consultation.getMedicalOfficer().getName() + "\nPlease proceed to the consultation room immediately.";

            Notification n = new Notification(title, body, NotificationTypeEnum.CONSULTATION, consultationId);
            notificationSessionBeanLocal.createNewNotification(n, consultation.getBooking().getServiceman().getServicemanId(), true);
            notificationSessionBeanLocal.sendPushNotification(title, body, consultation.getBooking().getServiceman().getFcmToken());
        } catch (Exception ex) {
            throw new StartConsultationException("Unknown exception: " + ex.getMessage());
        }
    }

    @Override
    public void startConsultationByInit(Long consultationId, Long medicalOfficerId, Date startDate) throws StartConsultationException {

        Consultation consultation = retrieveConsultationById(consultationId);
        MedicalOfficer medicalOfficer = employeeSessionBeanLocal.retrieveMedicalOfficerById(medicalOfficerId);

        if (consultation == null) {
            throw new StartConsultationException("Invalid Consultation Id");
        } else if (consultation.getConsultationStatusEnum() != ConsultationStatusEnum.WAITING) {
            throw new StartConsultationException("Invalid Consultation Status: Consultation is not in WAITING status");
        } else if (medicalOfficer == null) {
            throw new StartConsultationException("Invalid MedicalOfficer Id");
        } else if (medicalOfficer.getCurrentConsultation() != null) {
            throw new StartConsultationException("Unable to start Consultation: Medical Officer has a current consultation");
        } else if (medicalOfficer.getMedicalCentre() == null) {
            throw new StartConsultationException("Unable to start Consultation: Medical Officer is not attached to medical centre");
        } else if (!medicalOfficer.getMedicalCentre().equals(consultation.getBooking().getBookingSlot().getMedicalCentre())) {
            throw new StartConsultationException("Unable to start Consultation: Medical Officer's attached medical centre does not match with booking medical centre");
        }

        try {
            // DIFFERENCE - To create a custom start date
//            consultation.setStartDateTime(new Date());
            consultation.setStartDateTime(startDate);
            consultation.setConsultationStatusEnum(ConsultationStatusEnum.ONGOING);
            consultation.setMedicalOfficer(medicalOfficer);
            medicalOfficer.setCurrentConsultation(consultation);
        } catch (Exception ex) {
            throw new StartConsultationException("Unknown exception: " + ex.getMessage());
        }

        Notification n = new Notification("Your consultation has started", "Your consultation with Consultation ID[" + consultationId + "] is beginning. Please proceed to consultation room", NotificationTypeEnum.CONSULTATION, consultationId);

        try {
            notificationSessionBeanLocal.createNewNotification(n, consultation.getBooking().getServiceman().getServicemanId(), true);
            notificationSessionBeanLocal.sendPushNotification("Your consultation has started", "Your consultation with Consultation ID[" + consultationId + "] is beginning. Please proceed to consultation room", consultation.getBooking().getServiceman().getFcmToken());
        } catch (CreateNotificationException ex) {
            System.out.println("> " + ex.getMessage());
        }
    }

    @Override
    public void deferConsultation(Long consultationId, String remarks, String remarksForServiceman) throws DeferConsultationException {
        Consultation consultation = retrieveConsultationById(consultationId);
        if (consultation == null) {
            throw new DeferConsultationException("Invalid Consultation Id");
        } else if (consultation.getConsultationStatusEnum() != ConsultationStatusEnum.ONGOING) {
            throw new DeferConsultationException("Invalid Consultation Status: Consultation is not in ONGOING status");
        }

        try {
            consultation.setConsultationStatusEnum(ConsultationStatusEnum.WAITING);
            consultation.setStartDateTime(null);
            consultation.setJoinQueueDateTime(new Date());
            consultation.getMedicalOfficer().setCurrentConsultation(null);
            consultation.setMedicalOfficer(null);
            consultation.setRemarks(remarks);
            consultation.setRemarksForServiceman(remarksForServiceman);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DeferConsultationException("Unknown exception: " + ex.getMessage());
        }

    }

    @Override
    public void endConsultation(Long consultationId, String remarks, String remarksForServiceman) throws EndConsultationException {
        Consultation consultation = retrieveConsultationById(consultationId);
        if (consultation == null) {
            throw new EndConsultationException("Invalid Consultation Id");
        } else if (consultation.getConsultationStatusEnum() != ConsultationStatusEnum.ONGOING) {
            throw new EndConsultationException("Invalid Consultation Status: Consultation is not in ONGOING status");
        }

        for (FormInstance fi : consultation.getBooking().getFormInstances()) {
            if (fi.getSignedBy() == null) {
                throw new EndConsultationException("Unable to end Consultation: All form instances are required to be signed by attending medical officer");
            }
        }
        try {
            consultation.setEndDateTime(new Date());
            consultation.setConsultationStatusEnum(ConsultationStatusEnum.COMPLETED);
            consultation.getMedicalOfficer().setCurrentConsultation(null);
            consultation.getMedicalOfficer().getCompletedConsultations().add(consultation);
            consultation.setRemarks(remarks);
            consultation.setRemarksForServiceman(remarksForServiceman);
        } catch (Exception ex) {
            throw new EndConsultationException("Unknown exception: " + ex.getMessage());
        }

        String title = "Consultation Completed";
        String body = "Your consultation [ID: " + consultationId + "] has been completed.";

        Notification n = new Notification(title, body, NotificationTypeEnum.CONSULTATION, consultationId);
        try {
            notificationSessionBeanLocal.createNewNotification(n, consultation.getBooking().getServiceman().getServicemanId(), true);
            notificationSessionBeanLocal.sendPushNotification(title, body, consultation.getBooking().getServiceman().getFcmToken());
        } catch (CreateNotificationException ex) {
            System.out.println("> " + ex.getMessage());
        }

    }

    @Override
    public void endConsultationByInit(Long consultationId, String remarks, String remarksForServiceman, Date endDate) throws EndConsultationException {
        Consultation consultation = retrieveConsultationById(consultationId);
        if (consultation == null) {
            throw new EndConsultationException("Invalid Consultation Id");
        } else if (consultation.getConsultationStatusEnum() != ConsultationStatusEnum.ONGOING) {
            throw new EndConsultationException("Invalid Consultation Status: Consultation is not in ONGOING status");
        }

        for (FormInstance fi : consultation.getBooking().getFormInstances()) {
            if (fi.getSignedBy() == null) {
                throw new EndConsultationException("Unable to end Consultation: All form instances are required to be signed by attending medical officer");
            }
        }
        try {
            // DIFFERENCE - To create a custom end date
//            consultation.setEndDateTime(new Date());
            consultation.setEndDateTime(endDate);
            consultation.setConsultationStatusEnum(ConsultationStatusEnum.COMPLETED);
            consultation.getMedicalOfficer().setCurrentConsultation(null);
            consultation.getMedicalOfficer().getCompletedConsultations().add(consultation);
            consultation.setRemarks(remarks);
            consultation.setRemarksForServiceman(remarksForServiceman);
        } catch (Exception ex) {
            throw new EndConsultationException("Unknown exception: " + ex.getMessage());
        }

    }

    @Override
    public void invalidateConsultation(Long consultationId, String cancellationComment) throws InvalidateConsultationException {
        Consultation consultation = retrieveConsultationById(consultationId);
        if (consultation == null) {
            throw new InvalidateConsultationException("Invalid Consultation Id");
        } else if (consultation.getConsultationStatusEnum() != ConsultationStatusEnum.ONGOING) {
            throw new InvalidateConsultationException("Invalid Consultation Status: Consultation is not in ONGOING status");
        }

        try {
            consultation.getMedicalOfficer().setCurrentConsultation(null);
            Booking booking = consultation.getBooking();

            booking.setBookingStatusEnum(BookingStatusEnum.ABSENT);
            booking.setCancellationComment(cancellationComment);

            List<Long> formInstanceIds = new ArrayList<>();

            for (FormInstance fi : booking.getFormInstances()) {
                formInstanceIds.add(fi.getFormInstanceId());
            }

            for (Long fiId : formInstanceIds) {
                formInstanceSessionBeanLocal.deleteFormInstance(fiId, Boolean.TRUE);
            }

            booking.setConsultation(null);
            em.remove(consultation);

        } catch (DeleteFormInstanceException ex) {
            throw new InvalidateConsultationException("Unable to delete form instances linked to booking slot: " + ex.getMessage());
        } catch (Exception ex) {
            throw new InvalidateConsultationException("Failed to invalidate consultation: " + ex.getMessage());
        }

        String title = "Marked Absent for Booking [ID: " + consultation.getBooking().getBookingId() + "]";
        String body = "You did not attend your consultation which you marked attendance for.";

        Notification n = new Notification(title, body, NotificationTypeEnum.BOOKING, consultation.getBooking().getBookingId());

        try {
            notificationSessionBeanLocal.createNewNotification(n, consultation.getBooking().getServiceman().getServicemanId(), true);
            notificationSessionBeanLocal.sendPushNotification(title, body, consultation.getBooking().getServiceman().getFcmToken());
        } catch (CreateNotificationException ex) {
            System.out.println("> " + ex.getMessage());
        }

    }

    @Override
    public Consultation retrieveConsultationById(Long consultationId) {
        Consultation consultation = em.find(Consultation.class, consultationId);
        return consultation;
    }

    @Override
    public List<Consultation> retrieveWaitingConsultationsByMedicalCentre(Long medicalCentreId) {

        Query query = em.createQuery("SELECT c FROM Consultation c WHERE c.booking.bookingSlot.medicalCentre.medicalCentreId = :id AND c.consultationStatusEnum = :status");
        query.setParameter("id", medicalCentreId);
        query.setParameter("status", ConsultationStatusEnum.WAITING);

        return query.getResultList();

    }

    @Override
    public List<Consultation> retrieveServicemanNonWaitingConsultation(Long servicemanId) {
        Query query = em.createQuery("SELECT c FROM Consultation c WHERE c.booking.serviceman.servicemanId = :id AND c.consultationStatusEnum != :status");
        query.setParameter("id", servicemanId);
        query.setParameter("status", ConsultationStatusEnum.WAITING);

        return query.getResultList();
    }

    @Override
    public List<Consultation> retrieveCompletedConsultationsByServicemanId(Long servicemanId) {
        Query query = em.createQuery("SELECT c FROM Consultation c WHERE c.booking.serviceman.servicemanId = :inServicemanId AND c.consultationStatusEnum = :consultationStatus");
        query.setParameter("inServicemanId", servicemanId);
        query.setParameter("consultationStatus", ConsultationStatusEnum.COMPLETED);

        return query.getResultList();
    }

    @Override
    public List<Consultation> retrieveAllConsultations() {
        Query query = em.createQuery("SELECT c FROM Consultation c ORDER BY c.startDateTime ASC");

        return query.getResultList();
    }

    @Override
    public List<Consultation> retrieveCompletedConsultationsByMedicalOfficerId(Long medicalOfficerId) {
        Query query = em.createQuery("SELECT c FROM Consultation c WHERE c.medicalOfficer.employeeId = :inMedicalOfficerId AND c.consultationStatusEnum = :consultationStatus");
        query.setParameter("inMedicalOfficerId", medicalOfficerId);
        query.setParameter("consultationStatus", ConsultationStatusEnum.COMPLETED);

        return query.getResultList();
    }

    @Override
    public List<Consultation> retrieveAllServicemanConsultations(Long servicemanId) {
        Query query = em.createQuery("SELECT c FROM Consultation c WHERE c.booking.serviceman.servicemanId = :id");
        query.setParameter("id", servicemanId);

        return query.getResultList();
    }

    @Override
    public int retrieveConsultationQueuePosition(Long consultationId) throws RetrieveConsultationQueuePositionException {

        Consultation c = retrieveConsultationById(consultationId);

        if (c == null) {
            throw new RetrieveConsultationQueuePositionException("Invalid Consultation Id");
        } else if (c.getConsultationStatusEnum() != ConsultationStatusEnum.WAITING) {
            throw new RetrieveConsultationQueuePositionException("Consultation is not in any queue");
        }

        List<Consultation> consultationInQueue = retrieveWaitingConsultationsByMedicalCentre(c.getBooking().getBookingSlot().getMedicalCentre().getMedicalCentreId());

        int position = consultationInQueue.indexOf(c);

        if (position < 0) {
            throw new RetrieveConsultationQueuePositionException("Unable to find Consultation in medical centre's queue");
        } else {
            return position + 1;
        }

    }

}
