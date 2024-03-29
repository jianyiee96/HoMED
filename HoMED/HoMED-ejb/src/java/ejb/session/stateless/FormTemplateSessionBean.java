/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.ConsultationPurpose;
import entity.FormField;
import entity.FormFieldOption;
import entity.FormTemplate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.FormTemplateStatusEnum;
import util.exceptions.CreateFormTemplateException;

@Stateless
public class FormTemplateSessionBean implements FormTemplateSessionBeanLocal {

    @EJB
    private ConsultationPurposeSessionBeanLocal consultationPurposeSessionBeanLocal;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    private final String generalUnexpectedErrorMessage = "An unexpected error has occurred while ";

    public FormTemplateSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createFormTemplate(FormTemplate formTemplate) throws CreateFormTemplateException {
        String errorMessage = "Failed to create Form Template: ";

        try {
            Set<ConstraintViolation<FormTemplate>> constraintViolations = validator.validate(formTemplate);

            if (constraintViolations.isEmpty()) {
                em.persist(formTemplate);
                em.flush();

                return formTemplate.getFormTemplateId();
            } else {
                throw new CreateFormTemplateException(prepareInputDataValidationErrorsMessage(constraintViolations));

            }
        } catch (CreateFormTemplateException ex) {
            throw new CreateFormTemplateException(errorMessage + ex.getMessage());

        } catch (PersistenceException ex) {
            throw new CreateFormTemplateException(generalUnexpectedErrorMessage + "creating Form Template [Persistence Exception]");
        } catch (Exception ex) {
            System.out.println(ex.getClass());
            ex.printStackTrace();
            throw new CreateFormTemplateException(generalUnexpectedErrorMessage + "creating Form Template");
        }
    }

    @Override
    public void cloneFormTemplate(Long formTemplateId) {

        FormTemplate ft = retrieveFormTemplate(formTemplateId);

        FormTemplate newFT = new FormTemplate(ft.getFormTemplateName() + " - Cloned");

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
            newFF.setIsRequired(ff.getIsRequired());
            newFF.setFormFieldAccess(ff.getFormFieldAccess());
            newFF.setPosition(ff.getPosition());
            newFF.setQuestion(ff.getQuestion());
            em.persist(newFF);
            newFFs.add(newFF);
        }

        newFT.setFormFields(newFFs);
        newFT.setDeclaration(ft.getDeclaration());
        em.persist(newFT);
        em.flush();

    }

    @Override
    public void saveFormTemplate(FormTemplate formTemplate) {

        try {
            formTemplate.getFormFields().sort((x, y) -> x.getPosition() - y.getPosition());

            FormTemplate ft = retrieveFormTemplate(formTemplate.getFormTemplateId());

            for (FormField oldFF : ft.getFormFields()) {
                oldFF.setFormFieldOptions(new ArrayList<>());
            }
            ft.setFormFields(new ArrayList<>());
            em.flush();
            
            ft.setFormTemplateStatus(formTemplate.getFormTemplateStatus());
            ft.setFormTemplateName(formTemplate.getFormTemplateName());
            ft.setDeclaration(formTemplate.getDeclaration());
            ft.setIsPublic(formTemplate.getIsPublic());
            
            List<FormField> ffs = formTemplate.getFormFields();

            for (FormField ff : ffs) {
                for (FormFieldOption ffo : ff.getFormFieldOptions()) {
                    em.persist(ffo);
                    em.flush();
                }
                em.persist(ff);
                em.flush();
            }

            ft.setFormFields(ffs);
            em.merge(ft);
            em.flush();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public boolean publishFormTemplate(Long id) {
        FormTemplate formTemplate = retrieveFormTemplate(id);

        if (formTemplate.getFormTemplateStatus() == FormTemplateStatusEnum.ARCHIVED || formTemplate.getFormTemplateStatus() == FormTemplateStatusEnum.DRAFT && formTemplate.getFormFields().size() > 0) {
            formTemplate.setFormTemplateStatus(FormTemplateStatusEnum.PUBLISHED);
            formTemplate.setDatePublished(new Date());
            em.flush();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean archiveFormTemplate(Long id) {
        FormTemplate formTemplate = retrieveFormTemplate(id);

        if (formTemplate.getFormTemplateStatus() == FormTemplateStatusEnum.PUBLISHED) {
            formTemplate.setFormTemplateStatus(FormTemplateStatusEnum.ARCHIVED);

            List<ConsultationPurpose> cpUnlink = consultationPurposeSessionBeanLocal.retrieveAllFormTemplateLinkedConsultationPurposes(id);
            for (ConsultationPurpose cp : cpUnlink) {
                cp.getFormTemplates().remove(formTemplate);
            }
            formTemplate.setConsultationPurposes(new ArrayList<>());
            em.flush();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteFormTemplate(Long id) {
        FormTemplate formTemplate = retrieveFormTemplate(id);

        if (formTemplate.getFormTemplateStatus() == FormTemplateStatusEnum.DRAFT) {
            formTemplate.setFormTemplateStatus(FormTemplateStatusEnum.DELETED);
            em.flush();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean restoreFormTemplate(Long id) {
        FormTemplate formTemplate = retrieveFormTemplate(id);

        if (formTemplate.getFormTemplateStatus() == FormTemplateStatusEnum.DELETED) {
            formTemplate.setFormTemplateStatus(FormTemplateStatusEnum.DRAFT);
            em.flush();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public FormTemplate retrieveFormTemplate(Long id) {
        FormTemplate formTemplate = em.find(FormTemplate.class, id);
        formTemplate.getFormFields().sort((x, y) -> x.getPosition() - y.getPosition());
        return formTemplate;
    }

    @Override
    public List<FormTemplate> retrieveAllFormTemplates() {
        Query query = em.createQuery("SELECT f FROM FormTemplate f");
        List<FormTemplate> fts = query.getResultList();
        for (FormTemplate ft : fts) {
            ft.getConsultationPurposes().size();
            ft.getFormFields().sort((x, y) -> x.getPosition() - y.getPosition());
        }
        return fts;
    }
    
    @Override
    public List<FormTemplate> retrieveAllPublishedFormTemplates() {
        Query query = em.createQuery("SELECT f FROM FormTemplate f WHERE f.formTemplateStatus = :publish");
        query.setParameter("publish", FormTemplateStatusEnum.PUBLISHED);
        List<FormTemplate> fts = query.getResultList();
        for (FormTemplate ft : fts) {
            ft.getFormFields().sort((x, y) -> x.getPosition() - y.getPosition());
        }
        return fts;
    }

    @Override
    public List<FormTemplate> retrieveAllPublishedPublicFormTemplates() {
        Query query = em.createQuery("SELECT f FROM FormTemplate f WHERE f.formTemplateStatus = :publish AND f.isPublic = TRUE");
        query.setParameter("publish", FormTemplateStatusEnum.PUBLISHED);
        List<FormTemplate> fts = query.getResultList();
        for (FormTemplate ft : fts) {
            ft.getFormFields().sort((x, y) -> x.getPosition() - y.getPosition());
        }
        return fts;
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<FormTemplate>> constraintViolations) {
        String msg = "Input data validation error!:";
        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        return msg;
    }

}
