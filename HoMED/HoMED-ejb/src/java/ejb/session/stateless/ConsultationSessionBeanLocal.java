/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Consultation;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.CreateConsultationException;
import util.exceptions.DeferConsultationException;
import util.exceptions.EndConsultationException;
import util.exceptions.InvalidateConsultationException;
import util.exceptions.RetrieveConsultationQueuePositionException;
import util.exceptions.StartConsultationException;

/**
 *
 * @author User
 */
@Local
public interface ConsultationSessionBeanLocal {

    public void createConsultation(Long bookingId) throws CreateConsultationException;

    public void startConsultation(Long consultationId, Long medicalOfficerId) throws StartConsultationException;

    public void invalidateConsultation(Long consultationId, String cancellationComment) throws InvalidateConsultationException;

    public void endConsultation(Long consultationId, String remarks, String remarksForServiceman) throws EndConsultationException;

    public Consultation retrieveConsultationById(Long consultationId);

    public List<Consultation> retrieveWaitingConsultationsByMedicalCentre(Long medicalCentreId);

    public List<Consultation> retrieveServicemanNonWaitingConsultation(Long servicemanId);

    public List<Consultation> retrieveAllServicemanConsultations(Long servicemanId);

    public int retrieveConsultationQueuePosition(Long consultationId) throws RetrieveConsultationQueuePositionException;

    public List<Consultation> retrieveCompletedConsultationsByServicemanId(Long servicemanId);

    public List<Consultation> retrieveCompletedConsultationsByMedicalOfficerId(Long medicalOfficerId);

    public void deferConsultation(Long consultationId, String remarks, String remarksForServiceman) throws DeferConsultationException;

    public void startConsultationByInit(Long consultationId, Long medicalOfficerId, Date startDate) throws StartConsultationException;

    public void endConsultationByInit(Long consultationId, String remarks, String remarksForServiceman, Date endDate) throws EndConsultationException;

    public List<Consultation> retrieveAllConsultations();


}
