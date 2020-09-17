/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.ActivateEmployeeException;
import util.exceptions.EmployeeInvalidLoginCredentialException;
import util.exceptions.EmployeeInvalidPasswordException;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.EmployeeNricExistException;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;
import util.exceptions.UpdateEmployeeException;

/**
 *
 * @author User
 */
@Local
public interface EmployeeSessionBeanLocal {
    
    public Long createEmployeeByInit(Employee employee) throws InputDataValidationException, UnknownPersistenceException, EmployeeNricExistException;
    
    public String createEmployee(Employee employee) throws InputDataValidationException, UnknownPersistenceException, EmployeeNricExistException;
    
    public Employee retrieveEmployeeById(Long id);
    
    public Employee retrieveEmployeeByNric(String nric) throws EmployeeNotFoundException;
    
    public Employee employeeLogin(String nric, String password) throws EmployeeInvalidLoginCredentialException;
    
    public List<Employee> retrieveAllStaffs();

    public void updateEmployee(Employee employee) throws EmployeeNotFoundException, UpdateEmployeeException, InputDataValidationException;

    public Employee activateEmployee(String nric, String password, String rePassword) throws ActivateEmployeeException;

    public void changePassword(String nric, String oldPassword, String newPassword) throws EmployeeInvalidPasswordException, EmployeeNotFoundException;
}
