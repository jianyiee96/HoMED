/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import util.exceptions.EmployeeNotFoundException;
import util.exceptions.EmployeeNricExistException;
import entity.Employee;
import java.util.List;
import java.util.Set;
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
import util.exceptions.DeleteEmployeeException;
import util.exceptions.EmployeeInvalidLoginCredentialException;
import util.exceptions.EmployeeInvalidPasswordException;
import util.exceptions.InputDataValidationException;
import util.exceptions.ServicemanNotFoundException;
import util.exceptions.UnknownPersistenceException;
import util.exceptions.UpdateEmployeeException;
import util.security.CryptographicHelper;

/**
 *
 * @author User
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public EmployeeSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createEmployeeByInit(Employee employee) throws InputDataValidationException, UnknownPersistenceException, EmployeeNricExistException {
        try {
            Set<ConstraintViolation<Employee>> constraintViolations = validator.validate(employee);

            if (constraintViolations.isEmpty()) {
                employee.setIsActivated(true);
                em.persist(employee);
                em.flush();

                return employee.getEmployeeId();
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new EmployeeNricExistException("Employee NRIC already exists!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Employee> retrieveAllStaffs() {
        Query query = em.createQuery("SELECT e FROM Employee e");

        return query.getResultList();
    }

    // Creation of employee by admin (w OTP)
    @Override
    public String createEmployee(Employee employee) throws InputDataValidationException, UnknownPersistenceException, EmployeeNricExistException {
        try {

            String password = CryptographicHelper.getInstance().generateRandomString(8);
            employee.setPassword(password);
            Set<ConstraintViolation<Employee>> constraintViolations = validator.validate(employee);
            if (constraintViolations.isEmpty()) {
                em.persist(employee);
                em.flush();

                return password;
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new EmployeeNricExistException("Employee NRIC already exists!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    @Override
    public Employee retrieveEmployeeById(Long id) {
        Employee employee = em.find(Employee.class, id);
        return employee;
    }

    @Override
    public Employee retrieveEmployeeByNric(String nric) throws EmployeeNotFoundException {

        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.nric = :inNric");
        query.setParameter("inNric", nric);

        try {
            return (Employee) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new EmployeeNotFoundException("Serviceman Nric " + nric + " does not exist!");
        }
    }

    @Override
    public void updateEmployee(Employee employee) throws EmployeeNotFoundException, UpdateEmployeeException, InputDataValidationException {
        if (employee != null && employee.getEmployeeId() != null) {
            Set<ConstraintViolation<Employee>> constraintViolations = validator.validate(employee);

            if (constraintViolations.isEmpty()) {
                Employee employeeToUpdate = retrieveEmployeeByNric(employee.getNric());

                if (employeeToUpdate.getNric().equals(employee.getNric())) {
                    // Nric and password are deliberately NOT updated to demonstrate that client is not allowed to update account credential through this business method
                    employeeToUpdate.setAddress(employee.getAddress());
                    employeeToUpdate.setPhoneNumber(employee.getPhoneNumber());
                    employeeToUpdate.setEmail(employee.getEmail());
                    employeeToUpdate.setGender(employee.getGender());

                } else {
                    throw new UpdateEmployeeException("Nric of employee record to be updated does not match the existing record");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new EmployeeNotFoundException("Employee ID not provided for staff to be updated");
        }
    }

    public void deleteEmployee(Long employeeId) throws EmployeeNotFoundException, DeleteEmployeeException {
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
    }

    @Override
    public Employee employeeLogin(String nric, String password) throws EmployeeInvalidLoginCredentialException {
        try {
            Employee employee = retrieveEmployeeByNric(nric);
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + employee.getSalt()));

            if (employee.getPassword().equals(passwordHash)) {
                return employee;
            } else {
                throw new EmployeeInvalidLoginCredentialException("NRIC does not exist or invalid password!");
            }
        } catch (EmployeeNotFoundException ex) {
            throw new EmployeeInvalidLoginCredentialException("NRIC does not exist or invalid password!");
        }
    }

    @Override
    public Employee activateEmployee(String nric, String password, String rePassword) throws ActivateEmployeeException {
        if (!password.equals(rePassword)) {
            throw new ActivateEmployeeException("Passwords do not match!");
        }

        try {
            Employee employee = retrieveEmployeeByNric(nric);
            // HANDLE NEW PASSWORD VALIDATION AT FRONTEND

            employee.setPassword(password);
            employee.setIsActivated(true);
            return employee;
        } catch (EmployeeNotFoundException ex) {
            throw new ActivateEmployeeException("NRIC does not exist in our system! Please try again.");
        }
    }

    @Override
    public void changePassword(String nric, String oldPassword, String newPassword) throws EmployeeInvalidPasswordException, EmployeeNotFoundException {
        try {
            Employee employee = retrieveEmployeeByNric(nric);
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(oldPassword + employee.getSalt()));

            if (passwordHash.equals(employee.getPassword())) {
                employee.setPassword(newPassword);
                employee.setIsActivated(true);
                em.flush();
            } else {
                throw new EmployeeInvalidPasswordException("Entered password do not match password associated with account!");
            }

        } catch (EmployeeNotFoundException ex) {
            throw new EmployeeNotFoundException("Serviceman NRIC " + nric + " does not exist!");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Employee>> constraintViolations) {
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
