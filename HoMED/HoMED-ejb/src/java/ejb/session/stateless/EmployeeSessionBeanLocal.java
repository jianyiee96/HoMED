/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import javax.ejb.Local;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

/**
 *
 * @author User
 */
@Local
public interface EmployeeSessionBeanLocal {
    public Long createEmployee(Employee employee) throws InputDataValidationException, UnknownPersistenceException;
    
    public Employee retrieveEmployee(Long id);
}
