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
        try {
            employeeSessionBeanLocal.retrieveEmployeeById(1l);
            System.out.println("====================== Exiting Data Init [Data Exists] ======================");
        } catch (EmployeeNotFoundException ex) {
            initializeData();
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
            
            initializeVaccinationForm();
            System.out.println("====================== End of DATA INIT ======================");
        } catch (CreateEmployeeException | CreateServicemanException
                | CreateMedicalCentreException | CreateConsultationPurposeException
                | CreateFormTemplateException | GenerateFormInstanceException | RelinkFormTemplatesException | UpdateFormInstanceException ex) {
            System.out.println(ex.getMessage());
            System.out.println("====================== Failed to complete DATA INIT ======================");
        }
    }

    private void initializeFormInstance(Long servicemanId, Long formTemplateId) throws GenerateFormInstanceException, UpdateFormInstanceException {
//        Long formInstanceId = formInstanceSessionBeanLocal.generateFormInstance(servicemanId, formTemplateId);
//        FormInstance formInstance = formInstanceSessionBeanLocal.retrieveFormInstance(formInstanceId);
//        em.detach(formInstance);

//        formInstanceSessionBeanLocal.updateFormInstanceFieldValues(formInstance);
    }

    private Long initializeForm() throws CreateConsultationPurposeException, CreateFormTemplateException, RelinkFormTemplatesException {
        ConsultationPurpose consultationPurpose = new ConsultationPurpose("Consultation Purpose 1");
        consultationPurposeSessionBeanLocal.createConsultationPurpose(consultationPurpose);

        FormTemplate formTemplate = new FormTemplate("Form Template Demo 1");
        Long formTemplateId = formTemplateSessionBeanLocal.createFormTemplate(formTemplate);
        FormTemplate otherFormTemplate = new FormTemplate(formTemplate.getFormTemplateName());
        otherFormTemplate.setFormTemplateId(formTemplateId);

        List<FormField> formFields = new ArrayList<>();
        formFields.add(new FormField("Basic Questionnaire", 1, InputTypeEnum.HEADER, Boolean.FALSE, Boolean.FALSE, null));
        formFields.add(new FormField("What is your name?", 2, InputTypeEnum.TEXT, Boolean.TRUE, Boolean.TRUE, null));
        formFields.add(new FormField("What is your middle name?", 3, InputTypeEnum.TEXT, Boolean.FALSE, Boolean.TRUE, null));
        formFields.add(new FormField("How old are you?", 4, InputTypeEnum.NUMBER, Boolean.TRUE, Boolean.TRUE, null));
        formFields.add(new FormField("When was your last vaccination?", 5, InputTypeEnum.DATE, Boolean.TRUE, Boolean.TRUE, null));
        formFields.add(new FormField("What time do you usually sleep?", 6, InputTypeEnum.TIME, Boolean.TRUE, Boolean.TRUE, null));
        List<FormFieldOption> formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("MALE"));
        formFieldOptions.add(new FormFieldOption("FEMALE"));
        formFields.add(new FormField("What is your gender?", 7, InputTypeEnum.RADIO_BUTTON, Boolean.TRUE, Boolean.TRUE, formFieldOptions));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("YES"));
        formFieldOptions.add(new FormFieldOption("NO"));
        formFields.add(new FormField("Do you have any allergies?", 8, InputTypeEnum.RADIO_BUTTON, Boolean.TRUE, Boolean.TRUE, formFieldOptions));
        formFields.add(new FormField("If yes, what allergies?", 9, InputTypeEnum.TEXT, Boolean.FALSE, Boolean.TRUE, null));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("North"));
        formFieldOptions.add(new FormFieldOption("South"));
        formFieldOptions.add(new FormFieldOption("EAST"));
        formFieldOptions.add(new FormFieldOption("WEST"));
        formFieldOptions.add(new FormFieldOption("CENTRAL"));
        formFields.add(new FormField("What is your preferred location?", 10, InputTypeEnum.CHECK_BOX, Boolean.TRUE, Boolean.TRUE, formFieldOptions));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("A+"));
        formFieldOptions.add(new FormFieldOption("A-"));
        formFieldOptions.add(new FormFieldOption("B+"));
        formFieldOptions.add(new FormFieldOption("B-"));
        formFieldOptions.add(new FormFieldOption("AB+"));
        formFieldOptions.add(new FormFieldOption("AB-"));
        formFieldOptions.add(new FormFieldOption("O+"));
        formFieldOptions.add(new FormFieldOption("O-"));
        formFields.add(new FormField("What is your blood type?", 11, InputTypeEnum.SINGLE_DROPDOWN, Boolean.TRUE, Boolean.TRUE, formFieldOptions));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("Monday"));
        formFieldOptions.add(new FormFieldOption("Tuesday"));
        formFieldOptions.add(new FormFieldOption("Wednesday"));
        formFieldOptions.add(new FormFieldOption("Thursday"));
        formFieldOptions.add(new FormFieldOption("Friday"));
        formFieldOptions.add(new FormFieldOption("Saturday"));
        formFieldOptions.add(new FormFieldOption("Sunday"));
        formFields.add(new FormField("What are your preferred available days?", 12, InputTypeEnum.MULTI_DROPDOWN, Boolean.TRUE, Boolean.TRUE, formFieldOptions));
        formFields.add(new FormField("Doctor Remarks:", 13, InputTypeEnum.TEXT, Boolean.TRUE, Boolean.FALSE, null));
