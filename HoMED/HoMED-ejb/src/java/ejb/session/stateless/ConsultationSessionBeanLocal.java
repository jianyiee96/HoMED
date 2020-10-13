/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Consultation;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.CreateConsultationException;

/**
 *
 * @author User
 */
@Local
public interface ConsultationSessionBeanLocal {
    
    public void createConsultation(Long bookingId) throws CreateConsultationException;
    
    public List<Consultation> retrieveWaitingConsultationsByMedicalCentre(Long medicalCentreId);
    
}
