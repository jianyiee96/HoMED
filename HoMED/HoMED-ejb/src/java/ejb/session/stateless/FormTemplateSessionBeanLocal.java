/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.FormTemplate;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.CreateFormTemplateException;

/**
 *
 * @author User
 */
@Local
public interface FormTemplateSessionBeanLocal {

    public Long createFormTemplate(FormTemplate formTemplate) throws CreateFormTemplateException;

    public void cloneFormTemplate(Long formTemplateId);

    public void saveFormTemplate(FormTemplate formTemplate);

    public boolean publishFormTemplate(Long id);

    public boolean archiveFormTemplate(Long id);

    public boolean deleteFormTemplate(Long id);
    
    public boolean restoreFormTemplate(Long id);

    public boolean updateFormTemplatePrivacy(Long id, boolean newIsPublic);

    public FormTemplate retrieveFormTemplate(Long id);

    public List<FormTemplate> retrieveAllFormTemplates();

    public List<FormTemplate> retrieveAllPublishedFormTemplates();

    public List<FormTemplate> retrieveAllPublishedPublicFormTemplates();
    
}
