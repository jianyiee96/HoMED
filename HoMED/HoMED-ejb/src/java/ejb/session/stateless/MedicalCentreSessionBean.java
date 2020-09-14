/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.MedicalCentre;
import entity.OperatingHours;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

@Stateless
public class MedicalCentreSessionBean implements MedicalCentreSessionBeanLocal {

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    public MedicalCentreSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewMedicalCentre(MedicalCentre newMedicalCentre) throws InputDataValidationException, UnknownPersistenceException {
        try {
            Set<ConstraintViolation<MedicalCentre>> constraintViolations = validator.validate(newMedicalCentre);

            if (constraintViolations.isEmpty()) {
                em.persist(newMedicalCentre);
                em.flush();

                return newMedicalCentre.getMedicalCentreId();
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (PersistenceException ex) {
            throw new UnknownPersistenceException(ex.getMessage());
        }
    }
    
    @Override
    public Long createNewOperatingHours(OperatingHours newOperatingHours) {
        em.persist(newOperatingHours);
        em.flush();
        
        return newOperatingHours.getOperatingHoursId();
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<MedicalCentre>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
