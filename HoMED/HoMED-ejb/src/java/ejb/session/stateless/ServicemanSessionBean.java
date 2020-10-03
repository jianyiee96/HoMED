package ejb.session.stateless;

import entity.Serviceman;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
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
import util.email.ServicemanEmailWrapper;
import util.exceptions.ActivateServicemanException;
import util.exceptions.ChangeServicemanPasswordException;
import util.exceptions.CreateServicemanException;
import util.exceptions.DeleteServicemanException;
import util.exceptions.ResetServicemanPasswordException;
import util.exceptions.ServicemanInvalidLoginCredentialException;
import util.exceptions.ServicemanNotFoundException;
import util.exceptions.UpdateServicemanException;
import util.security.CryptographicHelper;

@Stateless
public class ServicemanSessionBean implements ServicemanSessionBeanLocal {

    @EJB
    private EmailSessionBeanLocal emailSessionBean;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    private final String generalUnexpectedErrorMessage = "An unexpected error has occurred while ";

    public ServicemanSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public List<Serviceman> retrieveAllServicemen() {
        Query query = em.createQuery("SELECT s FROM Serviceman s");

        return query.getResultList();
    }

    @Override
    public HashMap<Serviceman, CreateServicemanException> bulkCreateServicemen(List<Serviceman> servicemen) {
        HashMap<Serviceman, CreateServicemanException> map = new HashMap<>();
        List<ServicemanEmailWrapper> list = new ArrayList<>();
        for (Serviceman serviceman : servicemen) {
            try {
                String password = CryptographicHelper.getInstance().generateRandomString(8);
                serviceman.setPassword(password);

                Set<ConstraintViolation<Serviceman>> constraintViolations = validator.validate(serviceman);

                if (constraintViolations.isEmpty()) {
                    em.persist(serviceman);
                    em.flush();

                    Future<Boolean> response = emailSessionBean.emailServicemanOtpAsync(serviceman, password);
                    list.add(new ServicemanEmailWrapper(response, serviceman));
                } else {
                    throw new CreateServicemanException(prepareInputDataValidationErrorsMessage(constraintViolations));
                }
            } catch (CreateServicemanException ex) {
                map.put(serviceman, new CreateServicemanException(ex.getMessage()));
            } catch (PersistenceException ex) {
                map.put(serviceman, new CreateServicemanException(preparePersistenceExceptionErrorMessage(ex)));
            } catch (Exception ex) {
                map.put(serviceman, new CreateServicemanException("Unknown exception!"));
            }
        }

        list.parallelStream()
                .peek(wrapper -> {
                    try {
                        Boolean result = wrapper.getEmailResult().get();
                        if (!result) {
                            throw new CreateServicemanException("Email was not sent out successfully!");
                        }
                    } catch (CreateServicemanException ex) {
                        map.put(wrapper.getServiceman(), new CreateServicemanException(ex.getMessage()));
                    } catch (Exception ex) {
                        map.put(wrapper.getServiceman(), new CreateServicemanException("Unknown exception!"));
                    }
                })
                .collect(Collectors.toList());
        return map;
    }

