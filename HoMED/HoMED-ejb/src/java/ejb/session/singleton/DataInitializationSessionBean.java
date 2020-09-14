/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.FormTemplateSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Admin;
import entity.Clerk;
import entity.Employee;
import entity.FormTemplate;
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
    
    @EJB
    private FormTemplateSessionBeanLocal formTemplateSessionBeanLocal;
    
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
            Long empId1 = employeeSessionBeanLocal.createEmployee(new Admin("Admin 1", "s1234567a", "password"));
            Long empId2 = employeeSessionBeanLocal.createEmployee(new MedicalOfficer("Medical Officer 1", "s1234567b", "password"));
            Long empId3 = employeeSessionBeanLocal.createEmployee(new Clerk("Clerk 1", "s1234567c", "password"));
            
            Long servicemanId1 = servicemanSessionBeanLocal.createNewServiceman(new Serviceman("Amos Tan Ah Kow", "S9876543Z", new Date(), GenderEnum.MALE, BloodTypeEnum.BP, "amos@gmail.com", "13 Computing Drive"));
            Long servicemanId2 = servicemanSessionBeanLocal.createNewServiceman(new Serviceman("Brandon Tan Ah Kow", "S9876544Z", new Date(), GenderEnum.MALE, BloodTypeEnum.BP, "brandon@gmail.com", "14 Computing Drive"));
            Long servicemanId3 = servicemanSessionBeanLocal.createNewServiceman(new Serviceman("Charles Tan Ah Kow", "S9876545Z", new Date(), GenderEnum.MALE, BloodTypeEnum.BP, "charles@gmail.com", "15 Computing Drive"));
            
            Long formTemplateId1 = formTemplateSessionBeanLocal.createFormTemplate(new FormTemplate("Health Declaration"));            
            Long formTemplateId2 = formTemplateSessionBeanLocal.createFormTemplate(new FormTemplate("Travel Declaration"));
            Long formTemplateId3 = formTemplateSessionBeanLocal.createFormTemplate(new FormTemplate("Safe Distance Quiz"));

        } catch (InputDataValidationException | UnknownPersistenceException | ServicemanNricExistException | ServicemanEmailExistException | EmployeeNricExistException ex) {
            //ex.printStackTrace();
            System.out.println(ex.getMessage());
        } 
        
        
    }
}
