package ejb.session.singleton;

import ejb.session.stateless.BookingSessionBeanLocal;
import ejb.session.stateless.ConsultationPurposeSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.FormInstanceSessionBeanLocal;
import ejb.session.stateless.FormTemplateSessionBeanLocal;
import ejb.session.stateless.MedicalCentreSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import ejb.session.stateless.SlotSessionBeanLocal;
import entity.Address;
import entity.Booking;
import entity.BookingSlot;
import entity.SuperUser;
import entity.Clerk;
import entity.ConsultationPurpose;
import entity.Employee;
import entity.FormField;
import entity.FormFieldOption;
import entity.FormInstance;
import entity.FormInstanceField;
import entity.FormInstanceFieldValue;
import entity.FormTemplate;
import entity.MedicalBoardAdmin;
import entity.MedicalBoardSlot;
import entity.MedicalCentre;
import entity.OperatingHours;
import entity.MedicalOfficer;
import entity.MedicalStaff;
import entity.Serviceman;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
import util.enumeration.ServicemanRoleEnum;
import util.exceptions.AssignMedicalStaffToMedicalCentreException;
import util.exceptions.CreateBookingException;
import util.exceptions.CreateConsultationPurposeException;
import util.exceptions.CreateEmployeeException;
import util.exceptions.CreateFormTemplateException;
import util.exceptions.CreateMedicalCentreException;
import util.exceptions.CreateServicemanException;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.RelinkFormTemplatesException;
import util.exceptions.ScheduleBookingSlotException;
import util.exceptions.ServicemanNotFoundException;
import util.exceptions.SubmitFormInstanceException;
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
    @EJB(name = "BookingSessionBeanLocal")
    private BookingSessionBeanLocal bookingSessionBeanLocal;
    @EJB
    private SlotSessionBeanLocal slotSessionBeanLocal;

    final SimpleDateFormat JSON_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

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

            List<MedicalCentre> medicalCentres = initializeMedicalCentres();
            List<Employee> employees = initializeEmployees();
            initializeLinkEmployeesMedicalCentres(employees, medicalCentres);

            List<Serviceman> servicemen = initializeServiceman();

            List<ConsultationPurpose> consultationPurposes = initializeConsultationPurposes();

            List<BookingSlot> bookingSlots = initializeBookingSlots(medicalCentres);
            servicemen.remove(0);
            servicemen.remove(0);

            List<Booking> bookings = initializeBookings(bookingSlots, consultationPurposes, servicemen, 0.1);

