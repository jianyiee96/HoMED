/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.FormTemplate;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author User
 */
@Local
public interface FormTemplateSessionBeanLocal {
    
    public Long createFormTemplate(FormTemplate formTemplate);
    
    public void cloneFormTemplate(Long formTemplateId);
    
    public void saveFormTemplate(FormTemplate formTemplate);
    
    public boolean publishFormTemplate(Long id);

    public boolean archiveFormTemplate(Long id);

    public boolean deleteFormTemplate(Long id);

    public FormTemplate retrieveFormTemplate(Long id);
    
    public List<FormTemplate> retrieveAllFormTemplates();
    
}
