/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Employee;
import entity.FormField;
import entity.FormFieldOption;
import entity.FormTemplate;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.FormStatusEnum;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Stateless
public class FormTemplateSessionBean implements FormTemplateSessionBeanLocal {

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public FormTemplateSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createFormTemplate(FormTemplate formTemplate) throws InputDataValidationException, UnknownPersistenceException {
        try {
            Set<ConstraintViolation<FormTemplate>> constraintViolations = validator.validate(formTemplate);

            if (constraintViolations.isEmpty()) {
                em.persist(formTemplate);
                em.flush();

                return formTemplate.getFormTemplateId();
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (PersistenceException ex) {
            throw new UnknownPersistenceException(ex.getMessage());
        }
    }
    
    @Override
    public void saveFormTemplate(FormTemplate formTemplate) {
        
        FormTemplate ft = retrieveFormTemplate(formTemplate.getFormTemplateId());
        ft.setFormStatus(formTemplate.getFormStatus());
        ft.setFormTemplateName(formTemplate.getFormTemplateName());
        ft.setFormFields(formTemplate.getFormFields());
        
        for(FormField ff : ft.getFormFields()){
            for(FormFieldOption ffo : ff.getFormFieldOptions()) {
                em.persist(ffo);
            }
            em.persist(ff);
        }
        
        em.merge(ft);
        em.flush();
    }

    @Override
    public boolean publishFormTemplate(Long id) {
        FormTemplate formTemplate = retrieveFormTemplate(id);
        
        if(formTemplate.getFormStatus() == FormStatusEnum.DRAFT) {
            formTemplate.setFormStatus(FormStatusEnum.PUBLISHED);
            em.flush();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean archiveFormTemplate(Long id) {
        FormTemplate formTemplate = retrieveFormTemplate(id);
        
        if(formTemplate.getFormStatus() == FormStatusEnum.PUBLISHED) {
            formTemplate.setFormStatus(FormStatusEnum.ARCHIVED);
            em.flush();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteFormTemplate(Long id) {
        FormTemplate formTemplate = retrieveFormTemplate(id);
        
        if(formTemplate.getFormStatus() == FormStatusEnum.DRAFT) {
            formTemplate.setFormStatus(FormStatusEnum.DELETED);
            em.flush();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public FormTemplate retrieveFormTemplate(Long id) {
        FormTemplate formTemplate = em.find(FormTemplate.class, id);
        return formTemplate;
    }

    @Override
    public List<FormTemplate> retrieveAllFormTemplates() {
        Query query = em.createQuery("SELECT f FROM FormTemplate f");

        return query.getResultList();
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<FormTemplate>> constraintViolations) {
        String msg = "Input data validation error!:";
        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        return msg;
    }

    public void persist(Object object) {
        em.persist(object);
    }
}
