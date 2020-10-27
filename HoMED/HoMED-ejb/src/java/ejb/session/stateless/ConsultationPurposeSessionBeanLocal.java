/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.ConsultationPurpose;
import entity.FormTemplate;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.CreateConsultationPurposeException;
import util.exceptions.RelinkFormTemplatesException;

/**
 *
 * @author User
 */
@Local
public interface ConsultationPurposeSessionBeanLocal {
    
    public Long createConsultationPurpose(ConsultationPurpose consultationPurpose) throws CreateConsultationPurposeException;
    
    public ConsultationPurpose retrieveConsultationPurpose(Long id);
    
    public List<ConsultationPurpose> retrieveAllConsultationPurposes();
    
    public List<ConsultationPurpose> retrieveAllActiveConsultationPurposes();
    
    public List<ConsultationPurpose> retrieveAllActiveNonReviewOnlyConsultationPurposes();
    
    public void relinkFormTemplates(Long id, List<FormTemplate> formTemplates) throws RelinkFormTemplatesException;
    
    public List<ConsultationPurpose> retrieveAllFormTemplateLinkedConsultationPurposes(Long id);
    
    public void deleteConsultationPurpose(Long id);
    
    public void restoreConsultationPurpose(Long id);

    public void toggleConsultationPurposeReviewOnly(Long id);

}
