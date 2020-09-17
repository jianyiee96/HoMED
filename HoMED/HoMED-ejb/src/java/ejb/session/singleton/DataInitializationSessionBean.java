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
            System.out.println("data exists");
        }
    }

    private void initializeData() {
        
        try {
            System.out.println("Start of data init");
            
            Long empId1 = employeeSessionBeanLocal.createEmployee(new Admin("Admin 1", "s1234567a", "password"));
            Long empId2 = employeeSessionBeanLocal.createEmployee(new MedicalOfficer("Medical Officer 1", "s1234567b", "password"));
            Long empId3 = employeeSessionBeanLocal.createEmployee(new Clerk("Clerk 1", "s1234567c", "password"));
            
            String serviceman1OTP = servicemanSessionBeanLocal.createNewServiceman(new Serviceman("Amos Tan Ah Kow", "S9876543Z", "98765432", new Date(), GenderEnum.MALE, BloodTypeEnum.BP, "amos@gmail.com", "13 Computing Drive"));
            String serviceman2OTP = servicemanSessionBeanLocal.createNewServiceman(new Serviceman("Brandon Tan Ah Kow", "S9876544Z", "98765434", new Date(), GenderEnum.MALE, BloodTypeEnum.BP, "brandon@gmail.com", "14 Computing Drive"));
            String serviceman3OTP = servicemanSessionBeanLocal.createNewServiceman(new Serviceman("Charles Tan Ah Kow", "S9876545Z", "98765435", new Date(), GenderEnum.MALE, BloodTypeEnum.BP, "charles@gmail.com", "15 Computing Drive"));
            
            System.out.println("Serviceman 1 : " + serviceman1OTP);
            System.out.println("Serviceman 2 : " + serviceman2OTP);
            System.out.println("Serviceman 3 : " + serviceman3OTP);
            
            System.out.println("End of data init");
        } catch (InputDataValidationException | UnknownPersistenceException | ServicemanNricExistException | ServicemanEmailExistException | EmployeeNricExistException ex) {
            //ex.printStackTrace();
            System.out.println(ex.getMessage());
        } 
        
        
    }
}