//            List<MedicalBoardSlot> medicalBoardSlots = initializeMedicalBoardSlots();

            fillForms(bookings, 0.5);
            System.out.println("====================== End of DATA INIT ======================");
        } catch (CreateEmployeeException | CreateServicemanException | AssignMedicalStaffToMedicalCentreException
                | CreateMedicalCentreException | CreateConsultationPurposeException
                | CreateFormTemplateException | EmployeeNotFoundException
                | RelinkFormTemplatesException | CreateBookingException
                | ScheduleBookingSlotException | ServicemanNotFoundException ex) {
            System.out.println(ex.getMessage());
            System.out.println("====================== Failed to complete DATA INIT ======================");
        }
    }

    private void fillForms(List<Booking> bookings, double rate) {
        rate = Math.min(rate, 1);
        rate = Math.max(0, rate);
        for (Booking booking : bookings) {
            for (FormInstance fi : booking.getFormInstances()) {
                if (Math.random() <= rate) {
                    fillForm(fi);
                }
            }
        }
    }

    private void fillForm(FormInstance fi) {
        try {
            for (FormInstanceField fif : fi.getFormInstanceFields()) {
                if (fif.getFormFieldMapping().getIsRequired() && fif.getFormFieldMapping().getIsServicemanEditable()) {
                    InputTypeEnum inputType = fif.getFormFieldMapping().getInputType();
                    if (inputType == InputTypeEnum.CHECK_BOX
                            || inputType == InputTypeEnum.MULTI_DROPDOWN) {
                        int randOption = ThreadLocalRandom.current().nextInt(1, fif.getFormFieldMapping().getFormFieldOptions().size());
                        List<FormFieldOption> options = fif.getFormFieldMapping().getFormFieldOptions().stream().collect(Collectors.toList());
                        Collections.shuffle(options);
                        fif.setFormInstanceFieldValues(new ArrayList<>());
                        IntStream.range(0, randOption)
                                .forEach(x -> fif.getFormInstanceFieldValues().add(new FormInstanceFieldValue(options.get(x).getFormFieldOptionValue())));
                    } else if (inputType == InputTypeEnum.RADIO_BUTTON
                            || inputType == InputTypeEnum.SINGLE_DROPDOWN) {
                        int randOption = ThreadLocalRandom.current().nextInt(0, fif.getFormFieldMapping().getFormFieldOptions().size());
                        fif.getFormInstanceFieldValues().add(0, new FormInstanceFieldValue(fif.getFormFieldMapping().getFormFieldOptions().get(randOption).getFormFieldOptionValue()));
                    } else if (inputType == InputTypeEnum.TEXT) {
                        fif.getFormInstanceFieldValues().add(0, new FormInstanceFieldValue("This is a random string text that was filled up by serviceman."));
                    } else if (inputType == InputTypeEnum.NUMBER) {
                        fif.getFormInstanceFieldValues().add(0, new FormInstanceFieldValue(String.valueOf((int) (Math.random() * 100))));
                    } else if (inputType == InputTypeEnum.DATE) {
                        Date date = new Date();
                        fif.getFormInstanceFieldValues().add(0, new FormInstanceFieldValue(JSON_DATE_FORMATTER.format(date)));
                    } else if (inputType == InputTypeEnum.TIME) {
                        Date date = new Date();
                        fif.getFormInstanceFieldValues().add(0, new FormInstanceFieldValue(JSON_DATE_FORMATTER.format(date)));
                    }
                }
            }

            formInstanceSessionBeanLocal.updateFormInstanceFieldValues(fi);
            formInstanceSessionBeanLocal.submitFormInstance(fi.getFormInstanceId());
        } catch (UpdateFormInstanceException | SubmitFormInstanceException ex) {
            System.out.println("Failed to fill up form instance: " + ex.getMessage());
        }
    }

    private List<BookingSlot> initializeBookingSlots(List<MedicalCentre> medicalCentres) throws ScheduleBookingSlotException {
        List<BookingSlot> bookingSlots = new ArrayList<>();
        int numOfDaysToCreate = 28;

        for (MedicalCentre mc : medicalCentres) {
            System.out.println("Creating Booking Slots for " + mc.getName() + "...");
            List<OperatingHours> operatingHours = mc.getOperatingHours();

            for (int day = 0; day < numOfDaysToCreate; day++) {
                Calendar date = Calendar.getInstance();
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);

                while (date.get(Calendar.DAY_OF_WEEK) != 1) {
                    date.add(Calendar.DATE, -1);
                }

                date.add(Calendar.DATE, day);

                int dayIdx = date.get(Calendar.DAY_OF_WEEK);
                DayOfWeekEnum dayOfWeekEnum = getDayOfWeekEnum(dayIdx);

                OperatingHours daysOh = operatingHours.stream()
                        .filter(oh -> oh.getDayOfWeek() == dayOfWeekEnum)
                        .findFirst()
                        .orElse(new OperatingHours(DayOfWeekEnum.MONDAY, Boolean.TRUE, LocalTime.of(8, 30), LocalTime.of(17, 30)));

                if (!daysOh.getIsOpen()) {
                    continue;
                }

                Calendar start = new GregorianCalendar();
                Calendar end = new GregorianCalendar();
                start.setTime(date.getTime());
                end.setTime(date.getTime());

                start.set(Calendar.HOUR_OF_DAY, daysOh.getOpeningHours().getHour());
                start.set(Calendar.MINUTE, daysOh.getOpeningHours().getMinute());
                end.set(Calendar.HOUR_OF_DAY, daysOh.getClosingHours().getHour());
                end.set(Calendar.MINUTE, daysOh.getClosingHours().getMinute());

                if (start.getTime().before(end.getTime())) {
                    bookingSlots.addAll(slotSessionBeanLocal.createBookingSlots(mc.getMedicalCentreId(), start.getTime(), end.getTime()));
                }
            }

            System.out.println("Successfully created Booking Slots for " + mc.getName() + "...");

        }
        return bookingSlots;
    }

    private List<Booking> initializeBookings(List<BookingSlot> bookingSlots, List<ConsultationPurpose> consultationPurposes, List<Serviceman> servicemen, double rate) throws CreateBookingException {
        System.out.println("Creating Bookings...");
        List<Booking> bookings = new ArrayList<>();
        rate = Math.min(rate, 1);
        rate = Math.max(0, rate);

        HashMap<Serviceman, Integer> bookingHm = new HashMap<>();
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        HashMap<String, Integer> datesHm = new HashMap<>();

        for (BookingSlot bs : bookingSlots) {
            cal2.setTime(bs.getStartDateTime());
            boolean isAfterToday = cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR)
                    && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

            int randServicemanIdx = ThreadLocalRandom.current().nextInt(0, servicemen.size());
            int randCpIdx = ThreadLocalRandom.current().nextInt(0, consultationPurposes.size());

            if (Math.random() <= rate && isAfterToday) {
                Serviceman serviceman = servicemen.get(randServicemanIdx);
                Booking booking = bookingSessionBeanLocal.createBooking(serviceman.getServicemanId(), consultationPurposes.get(randCpIdx).getConsultationPurposeId(), bs.getSlotId(), "Created by data init.");
                bookings.add(booking);
//                System.out.println("Booking Slot: " + bs.getStartDateTime() + "\t" + bs.getEndDateTime());
//                System.out.println("Booking Created: Start[" + bs.getStartDateTime() + "+] End[" + bs.getEndDateTime() + "]");

                int count = bookingHm.containsKey(serviceman) ? bookingHm.get(serviceman) : 0;
                bookingHm.put(serviceman, count + 1);

                String dateStr = df.format(booking.getBookingSlot().getStartDateTime());
                int countDate = datesHm.containsKey(dateStr) ? datesHm.get(dateStr) : 0;
                datesHm.put(dateStr, countDate + 1);
            }
        }
        System.out.println("Bookings Summary:");
        System.out.println("By Serviceman:");
        for (Serviceman serviceman : bookingHm.keySet()) {
            System.out.println("\t" + serviceman.getName() + ": " + bookingHm.get(serviceman) + " bookings created");
        }
        System.out.println("");
        System.out.println("By Date:");
        for (String dateStr : datesHm.keySet()) {
            System.out.println("\t" + dateStr + ": " + datesHm.get(dateStr) + " bookings created");
        }
        System.out.println("Successfully created Bookings");
        return bookings;
    }

    private List<MedicalBoardSlot> initializeMedicalBoardSlots() throws ScheduleBookingSlotException {
        System.out.println("Creating Medical Board Slots...");
        List<MedicalBoardSlot> medicalBoardSlots = new ArrayList<>();
        int numOfDaysToCreate = 5;

        for (int day = 0; day < numOfDaysToCreate; day++) {
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DATE, day);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);

            Calendar start = new GregorianCalendar();
            Calendar end = new GregorianCalendar();
            start.setTime(date.getTime());
            end.setTime(date.getTime());

            start.set(Calendar.HOUR_OF_DAY, 12);
            start.set(Calendar.MINUTE, 30);
            end.set(Calendar.HOUR_OF_DAY, 17);
            end.set(Calendar.MINUTE, 30);

            if (start.getTime().before(new Date())) {
                start.setTime(new Date());
            }
            if (start.getTime().before(end.getTime())) {
                medicalBoardSlots.addAll(slotSessionBeanLocal.createMedicalBoardSlots(start.getTime(), end.getTime()));
            }
        }

        System.out.println("Successfully created Medical Board Slots");
        return medicalBoardSlots;
    }

    private DayOfWeekEnum getDayOfWeekEnum(int day) {
        switch (day) {
            case 1:
                return DayOfWeekEnum.SUNDAY;
            case 2:
                return DayOfWeekEnum.MONDAY;
            case 3:
                return DayOfWeekEnum.TUESDAY;
            case 4:
                return DayOfWeekEnum.WEDNESDAY;
            case 5:
                return DayOfWeekEnum.THURSDAY;
            case 6:
                return DayOfWeekEnum.FRIDAY;
            case 7:
                return DayOfWeekEnum.SATURDAY;
        }
        return DayOfWeekEnum.MONDAY;
    }

    private List<ConsultationPurpose> initializeConsultationPurposes() throws CreateConsultationPurposeException, CreateFormTemplateException, RelinkFormTemplatesException {
        List<ConsultationPurpose> consultationPurposes = new ArrayList<>();

        ConsultationPurpose consultationPurpose = new ConsultationPurpose("Consultation Purpose 1");
        ConsultationPurpose vaccinationConsultationPurpose = new ConsultationPurpose("Vaccination");
        Long consultationPurposeId = consultationPurposeSessionBeanLocal.createConsultationPurpose(consultationPurpose);
        Long vaccinationId = consultationPurposeSessionBeanLocal.createConsultationPurpose(vaccinationConsultationPurpose);

        Long formTemplateId = initializeForm(consultationPurpose);
        Long vaccinationFormTemplateId = initializeVaccinationForm(vaccinationConsultationPurpose);

        consultationPurposes.add(consultationPurpose);
        consultationPurposes.add(vaccinationConsultationPurpose);
        return consultationPurposes;
    }

    private Long initializeForm(ConsultationPurpose consultationPurpose) throws CreateConsultationPurposeException, CreateFormTemplateException, RelinkFormTemplatesException {
        FormTemplate formTemplate = new FormTemplate("Form Template Demo 1");
        formTemplate.setIsPublic(Boolean.TRUE);
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
        formFieldOptions.add(new FormFieldOption("East"));
        formFieldOptions.add(new FormFieldOption("West"));
        formFieldOptions.add(new FormFieldOption("Central"));
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

    private Long initializeVaccinationForm(ConsultationPurpose consultationPurpose) throws CreateConsultationPurposeException, CreateFormTemplateException, RelinkFormTemplatesException {
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

    private List<Serviceman> initializeServiceman() throws CreateServicemanException, ServicemanNotFoundException {
        List<Serviceman> servicemen = new ArrayList<>();
        Serviceman serviceman1 = new Serviceman("Audi More", "password", "ionic_user@hotmail.com", "98765432", ServicemanRoleEnum.REGULAR, new Date(), GenderEnum.MALE, BloodTypeEnum.A_POSITIVE, new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"));
        Serviceman serviceman2 = new Serviceman("Bee Am D. You", "password", "angular_user@hotmail.com", "98758434", ServicemanRoleEnum.NSF, new Date(), GenderEnum.MALE, BloodTypeEnum.A_NEGATIVE, new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"));
        Serviceman serviceman3 = new Serviceman("Hew Jian Yiee", "password", "svcman3_user@hotmail.com", "97255472", ServicemanRoleEnum.NSMEN, new Date(), GenderEnum.MALE, BloodTypeEnum.AB_POSITIVE, new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"));
        Serviceman serviceman4 = new Serviceman("2 Way Account", "password", "dummyemailx5@hotmail.com", "87241222", ServicemanRoleEnum.NSF, new Date(), GenderEnum.MALE, BloodTypeEnum.AB_POSITIVE, new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"));
        Long serviceman1Id = servicemanSessionBeanLocal.createServicemanByInit(serviceman1);
        Long serviceman2Id = servicemanSessionBeanLocal.createServicemanByInit(serviceman2);
        Long serviceman3Id = servicemanSessionBeanLocal.createServicemanByInit(serviceman3);
        Long serviceman4Id = servicemanSessionBeanLocal.createServicemanByInit(serviceman4);
        serviceman1 = servicemanSessionBeanLocal.retrieveServicemanById(serviceman1Id);
        serviceman2 = servicemanSessionBeanLocal.retrieveServicemanById(serviceman2Id);
        serviceman3 = servicemanSessionBeanLocal.retrieveServicemanById(serviceman3Id);
        serviceman4 = servicemanSessionBeanLocal.retrieveServicemanById(serviceman4Id);
        
        System.out.println("Serviceman INFO [INIT]");
        System.out.println("Email: " + serviceman1.getEmail() + "\tPhone: " + serviceman1.getPhoneNumber());
        System.out.println("Email: " + serviceman2.getEmail() + "\tPhone: " + serviceman2.getPhoneNumber());
        System.out.println("Email: " + serviceman3.getEmail() + "\tPhone: " + serviceman3.getPhoneNumber());
        System.out.println("Email: " + serviceman4.getEmail() + "\tPhone: " + serviceman4.getPhoneNumber());
        System.out.println("Successfully created servicemen by init\n");
        
        Serviceman serviceman1Otp = new Serviceman("Serviceman Activate 1", "serviceman_activate1@hotmail.com", "92856031", ServicemanRoleEnum.NSMEN, new Date(), GenderEnum.MALE, BloodTypeEnum.B_POSITIVE, new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"));
        Serviceman serviceman2Otp = new Serviceman("Serviceman Activate 2", "serviceman_activate2@hotmail.com", "97439534", ServicemanRoleEnum.OTHERS, new Date(), GenderEnum.MALE, BloodTypeEnum.B_NEGATIVE, new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"));
        String servicemanOtp1 = servicemanSessionBeanLocal.createServiceman(serviceman1Otp);
        String servicemanOtp2 = servicemanSessionBeanLocal.createServiceman(serviceman2Otp);
        
        System.out.println("Serviceman INFO [OTP]");
        System.out.println("Email: " + serviceman1Otp.getEmail() + "\tPhone: " + serviceman1Otp.getPhoneNumber() + "\tOTP: " + servicemanOtp1);
        System.out.println("Email: " + serviceman2Otp.getEmail() + "\tPhone: " + serviceman2Otp.getPhoneNumber() + "\tOTP: " + servicemanOtp2);
        System.out.println("Successfully created servicemen with OTP\n");

        servicemen.add(serviceman1);
        servicemen.add(serviceman2);
        servicemen.add(serviceman3);
        servicemen.add(serviceman4);
        servicemen.add(serviceman1Otp);
        servicemen.add(serviceman2Otp);
        return servicemen;
    }

    private void initializeLinkEmployeesMedicalCentres(List<Employee> employees, List<MedicalCentre> medicalCentres) throws AssignMedicalStaffToMedicalCentreException {
        for (Employee employee : employees) {
            if (employee instanceof MedicalStaff) {
                employeeSessionBeanLocal.assignMedicalStaffToMedicalCentre(employee.getEmployeeId(), medicalCentres.get(0).getMedicalCentreId());
            }
        }
    }

    private List<Employee> initializeEmployees() throws CreateEmployeeException, EmployeeNotFoundException {
        List<Employee> employees = new ArrayList<>();

        Employee emp1 = new SuperUser("Adrian Tan", "password", "dummyemailx1@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "98765432", GenderEnum.MALE);
        Employee emp2 = new MedicalOfficer("Melissa Lim", "password", "dummyemailx2@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "81234567", GenderEnum.FEMALE);
        Employee emp3 = new Clerk("Clyde", "password", "dummyemailx3@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "88888888", GenderEnum.MALE);
        Employee emp4 = new MedicalBoardAdmin("Dylan", "password", "dummyemailx4@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "88831888", GenderEnum.MALE);
        Employee emp5 = new Clerk("2 Way Account", "password", "dummyemailx5@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "87241222", GenderEnum.MALE);

        // NO Medical Centre Staff
        Employee emp6 = new MedicalOfficer("MedicalOfficer No MC", "password", "dummyemailx6@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "91758375", GenderEnum.MALE);
        Employee emp7 = new Clerk("Clerk No MC", "password", "dummyemailx7@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "91758375", GenderEnum.MALE);
        Long empId1 = employeeSessionBeanLocal.createEmployeeByInit(emp1);
        Long empId2 = employeeSessionBeanLocal.createEmployeeByInit(emp2);
        Long empId3 = employeeSessionBeanLocal.createEmployeeByInit(emp3);
        Long empId4 = employeeSessionBeanLocal.createEmployeeByInit(emp4);
        Long empId5 = employeeSessionBeanLocal.createEmployeeByInit(emp5);
        Long empId6 = employeeSessionBeanLocal.createEmployeeByInit(emp6);
        Long empId7 = employeeSessionBeanLocal.createEmployeeByInit(emp7);
        emp1 = employeeSessionBeanLocal.retrieveEmployeeByEmail(emp1.getEmail());
        emp2 = employeeSessionBeanLocal.retrieveEmployeeByEmail(emp2.getEmail());
        emp3 = employeeSessionBeanLocal.retrieveEmployeeByEmail(emp3.getEmail());
        emp4 = employeeSessionBeanLocal.retrieveEmployeeByEmail(emp4.getEmail());
        emp5 = employeeSessionBeanLocal.retrieveEmployeeByEmail(emp5.getEmail());
        emp6 = employeeSessionBeanLocal.retrieveEmployeeByEmail(emp6.getEmail());
        emp7 = employeeSessionBeanLocal.retrieveEmployeeByEmail(emp7.getEmail());

        System.out.println("EMPLOYEE INFO [INIT]");
        System.out.println("Email: " + emp1.getEmail() + "\tPhone: " + emp1.getPhoneNumber());
        System.out.println("Email: " + emp2.getEmail() + "\tPhone: " + emp2.getPhoneNumber());
        System.out.println("Email: " + emp3.getEmail() + "\tPhone: " + emp3.getPhoneNumber());
        System.out.println("Email: " + emp4.getEmail() + "\tPhone: " + emp4.getPhoneNumber());
        System.out.println("Email: " + emp5.getEmail() + "\tPhone: " + emp5.getPhoneNumber());
        System.out.println("Email: " + emp6.getEmail() + "\tPhone: " + emp6.getPhoneNumber());
        System.out.println("Email: " + emp7.getEmail() + "\tPhone: " + emp7.getPhoneNumber());
        System.out.println("Successfully created employees by init\n");

        Employee emp1Otp = new SuperUser("Super User OTP", "dummyemailxxx11@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "92153472", GenderEnum.FEMALE);
        Employee emp2Otp = new MedicalOfficer("MO OTP", "dummyemailxxx12@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "94360875", GenderEnum.MALE);
        Employee emp3Otp = new Clerk("Hew Jian Yiee", "dummyemailxxx13@hotmail.com", new Address("501 OLD CHOA CHU KANG ROAD", "#01-00", "", "Singapore", "698928"), "97255472", GenderEnum.MALE);
        String empOtp1 = employeeSessionBeanLocal.createEmployee(emp1Otp);
        String empOtp2 = employeeSessionBeanLocal.createEmployee(emp2Otp);
        String empOtp3 = employeeSessionBeanLocal.createEmployee(emp3Otp);
        emp1Otp = employeeSessionBeanLocal.retrieveEmployeeByEmail(emp1Otp.getEmail());
        emp2Otp = employeeSessionBeanLocal.retrieveEmployeeByEmail(emp2Otp.getEmail());
        emp3Otp = employeeSessionBeanLocal.retrieveEmployeeByEmail(emp3Otp.getEmail());

        System.out.println("EMPLOYEE INFO [OTP]");
        System.out.println("Email: " + emp1Otp.getEmail() + "\tPhone: " + emp1Otp.getPhoneNumber() + "\tOTP: " + empOtp1);
        System.out.println("Email: " + emp2Otp.getEmail() + "\tPhone: " + emp2Otp.getPhoneNumber() + "\tOTP: " + empOtp2);
        System.out.println("Email: " + emp3Otp.getEmail() + "\tPhone: " + emp3Otp.getPhoneNumber() + "\tOTP: " + empOtp3);
        System.out.println("Successfully created employees with OTP\n");

        employees.add(emp1);
        employees.add(emp2);
        employees.add(emp3);
        employees.add(emp4);
        employees.add(emp5);

        employees.add(emp1Otp);
        employees.add(emp2Otp);
        employees.add(emp3Otp);
        return employees;
    }

    private List<MedicalCentre> initializeMedicalCentres() throws CreateMedicalCentreException {
        List<MedicalCentre> medicalCentres = new ArrayList<>();

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

        medicalCentres.add(newMedicalCentre);
        return medicalCentres;
    }
}
