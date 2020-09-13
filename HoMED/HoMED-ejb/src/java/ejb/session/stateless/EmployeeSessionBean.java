/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import util.exceptions.EmployeeNotFoundException;
import util.exceptions.EmployeeNricExistException;
import entity.Employee;
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
import util.exceptions.EmployeeInvalidLoginCredentialException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;
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
    public Long createEmployee(Employee employee) throws InputDataValidationException, UnknownPersistenceException, EmployeeNricExistException {
        try {
            Set<ConstraintViolation<Employee>> constraintViolations = validator.validate(employee);

            if (constraintViolations.isEmpty()) {
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
    public Employee retrieveEmployee(Long id) {
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
            throw new EmployeeInvalidLoginCredentialException("Username does not exist or invalid password!");
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
