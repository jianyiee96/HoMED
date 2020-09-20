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

        if (employeeSessionBeanLocal.retrieveEmployeeById(1l) == null) {
            initializeData();
        } else {
            System.out.println("data exists");
        }
    }

    private void initializeData() {
        try {
            System.out.println("Start of data init");


            Long empId1 = employeeSessionBeanLocal.createEmployeeByInit(new Admin("Adrian Tan", "s1234567a", "password", "adrian_diver@hotmail.com", "1 Computing Drive", 98765432, GenderEnum.MALE));
            Long empId2 = employeeSessionBeanLocal.createEmployeeByInit(new MedicalOfficer("Melissa Lim", "s1234567b", "password", "melissa_gardening@hotmail.com", "10 Heng Mui Kee", 81234567, GenderEnum.FEMALE));
            Long empId3 = employeeSessionBeanLocal.createEmployeeByInit(new Clerk("Clyde", "s1234567c", "password", "clyde_clode@hotmail.com", "28 Jalan Klinik", 88888888, GenderEnum.MALE));

            String employee1OTP = employeeSessionBeanLocal.createEmployee(new Admin("Adam Pholo", "s1234567d", "adam_step2@hotmail.com", "30 Jalan Klinik", 94362875, GenderEnum.FEMALE));
            String employee2OTP = employeeSessionBeanLocal.createEmployee(new MedicalOfficer("Maramamo", "s1234567e", "maramamo_zoro@hotmail.com", "50 Jalan Jalan", 94360875, GenderEnum.MALE));
            String employee3OTP = employeeSessionBeanLocal.createEmployee(new Clerk("Clint", "s1234567f", "clint_tint@hotmail.com", "120 Jalan Bedok", 94326975, GenderEnum.MALE));

            System.out.println("Employee NRIC: s1234567d\tOTP " + employee1OTP);
            System.out.println("Employee NRIC: s1234567e\tOTP " + employee2OTP);
            System.out.println("Employee NRIC: s1234567f\tOTP " + employee3OTP);

            String serviceman1OTP = servicemanSessionBeanLocal.createNewServiceman(new Serviceman("Audi More", "s7654321d", "98765432", new Date(), GenderEnum.MALE, BloodTypeEnum.A_POSITIVE, "audi_keynote@hotmail.com", "13 Computing Drive"));
            String serviceman2OTP = servicemanSessionBeanLocal.createNewServiceman(new Serviceman("Bee Am D. You", "s7654321e", "98765434", new Date(), GenderEnum.MALE, BloodTypeEnum.A_NEGATIVE, "bee_board@hotmail.com", "14 Science Drive"));
            String serviceman3OTP = servicemanSessionBeanLocal.createNewServiceman(new Serviceman("Merser D.", "s7654321f", "98765435", new Date(), GenderEnum.MALE, BloodTypeEnum.AB_POSITIVE, "merser_master@hotmail.com", "15 Enigineering Drive"));

            System.out.println("Serviceman NRIC: s7654321d\tOTP : " + serviceman1OTP);
            System.out.println("Serviceman NRIC: s7654321e\tOTP : " + serviceman2OTP);
            System.out.println("Serviceman NRIC: s7654321f\tOTP : " + serviceman3OTP);
            initializeMedicalCentres();

            System.out.println("End of data init");
        } catch (InputDataValidationException | UnknownPersistenceException | ServicemanNricExistException | ServicemanEmailExistException | EmployeeNricExistException ex) {
            //ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    private void initializeMedicalCentres() throws InputDataValidationException, UnknownPersistenceException {
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
