/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Admin;
import entity.Clerk;
import entity.Employee;
import entity.MedicalOfficer;
import entity.Serviceman;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.BloodTypeEnum;
import util.enumeration.GenderEnum;
import util.exceptions.EmployeeNricExistException;
import util.exceptions.InputDataValidationException;
import util.exceptions.ServicemanEmailExistException;
import util.exceptions.ServicemanNricExistException;
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
    
    @EJB
    private ServicemanSessionBeanLocal servicemanSessionBeanLocal;
    
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
            Long id = employeeSessionBeanLocal.createEmployee(new Admin("Admin one", "s9771019h", "password"));
            System.out.println("Employee id: " + id);
            employeeSessionBeanLocal.createEmployee(new Clerk("Clerk one", "s0000000a", "password"));
            employeeSessionBeanLocal.createEmployee(new MedicalOfficer("MO one", "s0000000b", "password"));
            
            Long servicemanId1 = servicemanSessionBeanLocal.createNewServiceman(new Serviceman("Amos Tan Ah Kow", "S9876543Z", new Date(), GenderEnum.MALE, BloodTypeEnum.BP, "password", "bob@gmail.com", "13 Computing Drive"));
            
        } catch (InputDataValidationException | UnknownPersistenceException | ServicemanNricExistException | ServicemanEmailExistException | EmployeeNricExistException ex) {
            //ex.printStackTrace();
            System.out.println(ex.getMessage());
        } 
        
        
    }
}
