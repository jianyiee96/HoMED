/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;

/**
 *
 * @author User
 */
@Singleton
@LocalBean
@Startup
public class DataInitializationSessionBean {

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;
    
    @PostConstruct
    public void postConstruct() {
        if(employeeSessionBeanLocal.retrieveEmployee(1l) == null) {
           initializeData();
        } else {
            System.out.println("data exists.");
        }
    }

    private void initializeData() {
        
        try {   
            Long id = employeeSessionBeanLocal.createEmployee(new Employee("employee one"));
            System.out.println("Employee id: " + id);
        } catch (InputDataValidationException | UnknownPersistenceException ex) {
            //ex.printStackTrace();
            System.out.println(ex.getMessage());
        } 
        
        
    }
}
