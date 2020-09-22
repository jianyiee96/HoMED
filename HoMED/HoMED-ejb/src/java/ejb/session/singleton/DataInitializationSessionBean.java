package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.MedicalCentreSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Address;
import entity.Admin;
import entity.Clerk;
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
import util.exceptions.CreateEmployeeException;
import util.exceptions.CreateMedicalCentreException;
import util.exceptions.CreateServicemanException;
import util.exceptions.EmployeeNotFoundException;

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
        try {
            employeeSessionBeanLocal.retrieveEmployeeById(1l);
        } catch (EmployeeNotFoundException ex) {
            initializeData();
            System.out.println("data exists");
        }
    }

    private void initializeData() {
        try {
            System.out.println("Start of data init");

            Long empId1 = employeeSessionBeanLocal.createEmployeeByInit(new Admin("Adrian Tan", "s1234567a", "password", "dummyemailx1@hotmail.com", "1 Computing Drive", "98765432", GenderEnum.MALE));
            Long empId2 = employeeSessionBeanLocal.createEmployeeByInit(new MedicalOfficer("Melissa Lim", "s1234567b", "password", "dummyemailx2@hotmail.com", "10 Heng Mui Kee", "81234567", GenderEnum.FEMALE));
            Long empId3 = employeeSessionBeanLocal.createEmployeeByInit(new Clerk("Clyde", "s1234567c", "password", "dummyemailx3@hotmail.com", "28 Jalan Klinik", "88888888", GenderEnum.MALE));

            String employee1OTP = employeeSessionBeanLocal.createEmployee(new Admin("Admin OTP", "s1234567d", "dummyemailxxx11@hotmail.com", "30 Jalan Klinik", "94362875", GenderEnum.FEMALE));
            String employee2OTP = employeeSessionBeanLocal.createEmployee(new MedicalOfficer("MO OTP", "s1234567e", "dummyemailxxx12@hotmail.com", "50 Jalan Jalan", "94360875", GenderEnum.MALE));
            String employee3OTP = employeeSessionBeanLocal.createEmployee(new Clerk("Clerk OTP", "s1234567f", "dummyemailxxx13@hotmail.com", "120 Jalan Bedok", "94326975", GenderEnum.MALE));

            System.out.println("Employee NRIC: s1234567d\tOTP " + employee1OTP);
            System.out.println("Employee NRIC: s1234567e\tOTP " + employee2OTP);
            System.out.println("Employee NRIC: s1234567f\tOTP " + employee3OTP);

            String serviceman1OTP = servicemanSessionBeanLocal.createServiceman(new Serviceman("Audi More", "s7654321d", "98765432", new Date(), GenderEnum.MALE, BloodTypeEnum.A_POSITIVE, "audi_keynote@hotmail.com", "13 Computing Drive"));
            String serviceman2OTP = servicemanSessionBeanLocal.createServiceman(new Serviceman("Bee Am D. You", "s7654321e", "98765434", new Date(), GenderEnum.MALE, BloodTypeEnum.A_NEGATIVE, "bee_board@hotmail.com", "14 Science Drive"));
            String serviceman3OTP = servicemanSessionBeanLocal.createServiceman(new Serviceman("Merser D.", "s7654321f", "98765435", new Date(), GenderEnum.MALE, BloodTypeEnum.AB_POSITIVE, "merser_master@hotmail.com", "15 Enigineering Drive"));

            System.out.println("Serviceman EMAIL: audi_keynote@hotmail.com\tOTP : " + serviceman1OTP);
            System.out.println("Serviceman EMAIL: bee_board@hotmail.com\tOTP : " + serviceman2OTP);
            System.out.println("Serviceman EMAIL: merser_master@hotmail.com\tOTP : " + serviceman3OTP);
            initializeMedicalCentres();

            System.out.println("End of data init");
        } catch (CreateEmployeeException | CreateServicemanException | CreateMedicalCentreException ex) {
            //ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    private void initializeMedicalCentres() throws CreateMedicalCentreException {
        MedicalCentre newMedicalCentre = new MedicalCentre();

        String medicalCentreName = "HOME TEAM ACADEMY MEDICAL CENTRE";
        newMedicalCentre.setName(medicalCentreName);

        String medicalCentrePhone = "6465-3921";
        newMedicalCentre.setPhone(medicalCentrePhone);

        // Street Name, Unit Number, Building Name, Country, Postal Code
        Address medicalCentreAddress = new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928");
        newMedicalCentre.setAddress(medicalCentreAddress);

        List<OperatingHours> medicalCentreOperatingHours = new ArrayList<>();
        medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.MONDAY, Boolean.FALSE, LocalTime.of(8, 30), LocalTime.of(17, 30)));
        medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.TUESDAY, Boolean.FALSE, LocalTime.of(8, 30), LocalTime.of(17, 30)));
        medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.WEDNESDAY, Boolean.FALSE, LocalTime.of(8, 30), LocalTime.of(17, 30)));
        medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.THURSDAY, Boolean.FALSE, LocalTime.of(8, 30), LocalTime.of(17, 30)));
        medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.FRIDAY, Boolean.FALSE, LocalTime.of(8, 30), LocalTime.of(17, 30)));
        medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.SATURDAY, Boolean.FALSE, LocalTime.of(8, 30), LocalTime.of(13, 30)));
        medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.SUNDAY, Boolean.TRUE, null, null));
        medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.HOLIDAY, Boolean.TRUE, null, null));

        newMedicalCentre.setOperatingHours(medicalCentreOperatingHours);

        Long medicalCentreId1 = medicalCentreSessionBeanLocal.createNewMedicalCentre(newMedicalCentre);
    }
}
