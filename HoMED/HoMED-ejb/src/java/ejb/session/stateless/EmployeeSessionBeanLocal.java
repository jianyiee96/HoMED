/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import javax.ejb.Local;
import util.exceptions.EmployeeInvalidLoginCredentialException;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.EmployeeNricExistException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

/**
 *
 * @author User
 */
@Local
public interface EmployeeSessionBeanLocal {
    
    public Long createEmployeeByInit(Employee employee) throws InputDataValidationException, UnknownPersistenceException, EmployeeNricExistException;
    
    public String createEmployee(Employee employee) throws InputDataValidationException, UnknownPersistenceException, EmployeeNricExistException;
    
    public Employee retrieveEmployee(Long id);
    
    public Employee retrieveEmployeeByNric(String nric) throws EmployeeNotFoundException;
    
    public Employee employeeLogin(String nric, String password) throws EmployeeInvalidLoginCredentialException;

}
