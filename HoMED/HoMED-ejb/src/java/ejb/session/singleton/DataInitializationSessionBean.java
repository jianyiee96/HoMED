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
        if (employeeSessionBeanLocal.retrieveEmployeeById(1l) == null) {
            initializeData();
        } else {
            System.out.println("data exists");
        }
    }

    private void initializeData() {

        try {
        System.out.println("Start of data init");    
        
            Long empId1 = employeeSessionBeanLocal.createEmployeeByInit(new Admin("Admin 1", "s1234567a", "password", "vwqegwegm@hotmail.com", "1 Computing Drive", 98765432));
            Long empId2 = employeeSessionBeanLocal.createEmployeeByInit(new MedicalOfficer("Medical Officer 1", "s1234567b", "password", "eqeee@hotmail.com", "10 Heng Mui Kee", 81234567));
            Long empId3 = employeeSessionBeanLocal.createEmployeeByInit(new Clerk("Clerk 1", "s1234567c", "password", "hjejrt@hotmail.com", "28 Jalan Klinik", 88888888));

            

            String employee1OTP = employeeSessionBeanLocal.createEmployee(new Admin("Admin OTP", "s1234567d","bryan.thum@hotmail.com" ,"30 Jalan Klinik", 94362875));
            String employee2OTP = employeeSessionBeanLocal.createEmployee(new MedicalOfficer("MO OTP", "s1234567e", "eqwegswnbewqe@hotmail.com","50 Jalan Jalan", 94360875));
            String employee3OTP = employeeSessionBeanLocal.createEmployee(new Clerk("Clerk OTP", "s1234567f","eqwjherqe@hotmail.com" ,"120 Jalan Bedok", 94326975));
            System.out.println("Employee NRIC: s1234567d\tOTP " + employee1OTP);
            System.out.println("Employee NRIC: s1234567e\tOTP " + employee2OTP);
            System.out.println("Employee NRIC: s1234567f\tOTP " + employee3OTP);

            String serviceman1OTP = servicemanSessionBeanLocal.createNewServiceman(new Serviceman("Amos Tan Ah Kow", "s7654321d", "98765432", new Date(), GenderEnum.MALE, BloodTypeEnum.BP, "bryan.thum@hotmail.com", "13 Computing Drive"));
            String serviceman2OTP = servicemanSessionBeanLocal.createNewServiceman(new Serviceman("Brandon Tan Ah Kow", "S7654321e", "98765434", new Date(), GenderEnum.MALE, BloodTypeEnum.BP, "brandon@gmail.com", "14 Computing Drive"));
            String serviceman3OTP = servicemanSessionBeanLocal.createNewServiceman(new Serviceman("Charles Tan Ah Kow", "S7654321f", "98765435", new Date(), GenderEnum.MALE, BloodTypeEnum.BP, "charles@gmail.com", "15 Computing Drive"));

            System.out.println("Serviceman NRIC: s7654321d\tOTP : " + serviceman1OTP);
            System.out.println("Serviceman NRIC: s7654321e\tOTP : " + serviceman2OTP);
            System.out.println("Serviceman NRIC: s7654321f\tOTP : " + serviceman3OTP);

            System.out.println("End of data init");
        } catch (InputDataValidationException | UnknownPersistenceException | ServicemanNricExistException | ServicemanEmailExistException | EmployeeNricExistException ex) {
            //ex.printStackTrace();
            System.out.println(ex.getMessage());
        }

    }
}
