/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.MedicalCentreSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Employee;
import entity.MedicalCentre;
import entity.OperatingHours;
import entity.Serviceman;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.BloodTypeEnum;
import util.enumeration.DayOfWeekEnum;
import util.enumeration.GenderEnum;
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
    @EJB(name = "MedicalCentreSessionBeanLocal")
    private MedicalCentreSessionBeanLocal medicalCentreSessionBeanLocal;
    @EJB
    private ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    @PostConstruct
    public void postConstruct() {
        if (employeeSessionBeanLocal.retrieveEmployee(1l) == null) {
            initializeData();
        } else {
            System.out.println("data exists.");
        }
    }

    private void initializeData() {

        try {
            Long id = employeeSessionBeanLocal.createEmployee(new Employee("employee one"));
            System.out.println("Employee id: " + id);

            Long servicemanId1 = servicemanSessionBeanLocal.createNewServiceman(new Serviceman("Amos Tan Ah Kow", "S9876543Z", new Date(), GenderEnum.MALE, BloodTypeEnum.BP, "password", "bob@gmail.com", "13 Computing Drive"));

            String medicalCentreName = "HOME TEAM ACADEMY MEDICAL CENTRE";
            String medicalCentrePhone = "64653921";
            // Street Name
            // Unit Number
            // Building Name
            // Country
            // Postal Code
            // Delimited by "|@@|
            String medicalCentreAddress = "501 OLD CHOA CHU KANG ROAD|@@|#01-00|@@||@@|Singapore|@@|698928";
            List<OperatingHours> medicalCentreOperatingHours = new ArrayList<>();
            OperatingHours operatingHours;
            operatingHours = new OperatingHours(DayOfWeekEnum.MONDAY, LocalTime.of(8, 30), LocalTime.of(17, 30));
            medicalCentreSessionBeanLocal.createNewOperatingHours(operatingHours);
            medicalCentreOperatingHours.add(operatingHours);

            operatingHours = new OperatingHours(DayOfWeekEnum.TUESDAY, LocalTime.of(8, 30), LocalTime.of(17, 30));
            medicalCentreSessionBeanLocal.createNewOperatingHours(operatingHours);
            medicalCentreOperatingHours.add(operatingHours);

            operatingHours = new OperatingHours(DayOfWeekEnum.WEDNESDAY, LocalTime.of(8, 30), LocalTime.of(17, 30));
            medicalCentreSessionBeanLocal.createNewOperatingHours(operatingHours);
            medicalCentreOperatingHours.add(operatingHours);

            operatingHours = new OperatingHours(DayOfWeekEnum.THURSDAY, LocalTime.of(8, 30), LocalTime.of(17, 30));
            medicalCentreSessionBeanLocal.createNewOperatingHours(operatingHours);
            medicalCentreOperatingHours.add(operatingHours);

            operatingHours = new OperatingHours(DayOfWeekEnum.FRIDAY, LocalTime.of(8, 30), LocalTime.of(17, 30));
            medicalCentreSessionBeanLocal.createNewOperatingHours(operatingHours);
            medicalCentreOperatingHours.add(operatingHours);

            operatingHours = new OperatingHours(DayOfWeekEnum.SATURDAY, LocalTime.of(8, 30), LocalTime.of(13, 30));
            medicalCentreSessionBeanLocal.createNewOperatingHours(operatingHours);
            medicalCentreOperatingHours.add(operatingHours);

            operatingHours = new OperatingHours(DayOfWeekEnum.SUNDAY, null, null);
            medicalCentreSessionBeanLocal.createNewOperatingHours(operatingHours);
            medicalCentreOperatingHours.add(operatingHours);

            operatingHours = new OperatingHours(DayOfWeekEnum.PUBLIC_HOLIDAY, null, null);
            medicalCentreSessionBeanLocal.createNewOperatingHours(operatingHours);
            medicalCentreOperatingHours.add(operatingHours);

            MedicalCentre newMedicalCentre = new MedicalCentre(medicalCentreName, medicalCentrePhone, medicalCentreAddress, medicalCentreOperatingHours);
            Long medicalCentreId1 = medicalCentreSessionBeanLocal.createNewMedicalCentre(newMedicalCentre);

        } catch (InputDataValidationException | UnknownPersistenceException | ServicemanNricExistException | ServicemanEmailExistException ex) {
            //ex.printStackTrace();
            System.out.println(ex.getMessage());
        }

    }
}
