/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import util.exceptions.DeleteFormInstanceException;
import entity.FormField;
import entity.FormInstance;
import entity.FormInstanceField;
import entity.FormInstanceFieldValue;
import entity.FormTemplate;
import entity.MedicalOfficer;
import entity.Notification;
import entity.Serviceman;
import java.text.SimpleDateFormat;
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
import util.enumeration.BookingStatusEnum;
import util.enumeration.ConsultationStatusEnum;
import util.enumeration.FormInstanceStatusEnum;
import util.enumeration.FormTemplateStatusEnum;
import util.enumeration.InputTypeEnum;
import util.enumeration.NotificationTypeEnum;
import util.exceptions.ArchiveFormInstanceException;
import util.exceptions.CreateNotificationException;
import util.exceptions.GenerateFormInstanceException;
import util.exceptions.SubmitFormInstanceException;
import util.exceptions.UpdateFormInstanceException;

/**
 *
 * @author User
 */
@Stateless
public class FormInstanceSessionBean implements FormInstanceSessionBeanLocal {

    @EJB(name = "NotificationSessionBeanLocal")
    private NotificationSessionBeanLocal notificationSessionBeanLocal;

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @EJB
    FormTemplateSessionBeanLocal formTemplateSessionBeanLocal;

    @EJB
    ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    private final String generalUnexpectedErrorMessage = "An unexpected error has occurred while ";
    
