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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
        initializeReadFile();

        try {
            employeeSessionBeanLocal.retrieveEmployeeById(1l);
        } catch (EmployeeNotFoundException ex) {
            initializeData();
            System.out.println("data exists");
        }
    }

    private void initializeReadFile() {
        System.out.println("Initializing files....");

        String csvFile = "C:\\Users\\tanwk\\servicemen-accounts.csv";
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] servicemen = line.split(",");

                System.out.println("Name = " + servicemen[0] + " ; " + "NRIC = " + servicemen[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initializeData() {
        try {
            System.out.println("Start of data init");

            Long empId1 = employeeSessionBeanLocal.createEmployeeByInit(new Admin("Adrian Tan", "password", "dummyemailx1@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "98765432", GenderEnum.MALE));
            Long empId2 = employeeSessionBeanLocal.createEmployeeByInit(new MedicalOfficer("Melissa Lim", "password", "dummyemailx2@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "81234567", GenderEnum.FEMALE));
            Long empId3 = employeeSessionBeanLocal.createEmployeeByInit(new Clerk("Clyde", "password", "dummyemailx3@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "88888888", GenderEnum.MALE));

            String employee1OTP = employeeSessionBeanLocal.createEmployee(new Admin("Admin OTP", "dummyemailxxx11@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "94362875", GenderEnum.FEMALE));
            String employee2OTP = employeeSessionBeanLocal.createEmployee(new MedicalOfficer("MO OTP", "dummyemailxxx12@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "94360875", GenderEnum.MALE));
            String employee3OTP = employeeSessionBeanLocal.createEmployee(new Clerk("Clerk OTP", "dummyemailxxx13@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "94326975", GenderEnum.MALE));

            System.out.println("Employee EMAIL: dummyemailxxx11@hotmail.com\tOTP " + employee1OTP);
            System.out.println("Employee EMAIL: dummyemailxxx12@hotmail.com\tOTP " + employee2OTP);
            System.out.println("Employee EMAIL: dummyemailxxx13@hotmail.com\tOTP " + employee3OTP);

            String serviceman1OTP = servicemanSessionBeanLocal.createServiceman(new Serviceman("Audi More", "audi_keynote@hotmail.com", "98765432", new Date(), GenderEnum.MALE, BloodTypeEnum.A_POSITIVE, new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928")));
            String serviceman2OTP = servicemanSessionBeanLocal.createServiceman(new Serviceman("Bee Am D. You", "bee_board@hotmail.com", "98765434", new Date(), GenderEnum.MALE, BloodTypeEnum.A_NEGATIVE, new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928")));
            String serviceman3OTP = servicemanSessionBeanLocal.createServiceman(new Serviceman("Merser D.", "merser_master@hotmail.com", "98765435", new Date(), GenderEnum.MALE, BloodTypeEnum.AB_POSITIVE, new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928")));

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
        newMedicalCentre.setName("HOME TEAM ACADEMY MEDICAL CENTRE");
        newMedicalCentre.setPhone("64653921");
        // Street Name, Unit Number, Building Name, Country, Postal Code
        newMedicalCentre.setAddress(new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", null, "Singapore", "698928"));

        List<OperatingHours> medicalCentreOperatingHours = new ArrayList<>();
        medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.MONDAY, Boolean.TRUE, LocalTime.of(8, 30), LocalTime.of(17, 30)));
        medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.TUESDAY, Boolean.TRUE, LocalTime.of(8, 30), LocalTime.of(17, 30)));
        medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.WEDNESDAY, Boolean.TRUE, LocalTime.of(8, 30), LocalTime.of(17, 30)));
        medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.THURSDAY, Boolean.TRUE, LocalTime.of(8, 30), LocalTime.of(17, 30)));
        medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.FRIDAY, Boolean.TRUE, LocalTime.of(8, 30), LocalTime.of(17, 30)));
        medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.SATURDAY, Boolean.TRUE, LocalTime.of(8, 30), LocalTime.of(13, 30)));
        medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.SUNDAY, Boolean.FALSE, null, null));
        medicalCentreOperatingHours.add(new OperatingHours(DayOfWeekEnum.HOLIDAY, Boolean.FALSE, null, null));

        newMedicalCentre.setOperatingHours(medicalCentreOperatingHours);
        Long medicalCentreId1 = medicalCentreSessionBeanLocal.createNewMedicalCentre(newMedicalCentre);
    }
}
