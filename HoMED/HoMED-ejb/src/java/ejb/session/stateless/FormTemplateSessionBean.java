/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.FormField;
import entity.FormFieldOption;
import entity.FormTemplate;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    public Long createFormTemplate(FormTemplate formTemplate) {
        try {
            Set<ConstraintViolation<FormTemplate>> constraintViolations = validator.validate(formTemplate);

            if (constraintViolations.isEmpty()) {
                em.persist(formTemplate);
                em.flush();

                return formTemplate.getFormTemplateId();
            } else {
                System.out.println("Error Error");
                return null;

            }
        } catch (Exception ex) {
            System.out.println("Error Error");
            return null;
        }
    }

    @Override
    public void cloneFormTemplate(Long formTemplateId) {

        FormTemplate ft = retrieveFormTemplate(formTemplateId);

        FormTemplate newFT = new FormTemplate(ft.getFormTemplateName() + " Cloned");

        List<FormField> newFFs = new ArrayList<>();

        for (FormField ff : ft.getFormFields()) {

            FormField newFF = new FormField();

            List<FormFieldOption> newFFOs = new ArrayList<>();

            for (FormFieldOption ffo : ff.getFormFieldOptions()) {

                FormFieldOption newFFO = new FormFieldOption(ffo.getFormFieldOptionValue());
                em.persist(newFFO);
                newFFOs.add(newFFO);

            }

            newFF.setFormFieldOptions(newFFOs);
            newFF.setInputType(ff.getInputType());
            newFF.setIsRequired(ff.isIsRequired());
            newFF.setIsServicemanEditable(ff.isIsServicemanEditable());
            newFF.setPosition(ff.getPosition());
            newFF.setTitle(ff.getTitle());
            em.persist(newFF);
            newFFs.add(newFF);
        }

        newFT.setFormFields(newFFs);

        em.persist(newFT);
        em.flush();

    }

    @Override
    public void saveFormTemplate(FormTemplate formTemplate) {

        FormTemplate ft = retrieveFormTemplate(formTemplate.getFormTemplateId());

        for (FormField oldFF : ft.getFormFields()) {
            oldFF.setFormFieldOptions(new ArrayList<>());
        }
        ft.setFormFields(new ArrayList<>());
        em.flush();


        ft.setFormStatus(formTemplate.getFormStatus());
        ft.setFormTemplateName(formTemplate.getFormTemplateName());
        ft.setFormFields(formTemplate.getFormFields());

        for (FormField ff : ft.getFormFields()) {
            for (FormFieldOption ffo : ff.getFormFieldOptions()) {
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

        if (formTemplate.getFormStatus() == FormStatusEnum.ARCHIVED || formTemplate.getFormStatus() == FormStatusEnum.DRAFT && formTemplate.getFormFields().size() > 0) {
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

        if (formTemplate.getFormStatus() == FormStatusEnum.PUBLISHED) {
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

        if (formTemplate.getFormStatus() == FormStatusEnum.DRAFT) {
            formTemplate.setFormStatus(FormStatusEnum.DELETED);
            em.flush();
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean updateFormTemplatePrivacy(Long id, boolean newIsPublic) {
        FormTemplate formTemplate = retrieveFormTemplate(id);

        if (formTemplate.isIsPublic() != newIsPublic) {
            formTemplate.setIsPublic(newIsPublic);
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

}