    String pattern = "EEEE, d MMM yyyy HH:mm";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

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
    public void deleteFormInstance(Long formInstanceId, Boolean systemCall) throws DeleteFormInstanceException {
        String errorMessage = "Failed to delete Form Instance: ";

        try {

            FormInstance formInstance = retrieveFormInstance(formInstanceId);

            if (formInstance == null) {
                throw new DeleteFormInstanceException("Please supply an existing formInstanceId");
            } else if (!systemCall && formInstance.getFormInstanceStatusEnum() != FormInstanceStatusEnum.DRAFT) {
                throw new DeleteFormInstanceException("Unable to delete submitted form instances");
            } else if (!systemCall && formInstance.getBooking() != null) {
                throw new DeleteFormInstanceException("Unable to delete form instance with link to booking");
            }

            for (FormInstanceField fif : formInstance.getFormInstanceFields()) {
                fif.setFormInstanceFieldValues(new ArrayList<>());
            }
            formInstance.setFormInstanceFields(new ArrayList<>());

            formInstance.getServiceman().getFormInstances().remove(formInstance);

            formInstance.getFormTemplateMapping().getFormInstances().remove(formInstance);

            if (formInstance.getBooking() != null) {

                formInstance.getBooking().getFormInstances().remove(formInstance);
                formInstance.setBooking(null);

            }

            if (formInstance.getSignedBy() != null) {

                formInstance.getSignedBy().getSignedFormInstances().remove(formInstance);
                formInstance.setSignedBy(null);

            }

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

                    if (fif.getFormInstanceFieldId().equals(fifPersisted.getFormInstanceFieldId())) {

                        List<FormInstanceFieldValue> newFifvs = new ArrayList<>();

                        for (FormInstanceFieldValue fifv : fif.getFormInstanceFieldValues()) {

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
    public void submitFormInstance(Long formInstanceId) throws SubmitFormInstanceException {
        FormInstance formInstance = retrieveFormInstance(formInstanceId);

        if (formInstance.getFormInstanceStatusEnum() != FormInstanceStatusEnum.DRAFT) {
            throw new SubmitFormInstanceException("Invalid Form Instance status: Status of form instance must be DRAFT");
        } else if (formInstance == null) {
            throw new SubmitFormInstanceException("Invalid Form Instance: Unable to find form instance in records");
        }

        // server-side validation
        String validationMessage = "Form Instance Validation Error:";
        boolean validationSuccess = true;

        for (FormInstanceField fif : formInstance.getFormInstanceFields()) {

            if (fif.getFormFieldMapping().getIsServicemanEditable()
                    && fif.getFormFieldMapping().getIsRequired()
                    && fif.getFormFieldMapping().getInputType() != InputTypeEnum.HEADER) { // not header, is required, is editabl -> must have content
                boolean hasContent = false;
                for (FormInstanceFieldValue fifv : fif.getFormInstanceFieldValues()) {
                    if (fifv.getInputValue() != null && !fifv.getInputValue().equals("")) {
                        hasContent = true;
                    }
                }

                if (!hasContent) {
                    validationMessage = validationMessage + "\nQuestion: " + fif.getFormFieldMapping().getQuestion() + " is required";
                    validationSuccess = false;
                }

            }

        }

        if (!validationSuccess) {
            throw new SubmitFormInstanceException(validationMessage);
        }

        formInstance.setFormInstanceStatusEnum(FormInstanceStatusEnum.SUBMITTED);
        formInstance.setDateSubmitted(new Date());
        
        String date = simpleDateFormat.format(formInstance.getDateSubmitted());
        
        String title = "Form Submitted Successfully";
        String body = "Form [ " + formInstance.getFormTemplateMapping().getFormTemplateName() + " ] has been submitted successfully on " + date + ".";
        
        Notification n = new Notification(title, body, NotificationTypeEnum.FORM, formInstanceId);
        
        try {
            notificationSessionBeanLocal.createNewNotification(n, formInstance.getServiceman().getServicemanId(), true);
            notificationSessionBeanLocal.sendPushNotification(title, body, formInstance.getServiceman().getFcmToken());
        } catch (CreateNotificationException ex) {
            System.out.println("> " + ex.getMessage());
        }
    }

    @Override
    public void submitFormInstanceByDoctor(FormInstance forminstanceToUpdate, Long medicalOfficerId) throws SubmitFormInstanceException {
        if (medicalOfficerId == null) {
            throw new SubmitFormInstanceException("Failed to sign form instance: Unable to retrieve doctor information");
        }

        MedicalOfficer mo = employeeSessionBeanLocal.retrieveMedicalOfficerById(medicalOfficerId);

        if (mo == null) {
            throw new SubmitFormInstanceException("Failed to sign form instance: Unable to retrieve doctor information");
        }

        try {
            updateFormInstanceFieldValues(forminstanceToUpdate);
        } catch (UpdateFormInstanceException ex) {
            throw new SubmitFormInstanceException(ex.getMessage());
        }
        FormInstance formInstance = retrieveFormInstance(forminstanceToUpdate.getFormInstanceId());
        formInstance.setSignedBy(mo);
        mo.getSignedFormInstances().add(formInstance);

        // server-side validation
        String validationMessage = "Form Instance Validation Error:";
        boolean validationSuccess = true;

        for (FormInstanceField fif : formInstance.getFormInstanceFields()) {

            if (fif.getFormFieldMapping().getIsRequired()
                    && fif.getFormFieldMapping().getInputType() != InputTypeEnum.HEADER) { // not header, is required
                boolean hasContent = false;
                for (FormInstanceFieldValue fifv : fif.getFormInstanceFieldValues()) {
                    if (fifv.getInputValue() != null && !fifv.getInputValue().equals("")) {
                        hasContent = true;
                    }
                }

                if (!hasContent) {
                    validationMessage = validationMessage + "\nQuestion: " + fif.getFormFieldMapping().getQuestion() + " is required";
                    validationSuccess = false;
                }

            }

        }

        if (!validationSuccess) {
            throw new SubmitFormInstanceException(validationMessage);
        }

    }

    @Override
    public void archiveFormInstance(Long formInstanceId) throws ArchiveFormInstanceException {
        FormInstance formInstance = retrieveFormInstance(formInstanceId);

        if (formInstance == null) {
            throw new ArchiveFormInstanceException("Invalid Form Instance: Unable to find form instance in records");
        } else if (formInstance.getFormInstanceStatusEnum() != FormInstanceStatusEnum.SUBMITTED) {
            throw new ArchiveFormInstanceException("Invalid Form Instance status: Status of form instance must be SUBMITTED");
        } else if (formInstance.getBooking() != null && formInstance.getBooking().getBookingStatusEnum() == BookingStatusEnum.UPCOMING) {
            throw new ArchiveFormInstanceException("Unable to Archive Form Instance: Form Instance is linked to an upcoming booking.");
        } else if (formInstance.getBooking() != null && formInstance.getBooking().getConsultation() != null && formInstance.getBooking().getConsultation().getConsultationStatusEnum() != ConsultationStatusEnum.COMPLETED) {
            throw new ArchiveFormInstanceException("Unable to Archive Form Instance: Form Instance is linked to a present consultation.");

        }

        formInstance.setFormInstanceStatusEnum(FormInstanceStatusEnum.ARCHIVED);
    }

    @Override
    public List<FormInstance> retrieveServicemanFormInstances(Long servicemanId) {

        Query query = em.createQuery("SELECT f FROM FormInstance f WHERE f.serviceman.servicemanId = :id ");
        query.setParameter("id", servicemanId);
        List<FormInstance> fis = query.getResultList();
        for (FormInstance fi : fis) {
            fi.getFormInstanceFields().sort((x, y) -> x.getFormFieldMapping().getPosition() - y.getFormFieldMapping().getPosition());
        }
        return fis;

    }

    @Override
    public FormInstance retrieveFormInstance(Long id) {
        FormInstance formInstance = em.find(FormInstance.class, id);
        formInstance.getFormInstanceFields().sort((x, y) -> x.getFormFieldMapping().getPosition() - y.getFormFieldMapping().getPosition());
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
