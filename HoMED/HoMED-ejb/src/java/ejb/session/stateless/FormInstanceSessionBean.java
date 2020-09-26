/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.DeleteFormInstanceException;
import entity.FormField;
import entity.FormInstance;
import entity.FormInstanceField;
import entity.FormInstanceFieldValue;
import entity.FormTemplate;
import entity.Serviceman;
import java.util.ArrayList;
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
import util.exceptions.GenerateFormInstanceException;
import util.exceptions.UpdateFormInstanceException;

/**
 *
 * @author User
 */
@Stateless
public class FormInstanceSessionBean implements FormInstanceSessionBeanLocal {

    @EJB
    FormTemplateSessionBeanLocal formTemplateSessionBeanLocal;

    @EJB
    ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    private final String generalUnexpectedErrorMessage = "An unexpected error has occurred while ";

    public FormInstanceSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long generateFormInstance(Long servicemanId, Long formTemplateId) throws GenerateFormInstanceException {
        String errorMessage = "Failed to generate Form Instance: ";

        try {

            Serviceman serviceman = servicemanSessionBeanLocal.retrieveServicemanById(servicemanId);
            FormTemplate formTemplate = formTemplateSessionBeanLocal.retrieveFormTemplate(formTemplateId);

            if (serviceman == null || formTemplate == null) {
                throw new GenerateFormInstanceException("Please supply an existing servicemanId or formTemplateId");
            } else if (formTemplate.getFormTemplateStatus() != FormTemplateStatusEnum.PUBLISHED) {
                throw new GenerateFormInstanceException("Supplied Form Template is not of Published status");
            }

            FormInstance formInstance = new FormInstance();

            formInstance.setServiceman(serviceman);
            serviceman.getFormInstances().add(formInstance);

            formInstance.setFormTemplateMapping(formTemplate);
            formTemplate.getFormInstances().add(formInstance);

            for (FormField ff : formTemplate.getFormFields()) {

                FormInstanceField fif = new FormInstanceField();
                fif.setFormFieldMapping(ff);
                em.persist(fif);
                formInstance.getFormInstanceFields().add(fif);

            }

            em.persist(formInstance);
            em.flush();
            return formInstance.getFormInstanceId();

        } catch (GenerateFormInstanceException ex) {
            throw new GenerateFormInstanceException(errorMessage + ex.getMessage());
        } catch (PersistenceException ex) {
            throw new GenerateFormInstanceException(generalUnexpectedErrorMessage + "generate Form Instance [Persistence Exception]");
        } catch (Exception ex) {
            System.out.println(ex.getClass());
            ex.printStackTrace();
            throw new GenerateFormInstanceException(generalUnexpectedErrorMessage + "generate Form Instance");
        }
    }

    @Override
    public void deleteFormInstance(Long formInstanceId) throws DeleteFormInstanceException {
        String errorMessage = "Failed to delete Form Instance: ";

        try {

            FormInstance formInstance = retrieveFormInstance(formInstanceId);

            if (formInstance == null) {
                throw new DeleteFormInstanceException("Please supply an existing formInstanceId");
            }

            for (FormInstanceField fif : formInstance.getFormInstanceFields()) {
                fif.setFormInstanceFieldValues(new ArrayList<>());
            }
            formInstance.setFormInstanceFields(new ArrayList<>());

            formInstance.getServiceman().getFormInstances().remove(formInstance);

            formInstance.getFormTemplateMapping().getFormInstances().remove(formInstance);

            em.remove(formInstance);
            em.flush();

        } catch (DeleteFormInstanceException ex) {
            throw new DeleteFormInstanceException(errorMessage + ex.getMessage());
        } catch (PersistenceException ex) {
            throw new DeleteFormInstanceException(generalUnexpectedErrorMessage + "generate Form Instance [Persistence Exception]");
        } catch (Exception ex) {
            System.out.println(ex.getClass());
            ex.printStackTrace();
            throw new DeleteFormInstanceException(generalUnexpectedErrorMessage + "generate Form Instance");
        }
    }

    @Override
    public void updateFormInstanceFieldValues(FormInstance formInstance) throws UpdateFormInstanceException {
        String errorMessage = "Failed to update Form Instance Field Values: ";

        try {

            FormInstance fiPersisted = retrieveFormInstance(formInstance.getFormInstanceId());

            if (formInstance == null) {
                throw new UpdateFormInstanceException("Please supply an existing formInstanceId");
            }

            for (FormInstanceField fif : formInstance.getFormInstanceFields()) {
                
                for (FormInstanceField fifPersisted : fiPersisted.getFormInstanceFields()) {
                    
                    if(fif.getFormInstanceFieldId().equals(fifPersisted.getFormInstanceFieldId())) {
                        
                        List<FormInstanceFieldValue> newFifvs = new ArrayList<>();
                        
                        for(FormInstanceFieldValue fifv : fif.getFormInstanceFieldValues()) {
                            
                            FormInstanceFieldValue newFifv = new FormInstanceFieldValue(fifv.getInputValue());
                            em.persist(newFifv);
                            newFifvs.add(newFifv);
                            
                        }
                        
                        fifPersisted.setFormInstanceFieldValues(newFifvs);
                        
                    }
                    
                }
                
            }

        } catch (UpdateFormInstanceException ex) {
            throw new UpdateFormInstanceException(errorMessage + ex.getMessage());
        } catch (PersistenceException ex) {
            throw new UpdateFormInstanceException(generalUnexpectedErrorMessage + "update Form Instance Field Values [Persistence Exception]");
        } catch (Exception ex) {
            System.out.println(ex.getClass());
            ex.printStackTrace();
            throw new UpdateFormInstanceException(generalUnexpectedErrorMessage + "update Form Instance Field Values");
        }
    }

    @Override
    public List<FormInstance> retrieveServicemanFormInstances(Long servicemanId) {

        Query query = em.createQuery("SELECT f FROM FormInstance f WHERE f.serviceman.servicemanId = :id ");
        query.setParameter("id", servicemanId);

        return query.getResultList();

    }

    @Override
    public FormInstance retrieveFormInstance(Long id) {
        FormInstance formInstance = em.find(FormInstance.class, id);
        return formInstance;
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<FormInstance>> constraintViolations) {
        String msg = "Input data validation error!:";
        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        return msg;
    }

}
