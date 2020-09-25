/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import javax.ejb.Local;
import util.exceptions.GenerateFormInstanceException;

/**
 *
 * @author User
 */
@Local
public interface FormInstanceSessionBeanLocal {
    
    public Long generateFormInstance(Long servicemanId, Long formTemplateId) throws GenerateFormInstanceException;
    
}
