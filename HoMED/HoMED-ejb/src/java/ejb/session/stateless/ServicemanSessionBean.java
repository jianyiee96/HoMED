/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Serviceman;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exceptions.InputDataValidationException;
import util.exceptions.ServicemanEmailExistException;
import util.exceptions.ServicemanInvalidLoginCredentialException;
import util.exceptions.ServicemanNotFoundException;
import util.exceptions.ServicemanNricExistException;
import util.exceptions.UnknownPersistenceException;
import util.security.CryptographicHelper;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
@Stateless
public class ServicemanSessionBean implements ServicemanSessionBeanLocal {

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    public ServicemanSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewServiceman(Serviceman newServiceman) throws InputDataValidationException, ServicemanNricExistException, ServicemanEmailExistException, UnknownPersistenceException {
        try {
            Set<ConstraintViolation<Serviceman>> constraintViolations = validator.validate(newServiceman);

            if (constraintViolations.isEmpty()) {
                em.persist(newServiceman);
                em.flush();
                return newServiceman.getServicemanId();
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }

        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new ServicemanNricExistException("Serviceman NRIC already exists!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    @Override
    public Serviceman retrieveServicemanByNric(String nric) throws ServicemanNotFoundException {

        Query query = em.createQuery("SELECT s FROM Serviceman s WHERE s.nric = :inNric");
        query.setParameter("inNric", nric);

        try {
            return (Serviceman) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new ServicemanNotFoundException("Serviceman Nric " + nric + " does not exist!");
        }
    }

    @Override
    public Serviceman servicemanLogin(String nric, String password) throws ServicemanInvalidLoginCredentialException {
        try {
            Serviceman serviceman = retrieveServicemanByNric(nric);
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + serviceman.getSalt()));

            if (serviceman.getPassword().equals(passwordHash)) {
                return serviceman;
            } else {
                throw new ServicemanInvalidLoginCredentialException("NRIC does not exist or invalid password!");
            }
        } catch (ServicemanNotFoundException ex) {
            throw new ServicemanInvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Serviceman>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
