/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.ConsultationPurpose;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.CreateConsultationPurposeException;

/**
 *
 * @author User
 */
@Local
public interface ConsultationPurposeSessionBeanLocal {
    
    public Long createConsultationPurpose(ConsultationPurpose consultationPurpose) throws CreateConsultationPurposeException;
    
    public ConsultationPurpose retrieveConsultationPurpose(Long id);
    
    public List<ConsultationPurpose> retrieveAllConsultationPurposes();
}
