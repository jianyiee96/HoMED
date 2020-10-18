/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Booking;
import entity.Consultation;
import entity.FormInstance;
import entity.MedicalOfficer;
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
import util.exceptions.CreateConsultationException;
import util.exceptions.DeleteFormInstanceException;
import util.exceptions.EndConsultationException;
import util.exceptions.InvalidateConsultationException;
import util.exceptions.RetrieveConsultationQueuePositionException;
import util.exceptions.StartConsultationException;

@Stateless
public class ConsultationSessionBean implements ConsultationSessionBeanLocal {

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
        } catch (Exception ex) {
            throw new StartConsultationException("Unknown exception: " + ex.getMessage());
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

    }

    @Override
    public void invalidateConsultation(Long consultationId) throws InvalidateConsultationException {
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
