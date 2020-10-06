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
import util.exceptions.ChangeEmployeePasswordException;
import util.exceptions.CreateEmployeeException;
import util.exceptions.DeleteEmployeeException;
import util.exceptions.EmployeeInvalidLoginCredentialException;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.ResetEmployeePasswordException;
import util.exceptions.UpdateEmployeeException;

/**
 *
 * @author User
 */
@Local
public interface EmployeeSessionBeanLocal {

    public List<Employee> retrieveAllEmployees();

    public Long createEmployeeByInit(Employee employee) throws CreateEmployeeException;

    public String createEmployee(Employee employee) throws CreateEmployeeException;

    public Employee retrieveEmployeeById(Long id) throws EmployeeNotFoundException;

    public Employee retrieveEmployeeByEmail(String email) throws EmployeeNotFoundException;

    public Employee updateEmployee(Employee employee) throws UpdateEmployeeException;

    public void deleteEmployee(Long employeeId) throws DeleteEmployeeException;

    public Employee employeeLogin(String email, String password) throws EmployeeInvalidLoginCredentialException;

    public Employee activateEmployee(String email, String password, String rePassword) throws ActivateEmployeeException;

    public void changeEmployeePassword(String email, String oldPassword, String newPassword, String newRePassword) throws ChangeEmployeePasswordException;

    public void resetEmployeePassword(String email, String phoneNumber) throws ResetEmployeePasswordException;

    public Employee resetEmployeePasswordBySuperUser(Employee currentEmployee) throws ResetEmployeePasswordException;

}
