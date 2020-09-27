/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.ConsultationPurpose;
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
import util.exceptions.CreateConsultationPurposeException;

@Stateless
public class ConsultationPurposeSessionBean implements ConsultationPurposeSessionBeanLocal {

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    private final String generalUnexpectedErrorMessage = "An unexpected error has occurred while ";

    public ConsultationPurposeSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createConsultationPurpose(ConsultationPurpose consultationPurpose) throws CreateConsultationPurposeException {
        String errorMessage = "Failed to create Consultation Purpose: ";

        try {
            Set<ConstraintViolation<ConsultationPurpose>> constraintViolations = validator.validate(consultationPurpose);

            if (constraintViolations.isEmpty()) {
                em.persist(consultationPurpose);
                em.flush();

                return consultationPurpose.getConsultationPurposeId();
            } else {
                throw new CreateConsultationPurposeException(prepareInputDataValidationErrorsMessage(constraintViolations));

            }
        } catch (CreateConsultationPurposeException ex) {
            throw new CreateConsultationPurposeException(errorMessage + ex.getMessage());

        } catch (PersistenceException ex) {
            throw new CreateConsultationPurposeException(generalUnexpectedErrorMessage + "creating Consultation Purpose [Persistence Exception]");
        } catch (Exception ex) {
            System.out.println(ex.getClass());
            ex.printStackTrace();
            throw new CreateConsultationPurposeException(generalUnexpectedErrorMessage + "creating Consultation Purpose");
        }
    }

    @Override
    public void relinkFormTemplates(Long id, List<FormTemplate> formTemplates) {

        ConsultationPurpose cp = retrieveConsultationPurpose(id);

        List<FormTemplate> currentFormTemplates = cp.getFormTemplates();

        for (FormTemplate ftLink : formTemplates) {
            em.merge(ftLink);
        }

        // Unlink All
        for (FormTemplate ftUnlink : currentFormTemplates) {
            ftUnlink.getConsultationPurposes().remove(cp);
        }

        // Link 
        cp.setFormTemplates(formTemplates);
        for (FormTemplate ftLink : formTemplates) {
            ftLink.getConsultationPurposes().add(cp);
        }

    }

    @Override
    public ConsultationPurpose retrieveConsultationPurpose(Long id) {
        ConsultationPurpose consultationPurpose = em.find(ConsultationPurpose.class, id);
        return consultationPurpose;
    }

    @Override
    public List<ConsultationPurpose> retrieveAllConsultationPurposes() {
        Query query = em.createQuery("SELECT c FROM ConsultationPurpose c");
        List<ConsultationPurpose> cps = query.getResultList();
        for (ConsultationPurpose cp : cps) {
            cp.getFormTemplates().size();
        }
        return cps;
    }

    @Override
    public List<ConsultationPurpose> retrieveAllFormTemplateLinkedConsultationPurposes(Long id) {
        Query query = em.createQuery("SELECT distinct c FROM ConsultationPurpose c JOIN c.formTemplates ft WHERE ft.formTemplateId = :id");
        query.setParameter("id", id);
        List<ConsultationPurpose> cps = query.getResultList();
        for (ConsultationPurpose cp : cps) {
            cp.getFormTemplates().size();
        }
        return cps;
    }

    @Override
    public void deleteConsultationPurpose(Long id) {
        
        ConsultationPurpose cp = retrieveConsultationPurpose(id);

        List<FormTemplate> currentFormTemplates = cp.getFormTemplates();

        // Unlink All
        for (FormTemplate ftUnlink : currentFormTemplates) {
            ftUnlink.getConsultationPurposes().remove(cp);
        }
        
        em.remove(cp);
        
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<ConsultationPurpose>> constraintViolations) {
        String msg = "";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += constraintViolation.getMessage() + "\n";
        }

        msg = msg.substring(0, msg.length() - 1);

        return msg;
    }

}
