package ejb.session.singleton;

import ejb.session.stateless.ConsultationPurposeSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.FormInstanceSessionBeanLocal;
import ejb.session.stateless.FormTemplateSessionBeanLocal;
import ejb.session.stateless.MedicalCentreSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Address;
import entity.Admin;
import entity.Clerk;
import entity.ConsultationPurpose;
import entity.Employee;
import entity.FormField;
import entity.FormFieldOption;
import entity.FormInstance;
import entity.FormTemplate;
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
import util.enumeration.InputTypeEnum;
import util.exceptions.CreateConsultationPurposeException;
import util.exceptions.CreateEmployeeException;
import util.exceptions.CreateFormTemplateException;
import util.exceptions.CreateMedicalCentreException;
import util.exceptions.CreateServicemanException;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.GenerateFormInstanceException;
import util.exceptions.RelinkFormTemplatesException;
import util.exceptions.UpdateFormInstanceException;

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
    @EJB
    private ConsultationPurposeSessionBeanLocal consultationPurposeSessionBeanLocal;
    @EJB
    private FormTemplateSessionBeanLocal formTemplateSessionBeanLocal;
    @EJB
    private FormInstanceSessionBeanLocal formInstanceSessionBeanLocal;

    @PostConstruct
    public void postConstruct() {
        initializeReadFile();

        try {
            employeeSessionBeanLocal.retrieveEmployeeById(1l);
            System.out.println("====================== Exiting Data Init [Data Exists] ======================");
        } catch (EmployeeNotFoundException ex) {
            initializeData();
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
            System.out.println("====================== Start of DATA INIT ======================");

            Employee emp1 = new Admin("Adrian Tan", "password", "dummyemailx1@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "98765432", GenderEnum.MALE);
            Employee emp2 = new MedicalOfficer("Melissa Lim", "password", "dummyemailx2@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "81234567", GenderEnum.FEMALE);
            Employee emp3 = new Clerk("Clyde", "password", "dummyemailx3@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "88888888", GenderEnum.MALE);
            Long empId1 = employeeSessionBeanLocal.createEmployeeByInit(emp1);
            Long empId2 = employeeSessionBeanLocal.createEmployeeByInit(emp2);
            Long empId3 = employeeSessionBeanLocal.createEmployeeByInit(emp3);
            System.out.println("EMPLOYEE INFO [INIT]");
            System.out.println("Email: " + emp1.getEmail() + "\tPhone: " + emp1.getPhoneNumber());
            System.out.println("Email: " + emp2.getEmail() + "\tPhone: " + emp2.getPhoneNumber());
            System.out.println("Email: " + emp3.getEmail() + "\tPhone: " + emp3.getPhoneNumber());
            System.out.println("Successfully created employees by init\n");

            Employee emp1Otp = new Admin("Admin OTP", "dummyemailxxx11@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "94362875", GenderEnum.FEMALE);
            Employee emp2Otp = new MedicalOfficer("MO OTP", "dummyemailxxx12@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "94360875", GenderEnum.MALE);
            Employee emp3Otp = new Clerk("Clerk OTP", "dummyemailxxx13@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "94326975", GenderEnum.MALE);
            String empOtp1 = employeeSessionBeanLocal.createEmployee(emp1Otp);
            String empOtp2 = employeeSessionBeanLocal.createEmployee(emp2Otp);
            String empOtp3 = employeeSessionBeanLocal.createEmployee(emp3Otp);

            System.out.println("EMPLOYEE INFO [OTP]");
            System.out.println("Email: " + emp1Otp.getEmail() + "\tPhone: " + emp1Otp.getPhoneNumber() + "\tOTP: " + empOtp1);
            System.out.println("Email: " + emp2Otp.getEmail() + "\tPhone: " + emp2Otp.getPhoneNumber() + "\tOTP: " + empOtp2);
            System.out.println("Email: " + emp3Otp.getEmail() + "\tPhone: " + emp3Otp.getPhoneNumber() + "\tOTP: " + empOtp3);
            System.out.println("Successfully created employees with OTP\n");

            Serviceman serviceman1 = new Serviceman("Audi More", "audi_keynote@hotmail.com", "98765432", new Date(), GenderEnum.MALE, BloodTypeEnum.A_POSITIVE, new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"));
            Serviceman serviceman2 = new Serviceman("Bee Am D. You", "bee_board@hotmail.com", "98765434", new Date(), GenderEnum.MALE, BloodTypeEnum.A_NEGATIVE, new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"));
            Serviceman serviceman3 = new Serviceman("Merser D.", "merser_master@hotmail.com", "98765435", new Date(), GenderEnum.MALE, BloodTypeEnum.AB_POSITIVE, new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"));
            String serviceman1OTP = servicemanSessionBeanLocal.createServiceman(serviceman1);
            String serviceman2OTP = servicemanSessionBeanLocal.createServiceman(serviceman2);
            String serviceman3OTP = servicemanSessionBeanLocal.createServiceman(serviceman3);

            System.out.println("Serviceman INFO [OTP]");
            System.out.println("Email: " + serviceman1.getEmail() + "\tPhone: " + serviceman1.getPhoneNumber() + "\tOTP: " + serviceman1OTP);
            System.out.println("Email: " + serviceman2.getEmail() + "\tPhone: " + serviceman2.getPhoneNumber() + "\tOTP: " + serviceman2OTP);
            System.out.println("Email: " + serviceman3.getEmail() + "\tPhone: " + serviceman3.getPhoneNumber() + "\tOTP: " + serviceman3OTP);
            System.out.println("Successfully created serivcemen with OTP\n");

            initializeMedicalCentres();
            Long formTemplateId = initializeForm();
            initializeFormInstance(serviceman1.getServicemanId(), formTemplateId);
            System.out.println("====================== End of DATA INIT ======================");
        } catch (CreateEmployeeException | CreateServicemanException
                | CreateMedicalCentreException | CreateConsultationPurposeException
                | CreateFormTemplateException | GenerateFormInstanceException | RelinkFormTemplatesException | UpdateFormInstanceException ex) {
            System.out.println(ex.getMessage());
            System.out.println("====================== Failed to complete DATA INIT ======================");
        }
    }

    private void initializeFormInstance(Long servicemanId, Long formTemplateId) throws GenerateFormInstanceException, UpdateFormInstanceException {
        Long formInstanceId = formInstanceSessionBeanLocal.generateFormInstance(servicemanId, formTemplateId);
        FormInstance formInstance = formInstanceSessionBeanLocal.retrieveFormInstance(formInstanceId);
        em.detach(formInstance);

//        formInstanceSessionBeanLocal.updateFormInstanceFieldValues(formInstance);
    }

    private Long initializeForm() throws CreateConsultationPurposeException, CreateFormTemplateException, RelinkFormTemplatesException {
        ConsultationPurpose consultationPurpose = new ConsultationPurpose("Consultation Purpose 1");
        consultationPurposeSessionBeanLocal.createConsultationPurpose(consultationPurpose);

        FormTemplate formTemplate = new FormTemplate("Form Template Test 1");
        Long formTemplateId = formTemplateSessionBeanLocal.createFormTemplate(formTemplate);
        FormTemplate otherFormTemplate = new FormTemplate(formTemplate.getFormTemplateName());
        otherFormTemplate.setFormTemplateId(formTemplateId);

        List<FormField> formFields = new ArrayList<>();
        formFields.add(new FormField("This question is a text", 1, InputTypeEnum.TEXT, Boolean.TRUE, Boolean.TRUE, null));
        formFields.add(new FormField("This question is a text (not required)", 2, InputTypeEnum.TEXT, Boolean.FALSE, Boolean.TRUE, null));
        formFields.add(new FormField("This question is a text (doctor only)", 3, InputTypeEnum.TEXT, Boolean.TRUE, Boolean.FALSE, null));
        formFields.add(new FormField("This question is a header", 4, InputTypeEnum.HEADER, Boolean.FALSE, Boolean.FALSE, null));
        formFields.add(new FormField("This question is a number", 5, InputTypeEnum.NUMBER, Boolean.TRUE, Boolean.TRUE, null));
        formFields.add(new FormField("This question is a Date", 6, InputTypeEnum.DATE, Boolean.TRUE, Boolean.TRUE, null));
        formFields.add(new FormField("This question is a Time", 7, InputTypeEnum.TIME, Boolean.TRUE, Boolean.TRUE, null));
        List<FormFieldOption> formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("A"));
        formFieldOptions.add(new FormFieldOption("B"));
        formFieldOptions.add(new FormFieldOption("C"));
        formFieldOptions.add(new FormFieldOption("D"));
        formFields.add(new FormField("This question is a Radio Button", 8, InputTypeEnum.RADIO_BUTTON, Boolean.TRUE, Boolean.TRUE, formFieldOptions));
        formFields.add(new FormField("This question is a Check Box", 9, InputTypeEnum.CHECK_BOX, Boolean.TRUE, Boolean.TRUE, formFieldOptions));
        formFields.add(new FormField("This question is a Single Dropdown", 10, InputTypeEnum.SINGLE_DROPDOWN, Boolean.TRUE, Boolean.TRUE, formFieldOptions));
        formFields.add(new FormField("This question is a Multi Dropdown", 11, InputTypeEnum.MULTI_DROPDOWN, Boolean.TRUE, Boolean.TRUE, formFieldOptions));
        formFields.add(new FormField("This question is a File Upload", 12, InputTypeEnum.FILE_UPLOAD, Boolean.TRUE, Boolean.TRUE, null));
        formFields.add(new FormField("This question is a Image Upload", 13, InputTypeEnum.IMAGE_UPLOAD, Boolean.TRUE, Boolean.TRUE, null));
        otherFormTemplate.setFormFields(formFields);
        formTemplateSessionBeanLocal.saveFormTemplate(otherFormTemplate);
        formTemplateSessionBeanLocal.publishFormTemplate(otherFormTemplate.getFormTemplateId());

        List<FormTemplate> formTemplates = new ArrayList<>();
        formTemplates.add(formTemplate);
        consultationPurposeSessionBeanLocal.relinkFormTemplates(consultationPurpose.getConsultationPurposeId(), formTemplates);

        System.out.println("Successfully created Form Template\n");
        return formTemplate.getFormTemplateId();
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
        System.out.println("Successfully created medical centres\n");
    }
}
