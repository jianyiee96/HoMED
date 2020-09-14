/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.FormTemplate;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

/**
 *
 * @author User
 */
@Local
public interface FormTemplateSessionBeanLocal {
    
    public Long createFormTemplate(FormTemplate formTemplate) throws InputDataValidationException, UnknownPersistenceException;
    
    public FormTemplate retrieveFormTemplate(Long id);
    
    public List<FormTemplate> retrieveAllFormTemplates();
    
}
