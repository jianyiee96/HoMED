/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.DeleteFormInstanceException;
import entity.FormInstance;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.GenerateFormInstanceException;

/**
 *
 * @author User
 */
@Local
public interface FormInstanceSessionBeanLocal {
    
    public Long generateFormInstance(Long servicemanId, Long formTemplateId) throws GenerateFormInstanceException;

    public void deleteFormInstance(Long formInstanceId) throws DeleteFormInstanceException;
    
    public List<FormInstance> retrieveServicemanFormInstances(Long servicemanId);
    
    public FormInstance retrieveFormInstance(Long id);
    
}
