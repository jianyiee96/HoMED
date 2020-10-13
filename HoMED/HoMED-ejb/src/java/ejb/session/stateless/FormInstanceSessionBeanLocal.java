/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import util.exceptions.DeleteFormInstanceException;
import entity.FormInstance;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.ArchiveFormInstanceException;
import util.exceptions.GenerateFormInstanceException;
import util.exceptions.SubmitFormInstanceException;
import util.exceptions.UpdateFormInstanceException;

/**
 *
 * @author User
 */
@Local
public interface FormInstanceSessionBeanLocal {
    
    public Long generateFormInstance(Long servicemanId, Long formTemplateId) throws GenerateFormInstanceException;

    public void deleteFormInstance(Long formInstanceId, Boolean systemCall) throws DeleteFormInstanceException;
    
    public void updateFormInstanceFieldValues(FormInstance formInstance) throws UpdateFormInstanceException;
    
    public void submitFormInstance(Long formInstanceId) throws SubmitFormInstanceException;
    
    public void archiveFormInstance(Long formInstanceId) throws ArchiveFormInstanceException;
    
    public List<FormInstance> retrieveServicemanFormInstances(Long servicemanId);
    
    public FormInstance retrieveFormInstance(Long id);
    
}
