/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.MedicalCentre;
import entity.OperatingHours;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
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
                
                for (OperatingHours oh : newMedicalCentre.getOperatingHours()) {
                    em.persist(oh);
                    em.flush();
                }                
                
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
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Long createNewOperatingHours(OperatingHours newOperatingHours) {
        em.persist(newOperatingHours);
        em.flush();
        
        return newOperatingHours.getOperatingHoursId();
    }
    
    @Override
    public List<MedicalCentre> retrieveAllMedicalCentres() {
        Query query = em.createQuery("SELECT mc FROM MedicalCentre mc ORDER BY mc.name ASC");
        return query.getResultList();
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<MedicalCentre>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
