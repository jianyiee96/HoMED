package ejb.session.stateless;

import util.exceptions.EmployeeNotFoundException;
import entity.Employee;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.Validator;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import util.exceptions.ActivateEmployeeException;
import util.exceptions.ChangeEmployeePasswordException;
import util.exceptions.CreateEmployeeException;
import util.exceptions.DeleteEmployeeException;
import util.exceptions.EmployeeInvalidLoginCredentialException;
import util.exceptions.ResetEmployeePasswordException;
import util.exceptions.UpdateEmployeeException;
import util.security.CryptographicHelper;

@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanLocal {

    @EJB
    private EmailSessionBeanLocal emailSessionBean;

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    private final String generalUnexpectedErrorMessage = "An unexpected error has occurred while ";

    public EmployeeSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public List<Employee> retrieveAllEmployees() {
        Query query = em.createQuery("SELECT e FROM Employee e");

        return query.getResultList();
    }

    @Override
    public Long createEmployeeByInit(Employee employee) throws CreateEmployeeException {
        String errorMessage = "Failed to create Employee: ";
        try {
            Set<ConstraintViolation<Employee>> constraintViolations = validator.validate(employee);

            if (constraintViolations.isEmpty()) {
                employee.setIsActivated(true);
                em.persist(employee);
                em.flush();

                return employee.getEmployeeId();
            } else {
                throw new CreateEmployeeException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (CreateEmployeeException ex) {
            throw new CreateEmployeeException(errorMessage + ex.getMessage());
        } catch (PersistenceException ex) {
            throw new CreateEmployeeException(preparePersistenceExceptionErrorMessage(ex));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CreateEmployeeException(generalUnexpectedErrorMessage + "creating employee account by init");
        }
    }

    // Creation of employee by admin (w OTP)
    @Override
    public String createEmployee(Employee employee) throws CreateEmployeeException {
        String errorMessage = "Failed to create Employee: ";
        try {

            String password = CryptographicHelper.getInstance().generateRandomString(8);
            employee.setPassword(password);

            Set<ConstraintViolation<Employee>> constraintViolations = validator.validate(employee);

            if (constraintViolations.isEmpty()) {
                em.persist(employee);
                em.flush();

                Future<Boolean> response = emailSessionBean.emailEmployeeOtpAsync(employee, password);
                if (!response.get()) {
                    throw new CreateEmployeeException("Email was not sent out successfully!");
                }
                return password;
            } else {
                throw new CreateEmployeeException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (CreateEmployeeException ex) {
            throw new CreateEmployeeException(errorMessage + ex.getMessage());
        } catch (PersistenceException ex) {
            throw new CreateEmployeeException(preparePersistenceExceptionErrorMessage(ex));
        } catch (ExecutionException | InterruptedException ex) {
            throw new CreateEmployeeException(errorMessage + "Email was not sent out successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CreateEmployeeException(generalUnexpectedErrorMessage + "creating employee account");
        }
    }

    @Override
    public Employee retrieveEmployeeById(Long id) throws EmployeeNotFoundException {
        Employee employee = em.find(Employee.class, id);

        if (employee != null) {
            return employee;
        } else {
            throw new EmployeeNotFoundException("Employee ID " + id + " does not exist!");
        }
    }

    @Override
    public Employee retrieveEmployeeByEmail(String email) throws EmployeeNotFoundException {
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.email = :inEmail");
        query.setParameter("inEmail", email);

        try {
            return (Employee) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new EmployeeNotFoundException("Employee Email " + email + " does not exist!");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new EmployeeNotFoundException(generalUnexpectedErrorMessage + "retrieving employee account by Email");
        }
    }

    @Override
    public Employee updateEmployee(Employee employee) throws UpdateEmployeeException {
        String errorMessage = "Failed to update Employee: ";
        try {
            if (employee != null && employee.getEmployeeId() != null) {
                Set<ConstraintViolation<Employee>> constraintViolations = validator.validate(employee);

                if (constraintViolations.isEmpty()) {
                    Employee employeeToUpdate = retrieveEmployeeById(employee.getEmployeeId());

                    Boolean emailChangeDetected = false;
                    if (!employeeToUpdate.getEmail().equals(employee.getEmail())) {
                        emailChangeDetected = true;
                    }

                    // Password are deliberately NOT updated to demonstrate that client is not allowed to update account credential through this business method
                    employeeToUpdate.setName(employee.getName());
                    employeeToUpdate.setIsActivated(employee.getIsActivated());

                    employeeToUpdate.setAddress(employee.getAddress());
                    employeeToUpdate.setEmail(employee.getEmail());
                    employeeToUpdate.setPhoneNumber(employee.getPhoneNumber());
                    employeeToUpdate.setGender(employee.getGender());

                    em.flush();

                    if (emailChangeDetected) {
                        Future<Boolean> response = emailSessionBean.emailEmployeeChangeEmailAsync(employee);
                        if (!response.get()) {
                            throw new UpdateEmployeeException("Email was not sent out successfully!");
                        }
                    }

                    return employeeToUpdate;

                } else {
                    throw new UpdateEmployeeException(prepareInputDataValidationErrorsMessage(constraintViolations));
                }
            } else {
                throw new UpdateEmployeeException("Employee information not found");
            }
        } catch (UpdateEmployeeException ex) {
            throw new UpdateEmployeeException(errorMessage + ex.getMessage());
        } catch (EmployeeNotFoundException ex) {
            throw new UpdateEmployeeException(errorMessage + ex.getMessage());
        } catch (PersistenceException ex) {
            throw new UpdateEmployeeException(preparePersistenceExceptionErrorMessage(ex));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new UpdateEmployeeException(generalUnexpectedErrorMessage + "updating employee account");
        }
    }

    @Override
    public void deleteEmployee(Long employeeId) throws DeleteEmployeeException {
        String errorMessage = "Failed to delete Employee: ";
        try {
            Employee employeeToRemove = retrieveEmployeeById(employeeId);
            //for reference when other entities are related to Employee
//        if(employeeToRemove.getSaleTransactionEntities().isEmpty())
//        {
//            entityManager.remove(employeeToRemove);
//        }
//        else
//        {
//            throw new DeleteStaffException("Staff ID " + employeeId + " is associated with existing sale transaction(s) and cannot be deleted!");
//        }
            // for now just remove employee
            em.remove(employeeToRemove);
        } catch (EmployeeNotFoundException ex) {
            throw new DeleteEmployeeException(errorMessage + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DeleteEmployeeException(generalUnexpectedErrorMessage + "deleting employee account");
        }
    }

    @Override
    public Employee employeeLogin(String email, String password) throws EmployeeInvalidLoginCredentialException {
        String errorMessage = "Failed to Login: ";
        try {
            Employee employee = retrieveEmployeeByEmail(email);
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + employee.getSalt()));

            if (employee.getPassword().equals(passwordHash)) {
                return employee;
            } else {
                throw new EmployeeInvalidLoginCredentialException("Email does not exist or invalid password!");
            }
        } catch (EmployeeInvalidLoginCredentialException ex) {
            throw new EmployeeInvalidLoginCredentialException(errorMessage + ex.getMessage());
        } catch (EmployeeNotFoundException ex) {
            throw new EmployeeInvalidLoginCredentialException(errorMessage + "Email does not exist or invalid password!");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new EmployeeInvalidLoginCredentialException(generalUnexpectedErrorMessage + "logging in");
        }
    }

    @Override
    public Employee activateEmployee(String email, String password, String rePassword) throws ActivateEmployeeException {
        String errorMessage = "Failed to activate Employee account: ";
        if (!password.equals(rePassword)) {
            throw new ActivateEmployeeException("Passwords do not match!");
        }

        try {
            Employee employee = retrieveEmployeeByEmail(email);
            // HANDLE NEW PASSWORD VALIDATION AT FRONTEND - Password min length
            if (employee.getIsActivated()) {
                throw new ActivateEmployeeException("Account has already been activated!");
            }

            employee.setPassword(password);
            employee.setIsActivated(true);
            return employee;
        } catch (ActivateEmployeeException ex) {
            throw new ActivateEmployeeException(errorMessage + ex.getMessage());
        } catch (EmployeeNotFoundException ex) {
            throw new ActivateEmployeeException(errorMessage + "Unable to find account details!");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ActivateEmployeeException(generalUnexpectedErrorMessage + "activating employee account");
        }
    }

    @Override
    public void changeEmployeePassword(String email, String oldPassword, String newPassword, String newRePassword) throws ChangeEmployeePasswordException {
        String errorMessage = "Failed to change Employee password: ";
        if (!newPassword.equals(newRePassword)) {
            throw new ChangeEmployeePasswordException("Passwords do not match!");
        }

        try {
            Employee employee = retrieveEmployeeByEmail(email);
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(oldPassword + employee.getSalt()));

            // HANDLE NEW PASSWORD VALIDATION AT FRONTEND - Password min length
            if (passwordHash.equals(employee.getPassword())) {
                if (oldPassword.equals(newPassword)) {
                    throw new ChangeEmployeePasswordException("New password cannot be the same as old password!");
                }

                employee.setPassword(newPassword);
                em.flush();
            } else {
                throw new ChangeEmployeePasswordException("Entered password do not match password associated with account!");
            }
        } catch (ChangeEmployeePasswordException ex) {
            throw new ChangeEmployeePasswordException(errorMessage + ex.getMessage());
        } catch (EmployeeNotFoundException ex) {
            throw new ChangeEmployeePasswordException(errorMessage + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ChangeEmployeePasswordException(generalUnexpectedErrorMessage + "changing password");
        }
    }

    @Override
    public void resetEmployeePassword(String email, String phoneNumber) throws ResetEmployeePasswordException {
        String errorMessage = "Failed to reset Employee password: ";
        try {
            Employee employee = retrieveEmployeeByEmail(email);
            if (!phoneNumber.equals(employee.getPhoneNumber())) {
                throw new ResetEmployeePasswordException("Phone number does not match account's phone number!");
            }

            String password = CryptographicHelper.getInstance().generateRandomString(8);
            employee.setPassword(password);
            employee.setIsActivated(false);

            Future<Boolean> response = emailSessionBean.emailEmployeeResetPasswordAsync(employee, password);
            if (!response.get()) {
                throw new ResetEmployeePasswordException("Email was not sent out successfully!");
            }
        } catch (ResetEmployeePasswordException ex) {
            throw new ResetEmployeePasswordException(errorMessage + ex.getMessage());
        } catch (EmployeeNotFoundException ex) {
            throw new ResetEmployeePasswordException(errorMessage + ex.getMessage());
        } catch (ExecutionException | InterruptedException ex) {
            throw new ResetEmployeePasswordException(errorMessage + "Email was not sent out successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ResetEmployeePasswordException(generalUnexpectedErrorMessage + "resetting password");
        }
    }

    @Override
    public Employee resetEmployeePasswordByAdmin(Employee currentEmployee) throws ResetEmployeePasswordException {
        String errorMessage = "Failed to reset Employee password: ";
        try {
            Employee employee = retrieveEmployeeByEmail(currentEmployee.getEmail());

            String password = CryptographicHelper.getInstance().generateRandomString(8);
            employee.setPassword(password);
            employee.setIsActivated(false);

            Future<Boolean> response = emailSessionBean.emailEmployeeResetPasswordAsync(employee, password);
            if (!response.get()) {
                throw new ResetEmployeePasswordException("Email was not sent out successfully!");
            }
            return employee;
        } catch (ResetEmployeePasswordException ex) {
            throw new ResetEmployeePasswordException(errorMessage + ex.getMessage());
        } catch (EmployeeNotFoundException ex) {
            throw new ResetEmployeePasswordException(errorMessage + ex.getMessage());
        } catch (ExecutionException | InterruptedException ex) {
            throw new ResetEmployeePasswordException(errorMessage + "Email was not sent out successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ResetEmployeePasswordException(generalUnexpectedErrorMessage + "resetting password by Admin");
        }
    }

    private String preparePersistenceExceptionErrorMessage(PersistenceException ex) {
        String result = "";

        if (ex.getCause() != null
                && ex.getCause().getCause() != null
                && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {

            if (ex.getCause().getCause().getMessage().contains("EMAIL")) {
                result += "Employee with same email address already exists\n";
            }
            if (ex.getCause().getCause().getMessage().contains("PHONE")) {
                result += "Employee with same phone number already exists\n";
            }
        } else {
            ex.printStackTrace();
            result = "An unexpected database error has occurred!";
        }

        return result;
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Employee>> constraintViolations) {
        String msg = "";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += constraintViolation.getMessage() + "\n";
        }

        msg = msg.substring(0, msg.length() - 1);

        return msg;
    }

}
