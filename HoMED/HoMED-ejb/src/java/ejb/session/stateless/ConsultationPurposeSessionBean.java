/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.ConsultationPurpose;
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
    public ConsultationPurpose retrieveConsultationPurpose(Long id) {
        ConsultationPurpose consultationPurpose = em.find(ConsultationPurpose.class, id);
        return consultationPurpose;
    }

    @Override
    public List<ConsultationPurpose> retrieveAllConsultationPurposes() {
        Query query = em.createQuery("SELECT c FROM ConsultationPurpose c");
        return query.getResultList();
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
