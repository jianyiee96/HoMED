package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.MedicalCentreSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Admin;
import entity.Clerk;
import entity.Employee;
import entity.MedicalCentre;
import entity.OperatingHours;
import entity.MedicalOfficer;
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
import util.exceptions.EmployeeNricExistException;
import util.exceptions.InputDataValidationException;
import util.exceptions.ServicemanEmailExistException;
import util.exceptions.ServicemanNricExistException;
import util.exceptions.UnknownPersistenceException;

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
            System.out.println("data exists");
        }
    }

    private void initializeData() {

        try {
            Long empId1 = employeeSessionBeanLocal.createEmployee(new Admin("Admin 1", "s1234567a", "password"));
            Long empId2 = employeeSessionBeanLocal.createEmployee(new MedicalOfficer("Medical Officer 1", "s1234567b", "password"));
            Long empId3 = employeeSessionBeanLocal.createEmployee(new Clerk("Clerk 1", "s1234567c", "password"));

            String serviceman1OTP = servicemanSessionBeanLocal.createNewServiceman(new Serviceman("Amos Tan Ah Kow", "S9876543Z", new Date(), GenderEnum.MALE, BloodTypeEnum.BP, "amos@gmail.com", "13 Computing Drive"));
            String serviceman2OTP = servicemanSessionBeanLocal.createNewServiceman(new Serviceman("Brandon Tan Ah Kow", "S9876544Z", new Date(), GenderEnum.MALE, BloodTypeEnum.BP, "brandon@gmail.com", "14 Computing Drive"));
            String serviceman3OTP = servicemanSessionBeanLocal.createNewServiceman(new Serviceman("Charles Tan Ah Kow", "S9876545Z", new Date(), GenderEnum.MALE, BloodTypeEnum.BP, "charles@gmail.com", "15 Computing Drive"));

            System.out.println("Serviceman 1 : " + serviceman1OTP);
            System.out.println("Serviceman 2 : " + serviceman2OTP);
            System.out.println("Serviceman 3 : " + serviceman3OTP);

            String medicalCentreName = "HOME TEAM ACADEMY MEDICAL CENTRE";
            String medicalCentrePhone = "64653921";
            // Street Name
            // Unit Number
            // Building Name
            // Country
            // Postal Code
            // Delimited by "!!!@@!!!"
            String medicalCentreAddress = "501 OLD CHOA CHU KANG ROAD!!!@@!!!#01-00!!!@@!!!!!!@@!!!Singapore!!!@@!!!698928";
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

            System.out.println("End of data init");
        } catch (InputDataValidationException | UnknownPersistenceException | ServicemanNricExistException | ServicemanEmailExistException | EmployeeNricExistException ex) {
            //ex.printStackTrace();
            System.out.println(ex.getMessage());
        }

    }
}
