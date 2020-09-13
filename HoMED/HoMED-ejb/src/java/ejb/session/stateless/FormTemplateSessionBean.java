/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Employee;
import entity.FormTemplate;
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
    public FormTemplate retrieveFormTemplate(Long id) {
        FormTemplate formTemplate = em.find(FormTemplate.class, id);
        return formTemplate;
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
