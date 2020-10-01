/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import util.exceptions.DeleteFormInstanceException;
import entity.FormInstance;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.GenerateFormInstanceException;
import util.exceptions.UpdateFormInstanceException;

/**
 *
 * @author User
 */
@Local
public interface FormInstanceSessionBeanLocal {
    
    public Long generateFormInstance(Long servicemanId, Long formTemplateId) throws GenerateFormInstanceException;

    public void deleteFormInstance(Long formInstanceId) throws DeleteFormInstanceException;
    
    public void updateFormInstanceFieldValues(FormInstance formInstance) throws UpdateFormInstanceException;
    
    public List<FormInstance> retrieveServicemanFormInstances(Long servicemanId);
    
    public FormInstance retrieveFormInstance(Long id);
    
}