//        formFields.add(new FormField("This question is a File Upload", 12, InputTypeEnum.FILE_UPLOAD, Boolean.TRUE, Boolean.TRUE, null));
//        formFields.add(new FormField("This question is a Image Upload", 13, InputTypeEnum.IMAGE_UPLOAD, Boolean.TRUE, Boolean.TRUE, null));
        otherFormTemplate.setFormFields(formFields);
        formTemplateSessionBeanLocal.saveFormTemplate(otherFormTemplate);
        formTemplateSessionBeanLocal.publishFormTemplate(otherFormTemplate.getFormTemplateId());

        List<FormTemplate> formTemplates = new ArrayList<>();
        formTemplates.add(formTemplate);
        consultationPurposeSessionBeanLocal.relinkFormTemplates(consultationPurpose.getConsultationPurposeId(), formTemplates);

        System.out.println("Successfully created Form Template Demo 1\n");
        return formTemplate.getFormTemplateId();
    }

    private Long initializeVaccinationForm() throws CreateConsultationPurposeException, CreateFormTemplateException, RelinkFormTemplatesException {
        ConsultationPurpose consultationPurpose = new ConsultationPurpose("Vaccination");
        consultationPurposeSessionBeanLocal.createConsultationPurpose(consultationPurpose);

        FormTemplate formTemplate = new FormTemplate("Pre-vaccination Questionnaires");
        Long formTemplateId = formTemplateSessionBeanLocal.createFormTemplate(formTemplate);
        FormTemplate otherFormTemplate = new FormTemplate(formTemplate.getFormTemplateName());
        otherFormTemplate.setFormTemplateId(formTemplateId);

        List<FormField> formFields = new ArrayList<>();
        List<FormFieldOption> formFieldOptions = new ArrayList<>();
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("YES"));
        formFieldOptions.add(new FormFieldOption("NO"));
        formFields.add(new FormField("Were you born in Singapore?", 1, InputTypeEnum.RADIO_BUTTON, Boolean.TRUE, Boolean.TRUE, formFieldOptions));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("YES"));
        formFieldOptions.add(new FormFieldOption("NO"));
        formFields.add(new FormField("Are you on any long-term medication?", 2, InputTypeEnum.RADIO_BUTTON, Boolean.TRUE, Boolean.TRUE, formFieldOptions));
        formFields.add(new FormField("If yes, please declare the name of drugs?", 3, InputTypeEnum.TEXT, Boolean.FALSE, Boolean.TRUE, null));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("YES"));
        formFieldOptions.add(new FormFieldOption("NO"));
        formFields.add(new FormField("Are you currently having any of the following: fever, acute illness, chronic disease, bleeding disorders?", 4, InputTypeEnum.RADIO_BUTTON, Boolean.TRUE, Boolean.TRUE, formFieldOptions));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("YES"));
        formFieldOptions.add(new FormFieldOption("NO"));
        formFields.add(new FormField("Do you have any drug allergies?", 5, InputTypeEnum.RADIO_BUTTON, Boolean.TRUE, Boolean.TRUE, formFieldOptions));
        formFields.add(new FormField("If yes, please declare your drug allergies?", 6, InputTypeEnum.TEXT, Boolean.FALSE, Boolean.TRUE, null));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("YES"));
        formFieldOptions.add(new FormFieldOption("NO"));
        formFields.add(new FormField("[For Female Patients] Are you currently pregnant or suspect that you may be pregnant?", 7, InputTypeEnum.RADIO_BUTTON, Boolean.FALSE, Boolean.TRUE, formFieldOptions));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("YES"));
        formFieldOptions.add(new FormFieldOption("NO"));
        formFields.add(new FormField("Have you ever had chicken pox before or had the Varicella vaccine", 8, InputTypeEnum.RADIO_BUTTON, Boolean.TRUE, Boolean.TRUE, formFieldOptions));
        formFields.add(new FormField("Vaccine to be given: Seasonal Flu", 9, InputTypeEnum.DATE, Boolean.FALSE, Boolean.FALSE, null));
        formFields.add(new FormField("Vaccine to be given: Tetanus", 10, InputTypeEnum.DATE, Boolean.FALSE, Boolean.FALSE, null));
        formFields.add(new FormField("Vaccine to be given: Hepatitis B", 11, InputTypeEnum.DATE, Boolean.FALSE, Boolean.FALSE, null));
        formFields.add(new FormField("Vaccine to be given: Hepatitis A", 12, InputTypeEnum.DATE, Boolean.FALSE, Boolean.FALSE, null));
        formFields.add(new FormField("Vaccine to be given: Typhoid", 13, InputTypeEnum.DATE, Boolean.FALSE, Boolean.FALSE, null));
        formFields.add(new FormField("Vaccine to be given: Varicella", 14, InputTypeEnum.DATE, Boolean.FALSE, Boolean.FALSE, null));
        formFields.add(new FormField("If others, please specify:", 15, InputTypeEnum.TEXT, Boolean.FALSE, Boolean.FALSE, null));
        
        otherFormTemplate.setFormFields(formFields);
        otherFormTemplate.setDeclaration("I certify that I have answered the above questionnaire to the best of my knowledge.");
        formTemplateSessionBeanLocal.saveFormTemplate(otherFormTemplate);
        formTemplateSessionBeanLocal.publishFormTemplate(otherFormTemplate.getFormTemplateId());

        List<FormTemplate> formTemplates = new ArrayList<>();
        formTemplates.add(formTemplate);
        consultationPurposeSessionBeanLocal.relinkFormTemplates(consultationPurpose.getConsultationPurposeId(), formTemplates);

        System.out.println("Successfully created Pre-vaccination Questionnaires\n");
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
