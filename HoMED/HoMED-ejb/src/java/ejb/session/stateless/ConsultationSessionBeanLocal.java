/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Consultation;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author User
 */
@Local
public interface ConsultationSessionBeanLocal {
    
    public List<Consultation> retrieveWaitingConsultationsByMedicalCentre(Long medicalCentreId);
    
}
