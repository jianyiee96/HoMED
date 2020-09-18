/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Serviceman;
import java.util.Set;
import javax.ejb.EJB;
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
import util.exceptions.ServicemanInvalidPasswordException;
import util.exceptions.ServicemanNotFoundException;
import util.exceptions.ServicemanNricExistException;
import util.exceptions.UnknownPersistenceException;
import util.exceptions.UpdateServicemanException;
import util.security.CryptographicHelper;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
@Stateless
public class ServicemanSessionBean implements ServicemanSessionBeanLocal {

    @EJB
    private EmailSessionBeanLocal emailSessionBean;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    public ServicemanSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public String createNewServiceman(Serviceman newServiceman) throws InputDataValidationException, ServicemanNricExistException, ServicemanEmailExistException, UnknownPersistenceException {
        try {

            String password = CryptographicHelper.getInstance().generateRandomString(8);
            newServiceman.setPassword(password);
            Set<ConstraintViolation<Serviceman>> constraintViolations = validator.validate(newServiceman);

            if (constraintViolations.isEmpty()) {
                em.persist(newServiceman);
                em.flush();
                // COMMENT THIS CHUNCK TO PREVENT EMAIL
                try {
                    emailSessionBean.emailServicemanOtpAsync(newServiceman, password);
                } catch (InterruptedException ex) {
                    // EMAIL NOT SENT OUT SUCCESSFULLY
                }
                return password;
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }

        } catch (PersistenceException ex) {
            throw new UnknownPersistenceException(ex.getMessage());
        }
    }

    @Override
    public Serviceman retrieveServicemanById(Long servicemanId) throws ServicemanNotFoundException {
        Serviceman serviceman = em.find(Serviceman.class, servicemanId);

        if (serviceman != null) {
            return serviceman;
        } else {
            throw new ServicemanNotFoundException("Serviceman ID " + servicemanId + " does not exist!");
        }
    }

    @Override
    public Serviceman retrieveServicemanByNric(String nric) throws ServicemanNotFoundException {

        Query query = em.createQuery("SELECT s FROM Serviceman s WHERE s.nric = :inNric");
        query.setParameter("inNric", nric);

        try {
            return (Serviceman) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new ServicemanNotFoundException("Serviceman NRIC " + nric + " does not exist!");
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
            throw new ServicemanInvalidLoginCredentialException("NRIC does not exist or invalid password!");
        }
    }

    @Override
    public void changePassword(String nric, String oldPassword, String newPassword) throws ServicemanInvalidPasswordException, ServicemanNotFoundException {
        try {
            Serviceman serviceman = retrieveServicemanByNric(nric);
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(oldPassword + serviceman.getSalt()));

            if (passwordHash.equals(serviceman.getPassword())) {
                serviceman.setPassword(newPassword);
                serviceman.setIsActivated(true);
                em.flush();
            } else {
                throw new ServicemanInvalidPasswordException("Entered password do not match password associated with account!");
            }

        } catch (ServicemanNotFoundException ex) {
            throw new ServicemanNotFoundException("Serviceman NRIC " + nric + " does not exist!");
        }
    }

    @Override
    public Serviceman updateServiceman(Serviceman serviceman) throws ServicemanNotFoundException, ServicemanInvalidLoginCredentialException, UpdateServicemanException, InputDataValidationException, UnknownPersistenceException {
        if (serviceman != null && serviceman.getServicemanId() != null) {

            Set<ConstraintViolation<Serviceman>> constraintViolations = validator.validate(serviceman);
            try {
                if (constraintViolations.isEmpty()) {
                    Serviceman servicemanToUpdate = retrieveServicemanById(serviceman.getServicemanId());
                    if (servicemanToUpdate.getNric().equals(serviceman.getNric())) {

                        servicemanToUpdate.setEmail(serviceman.getEmail());
                        servicemanToUpdate.setPhoneNumber(serviceman.getPhoneNumber());
                        servicemanToUpdate.setAddress(serviceman.getAddress());

                        em.merge(servicemanToUpdate);
                        em.flush();

                        return servicemanToUpdate;

                    } else {
                        throw new UpdateServicemanException("NRIC of serviceman record to be updated does not match the existing record!");
                    }
                } else {
                    throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
                }
            } catch (PersistenceException ex) {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        } else {
            throw new ServicemanNotFoundException("Serviceman ID not provided for serviceman to be updated!");
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