    @Override
    public String createServiceman(Serviceman newServiceman) throws CreateServicemanException {
        String errorMessage = "Failed to create Serivceman: ";
        try {

            String password = CryptographicHelper.getInstance().generateRandomString(8);
            newServiceman.setPassword(password);

            Set<ConstraintViolation<Serviceman>> constraintViolations = validator.validate(newServiceman);

            if (constraintViolations.isEmpty()) {
                em.persist(newServiceman);
                em.flush();

                Future<Boolean> response = emailSessionBean.emailServicemanOtpAsync(newServiceman, password);
                if (!response.get()) {
                    throw new CreateServicemanException("Email was not sent out successfully!");
                }
                return password;
            } else {
                throw new CreateServicemanException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (CreateServicemanException ex) {
            throw new CreateServicemanException(errorMessage + ex.getMessage());
        } catch (PersistenceException ex) {
            throw new CreateServicemanException(preparePersistenceExceptionErrorMessage(ex));
        } catch (ExecutionException | InterruptedException ex) {
            throw new CreateServicemanException(errorMessage + "Email was not sent out successfully");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CreateServicemanException(generalUnexpectedErrorMessage + "creating serviceman account");
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
    public Serviceman retrieveServicemanByEmail(String email) throws ServicemanNotFoundException {
        Query query = em.createQuery("SELECT s FROM Serviceman s WHERE s.email = :inEmail");
        query.setParameter("inEmail", email);

        try {
            return (Serviceman) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new ServicemanNotFoundException("Serviceman EMAIL " + email + " does not exist!");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServicemanNotFoundException(generalUnexpectedErrorMessage + "retrieving serviceman account by EMAIL");
        }
    }

    @Override
    public Serviceman updateServiceman(Serviceman serviceman) throws UpdateServicemanException {
        String errorMessage = "Failed to update Serviceman: ";
        try {
            if (serviceman != null && serviceman.getServicemanId() != null) {

                Set<ConstraintViolation<Serviceman>> constraintViolations = validator.validate(serviceman);
                if (constraintViolations.isEmpty()) {
                    Serviceman servicemanToUpdate = retrieveServicemanById(serviceman.getServicemanId());

                    Boolean emailChangeDetected = false;
                    if (!servicemanToUpdate.getEmail().equals(serviceman.getEmail())) {
                        emailChangeDetected = true;
                    }

                    servicemanToUpdate.setName(serviceman.getName());
                    servicemanToUpdate.setEmail(serviceman.getEmail());
                    servicemanToUpdate.setRod(serviceman.getRod());
                    servicemanToUpdate.setGender(serviceman.getGender());
                    servicemanToUpdate.setBloodType(serviceman.getBloodType());
                    servicemanToUpdate.setPhoneNumber(serviceman.getPhoneNumber());
                    servicemanToUpdate.setAddress(serviceman.getAddress());

                    em.flush();

                    if (emailChangeDetected) {
                        Future<Boolean> response = emailSessionBean.emailServicemanChangeEmailAsync(serviceman);
                        if (!response.get()) {
                            throw new UpdateServicemanException("Email was not sent out successfully!");
                        }
                    }

                    return servicemanToUpdate;

                } else {
                    throw new UpdateServicemanException(prepareInputDataValidationErrorsMessage(constraintViolations));
                }
            } else {
                throw new UpdateServicemanException("Serviceman information not found");
            }
        } catch (UpdateServicemanException ex) {
            throw new UpdateServicemanException(errorMessage + ex.getMessage());
        } catch (ServicemanNotFoundException ex) {
            throw new UpdateServicemanException(errorMessage + ex.getMessage());
        } catch (PersistenceException ex) {
            throw new UpdateServicemanException(preparePersistenceExceptionErrorMessage(ex));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new UpdateServicemanException(generalUnexpectedErrorMessage + "updating serviceman account");
        }
    }

    @Override
    public void deleteServiceman(Long servicemanId) throws DeleteServicemanException {
        String errorMessage = "Failed to delete Serviceman: ";
        try {
            Serviceman servicemanToRemove = retrieveServicemanById(servicemanId);
            em.remove(servicemanToRemove);
        } catch (ServicemanNotFoundException ex) {
            throw new DeleteServicemanException(errorMessage + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DeleteServicemanException(generalUnexpectedErrorMessage + "deleting serviceman account");
        }
    }

    @Override
    public Serviceman servicemanLogin(String email, String password) throws ServicemanInvalidLoginCredentialException {
        String errorMessage = "Failed to Login: ";
        try {
            Serviceman serviceman = retrieveServicemanByEmail(email);
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + serviceman.getSalt()));

            if (serviceman.getPassword().equals(passwordHash)) {
                return serviceman;
            } else {
                throw new ServicemanInvalidLoginCredentialException("EMAIL does not exist or invalid password!");
            }
        } catch (ServicemanInvalidLoginCredentialException ex) {
            throw new ServicemanInvalidLoginCredentialException(errorMessage + ex.getMessage());
        } catch (ServicemanNotFoundException ex) {
            throw new ServicemanInvalidLoginCredentialException(errorMessage + "EMAIL does not exist or invalid password!");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServicemanInvalidLoginCredentialException(generalUnexpectedErrorMessage + "logging in");
        }
    }

    @Override
    public void activateServiceman(String email, String password, String rePassword) throws ActivateServicemanException {
        String errorMessage = "Failed to activate Serviceman account: ";
        if (!password.equals(rePassword)) {
            throw new ActivateServicemanException("Passwords do not match!");
        }

        try {
            Serviceman serviceman = retrieveServicemanByEmail(email);
            // HANDLE NEW PASSWORD VALIDATION AT FRONTEND - Password min length
            if (serviceman.getIsActivated()) {
                throw new ActivateServicemanException("Account has already been activated!");
            }

            serviceman.setPassword(password);
            serviceman.setIsActivated(true);
        } catch (ActivateServicemanException ex) {
            throw new ActivateServicemanException(errorMessage + ex.getMessage());
        } catch (ServicemanNotFoundException ex) {
            throw new ActivateServicemanException(errorMessage + "Unable to find account details!");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ActivateServicemanException(generalUnexpectedErrorMessage + "activating serviceman account");
        }
    }

    @Override
    public void changeServicemanPassword(String email, String oldPassword, String newPassword, String newRePassword) throws ChangeServicemanPasswordException {
        String errorMessage = "Failed to change Serviceman password: ";
        if (!newPassword.equals(newRePassword)) {
            throw new ChangeServicemanPasswordException("Passwords do not match!");
        }

        try {
            Serviceman serviceman = retrieveServicemanByEmail(email);
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(oldPassword + serviceman.getSalt()));

            // HANDLE NEW PASSWORD VALIDATION AT FRONTEND - Password min length
            if (passwordHash.equals(serviceman.getPassword())) {
                if (oldPassword.equals(newPassword)) {
                    throw new ChangeServicemanPasswordException("New password cannot be the same as old password!");
                }

                serviceman.setPassword(newPassword);
                em.flush();
            } else {
                throw new ChangeServicemanPasswordException("Entered password do not match password associated with account!");
            }
        } catch (ChangeServicemanPasswordException ex) {
            throw new ChangeServicemanPasswordException(errorMessage + ex.getMessage());
        } catch (ServicemanNotFoundException ex) {
            throw new ChangeServicemanPasswordException(errorMessage + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ChangeServicemanPasswordException(generalUnexpectedErrorMessage + "changing password");
        }
    }

    @Override
    public void resetServicemanPassword(String email, String phoneNumber) throws ResetServicemanPasswordException {
        String errorMessage = "Failed to reset Serviceman password: ";
        try {
            Serviceman serviceman = retrieveServicemanByEmail(email);
            if (!phoneNumber.equals(serviceman.getPhoneNumber())) {
                throw new ResetServicemanPasswordException("Phone number does not match account's phone number!");
            }

            String password = CryptographicHelper.getInstance().generateRandomString(8);
            serviceman.setPassword(password);
            serviceman.setIsActivated(false);

            Future<Boolean> response = emailSessionBean.emailServicemanResetPasswordAsync(serviceman, password);
            if (!response.get()) {
                throw new ResetServicemanPasswordException("Email was not sent out successfully!");
            }
        } catch (ResetServicemanPasswordException ex) {
            throw new ResetServicemanPasswordException(errorMessage + ex.getMessage());
        } catch (ServicemanNotFoundException ex) {
            throw new ResetServicemanPasswordException(errorMessage + ex.getMessage());
        } catch (ExecutionException | InterruptedException ex) {
            throw new ResetServicemanPasswordException(errorMessage + "Email was not sent out successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ResetServicemanPasswordException(generalUnexpectedErrorMessage + "resetting password");
        }
    }

    @Override
    public Serviceman resetServicemanPasswordByAdmin(Serviceman currentServiceman) throws ResetServicemanPasswordException {
        String errorMessage = "Failed to reset Serviceman password: ";
        try {
            Serviceman serviceman = retrieveServicemanByEmail(currentServiceman.getEmail());

            String password = CryptographicHelper.getInstance().generateRandomString(8);
            serviceman.setPassword(password);
            serviceman.setIsActivated(false);

            Future<Boolean> response = emailSessionBean.emailServicemanResetPasswordAsync(serviceman, password);
            if (!response.get()) {
                throw new ResetServicemanPasswordException("Email was not sent out successfully!");
            }
            return serviceman;

        } catch (ResetServicemanPasswordException ex) {
            throw new ResetServicemanPasswordException(errorMessage + ex.getMessage());
        } catch (ServicemanNotFoundException ex) {
            throw new ResetServicemanPasswordException(errorMessage + ex.getMessage());
        } catch (ExecutionException | InterruptedException ex) {
            throw new ResetServicemanPasswordException(errorMessage + "Email was not sent out successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ResetServicemanPasswordException(generalUnexpectedErrorMessage + "resetting password by Admin");
        }
    }

    private String preparePersistenceExceptionErrorMessage(PersistenceException ex) {
        String result = "";

        if (ex.getCause() != null
                && ex.getCause().getCause() != null
                && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {

            if (ex.getCause().getCause().getMessage().contains("EMAIL")) {
                result += "Serivceman with same email address already exists\n";
            }
            if (ex.getCause().getCause().getMessage().contains("PHONE")) {
                result += "Serivceman with same phone number already exists\n";
            }
        } else {
            ex.printStackTrace();
            result = "An unexpected database error has occurred!";
        }

        return result;
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Serviceman>> constraintViolations) {
        String msg = "";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += constraintViolation.getMessage() + "\n";
        }

        msg = msg.substring(0, msg.length() - 1);

        return msg;
    }
}
