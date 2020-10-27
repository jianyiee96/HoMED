package ejb.session.singleton;

import ejb.session.stateless.BookingSessionBeanLocal;
import ejb.session.stateless.ConsultationPurposeSessionBeanLocal;
import ejb.session.stateless.ConsultationSessionBeanLocal;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
import util.enumeration.FormFieldAccessEnum;
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
import util.exceptions.EndConsultationException;
import util.exceptions.MarkBookingAttendanceException;
import util.exceptions.RelinkFormTemplatesException;
import util.exceptions.ScheduleBookingSlotException;
import util.exceptions.ServicemanNotFoundException;
import util.exceptions.StartConsultationException;
import util.exceptions.SubmitFormInstanceException;
import util.exceptions.UpdateFormInstanceException;

@Singleton
@LocalBean
@Startup
public class DataInitializationSessionBean {

    @EJB(name = "ConsultationSessionBeanLocal")
    private ConsultationSessionBeanLocal consultationSessionBeanLocal;

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
        // EDIT VARIABLES HERE
        Double RATE_OF_CREATING_BOOKINGS = 0.1;
        Double RATE_OF_FILLING_FORMS = 0.8;

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

            List<Booking> bookings = initializeBookings(bookingSlots, consultationPurposes, servicemen, RATE_OF_CREATING_BOOKINGS);

//            List<MedicalBoardSlot> medicalBoardSlots = initializeMedicalBoardSlots();
            fillForms(bookings, RATE_OF_FILLING_FORMS);
            Date today = new Date();
            List<Booking> pastBookings = bookings.stream()
                    .filter(b -> b.getBookingSlot().getStartDateTime().before(today))
                    .collect(Collectors.toList());
            executeConsultationsForPastBookings(pastBookings);
            // MARK ATTENDANCE FOR ALL PAST BOOKINGS
            // CONSULTATION FOR ALL PAST BOOKINGS
            System.out.println("====================== End of DATA INIT ======================");
        } catch (CreateEmployeeException | CreateServicemanException | AssignMedicalStaffToMedicalCentreException
                | CreateMedicalCentreException | CreateConsultationPurposeException
                | CreateFormTemplateException | EmployeeNotFoundException
                | RelinkFormTemplatesException | CreateBookingException
                | ScheduleBookingSlotException | ServicemanNotFoundException
                | MarkBookingAttendanceException | StartConsultationException
                | SubmitFormInstanceException | EndConsultationException ex) {
            System.out.println(ex.getMessage());
            System.out.println("====================== Failed to complete DATA INIT ======================");
        }
    }

    private void executeConsultationsForPastBookings(List<Booking> pastBookings) throws MarkBookingAttendanceException, StartConsultationException, SubmitFormInstanceException, EndConsultationException {
        pastBookings.sort((x, y) -> {
            if (x.getBookingSlot().getStartDateTime().before(y.getBookingSlot().getStartDateTime())) {
                return -1;
            } else {
                return 1;
            }
        });

        // MARKING ATTENDANCE
        for (Booking booking : pastBookings) {
            bookingSessionBeanLocal.markBookingAttendance(booking.getBookingId());
            Calendar calQueue = new GregorianCalendar();
            calQueue.setTime(booking.getBookingSlot().getStartDateTime());
            calQueue.add(Calendar.MINUTE, getRandomNumber(1, 16));
            booking.getConsultation().setJoinQueueDateTime(calQueue.getTime());
        }

        pastBookings.sort((x, y) -> {
            if (x.getConsultation().getJoinQueueDateTime().before(y.getConsultation().getJoinQueueDateTime())) {
                return -1;
            } else {
                return 1;
            }
        });

        Calendar calCheck = new GregorianCalendar();
        calCheck.setTime(pastBookings.get(0).getBookingSlot().getStartDateTime());
        // STARTING AND ENDING CONSULT
        for (Booking booking : pastBookings) {
            List<MedicalOfficer> medicalOfficers = booking.getBookingSlot().getMedicalCentre().getMedicalStaffList().stream()
                    .filter(ms -> ms instanceof MedicalOfficer)
                    .map(ms -> (MedicalOfficer) ms)
                    .collect(Collectors.toList());
            Collections.shuffle(medicalOfficers);
            MedicalOfficer mo = medicalOfficers.get(0);

            Calendar calBooking = new GregorianCalendar();
            calBooking.setTime(booking.getConsultation().getJoinQueueDateTime());

//            To Make sure start of consultation is always after the previous consultation
            if (calCheck.get(Calendar.DAY_OF_YEAR) == calBooking.get(Calendar.DAY_OF_YEAR)) {
                if (calCheck.getTime().after(calBooking.getTime())) {
                    calBooking.setTime(calCheck.getTime());
                }
            }

            // Added offset time for queue time
            int randTimeMinutes = getRandomNumber(1, 16);
            calBooking.add(Calendar.MINUTE, randTimeMinutes);
            consultationSessionBeanLocal.startConsultationByInit(booking.getConsultation().getConsultationId(), mo.getEmployeeId(), calBooking.getTime());
            for (FormInstance fi : booking.getFormInstances()) {
                fillForm(fi, true);
                formInstanceSessionBeanLocal.submitFormInstanceByDoctor(fi, mo.getEmployeeId());
            }
            randTimeMinutes = getRandomNumber(1, 16);
            calBooking.add(Calendar.MINUTE, randTimeMinutes);
            consultationSessionBeanLocal.endConsultationByInit(booking.getConsultation().getConsultationId(), getRandomString(), getRandomString(), calBooking.getTime());
            calCheck.setTime(calBooking.getTime());
        }
        System.out.println("Successfully consulted all past bookings");
    }

    private void fillForms(List<Booking> bookings, double rate) {
        rate = Math.min(rate, 1);
        rate = Math.max(0, rate);
        Date today = new Date();
        for (Booking booking : bookings) {
            for (FormInstance fi : booking.getFormInstances()) {
                if (booking.getBookingSlot().getStartDateTime().before(today)
                        || Math.random() <= rate) {
                    fillForm(fi, false);
                }
            }
        }
    }

    private void fillForm(FormInstance fi, boolean isDoctor) {
        try {
            for (FormInstanceField fif : fi.getFormInstanceFields()) {
                if (fif.getFormFieldMapping().getIsRequired() && ((!isDoctor && fif.getFormFieldMapping().getIsServicemanEditable()) || (isDoctor && !fif.getFormFieldMapping().getIsServicemanEditable()))) {
                    InputTypeEnum inputType = fif.getFormFieldMapping().getInputType();
                    if (inputType == InputTypeEnum.CHECK_BOX
                            || inputType == InputTypeEnum.MULTI_DROPDOWN) {
                        int randOption = getRandomNumber(1, fif.getFormFieldMapping().getFormFieldOptions().size());
                        List<FormFieldOption> options = fif.getFormFieldMapping().getFormFieldOptions().stream().collect(Collectors.toList());
                        Collections.shuffle(options);
                        fif.setFormInstanceFieldValues(new ArrayList<>());
                        IntStream.range(0, randOption)
                                .forEach(x -> fif.getFormInstanceFieldValues().add(new FormInstanceFieldValue(options.get(x).getFormFieldOptionValue())));
                    } else if (inputType == InputTypeEnum.RADIO_BUTTON
                            || inputType == InputTypeEnum.SINGLE_DROPDOWN) {
                        int randOption = getRandomNumber(1, fif.getFormFieldMapping().getFormFieldOptions().size());
                        fif.getFormInstanceFieldValues().add(0, new FormInstanceFieldValue(fif.getFormFieldMapping().getFormFieldOptions().get(randOption).getFormFieldOptionValue()));
                    } else if (inputType == InputTypeEnum.TEXT) {
                        fif.getFormInstanceFieldValues().add(0, new FormInstanceFieldValue(getRandomString()));
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
            if (!isDoctor) {
                formInstanceSessionBeanLocal.updateFormInstanceFieldValues(fi);
                formInstanceSessionBeanLocal.submitFormInstance(fi.getFormInstanceId());
            }
        } catch (UpdateFormInstanceException | SubmitFormInstanceException ex) {
            System.out.println("Failed to fill up form instance: " + ex.getMessage());
        }
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
        TreeMap<String, Integer> datesTm = new TreeMap<>((x, y) -> {
            try {
                if (x.equals(y)) {
                    return 0;
                }
                Date d1 = df.parse(x);
                Date d2 = df.parse(y);
                if (d1.before(d2)) {
                    return -1;
                }
                return 1;
            } catch (ParseException ex) {
                return -1;
            }
        });

        for (BookingSlot bs : bookingSlots) {
            cal2.setTime(bs.getStartDateTime());
            boolean isAfterToday = cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR)
                    && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

            int randServicemanIdx = getRandomNumber(0, servicemen.size());
            int randCpIdx = getRandomNumber(0, consultationPurposes.size());
//          && isAfterToday
            if (Math.random() <= rate) {
                Serviceman serviceman = servicemen.get(randServicemanIdx);
                Booking booking = bookingSessionBeanLocal.createBookingByInit(serviceman.getServicemanId(), consultationPurposes.get(randCpIdx).getConsultationPurposeId(), bs.getSlotId(), "Created by data init.");
                bookings.add(booking);

                int count = bookingHm.containsKey(serviceman) ? bookingHm.get(serviceman) : 0;
                bookingHm.put(serviceman, count + 1);

                String dateStr = df.format(booking.getBookingSlot().getStartDateTime());
                if (datesTm.containsKey(dateStr)) {
                    datesTm.replace(dateStr, datesTm.get(dateStr) + 1);
                } else {
                    datesTm.put(dateStr, 1);
                }
            }
        }
        System.out.println("Bookings Summary:");
        System.out.println("By Serviceman:");

        for (Serviceman serviceman : bookingHm.keySet()) {
            System.out.println("\t" + serviceman.getName() + ": " + bookingHm.get(serviceman) + " bookings created");
        }
        System.out.println("");
        System.out.println("By Date:");
        for (Map.Entry<String, Integer> entry : datesTm.entrySet()) {
            System.out.println("\t" + entry.getKey() + ": " + entry.getValue() + " bookings created");
        }
        System.out.println("Successfully created Bookings\n");
        return bookings;
    }

    private List<BookingSlot> initializeBookingSlots(List<MedicalCentre> medicalCentres) throws ScheduleBookingSlotException {
        List<BookingSlot> bookingSlots = new ArrayList<>();
        int numOfPastDaysToCreate = 30;
        int numOfDaysToCreate = 28;

        for (MedicalCentre mc : medicalCentres) {
            System.out.println("Creating Booking Slots for " + mc.getName() + "...");
            List<OperatingHours> operatingHours = mc.getOperatingHours();

            for (int day = 0; day < numOfPastDaysToCreate; day++) {
                Calendar date = Calendar.getInstance();
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);

//                while (date.get(Calendar.DAY_OF_WEEK) != 1) {
//                    date.add(Calendar.DATE, -1);
//                }
                date.add(Calendar.DATE, -day);

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

            // Creating future bookings
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

            System.out.println("Successfully created Booking Slots for " + mc.getName() + "...\n");

        }
        bookingSlots.sort((x, y) -> {
            if (x.getStartDateTime().before(y.getStartDateTime())) {
                return -1;
            } else {
                return 1;
            }
        });
        return bookingSlots;
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

//        ConsultationPurpose consultationPurpose = new ConsultationPurpose("Consultation Purpose 1");
//        ConsultationPurpose vaccinationConsultationPurpose = new ConsultationPurpose("Vaccination");
//        Long consultationPurposeId = consultationPurposeSessionBeanLocal.createConsultationPurpose(consultationPurpose);
//        Long vaccinationId = consultationPurposeSessionBeanLocal.createConsultationPurpose(vaccinationConsultationPurpose);
//
//        Long formTemplateId = initializeForm(consultationPurpose);
//        Long vaccinationFormTemplateId = initializeVaccinationForm(vaccinationConsultationPurpose);
//
//        consultationPurposes.add(consultationPurpose);
//        consultationPurposes.add(vaccinationConsultationPurpose);
        ConsultationPurpose happinessCP = new ConsultationPurpose("Happiness Screening");
        ConsultationPurpose celebrityCP = new ConsultationPurpose("Celebrity Screening");
        ConsultationPurpose animalCP = new ConsultationPurpose("Pet Clearance Checkup");
        consultationPurposeSessionBeanLocal.createConsultationPurpose(happinessCP);
        consultationPurposeSessionBeanLocal.createConsultationPurpose(celebrityCP);
        consultationPurposeSessionBeanLocal.createConsultationPurpose(animalCP);
        FormTemplate happinessFT = initializeFormHappiness();
        FormTemplate celebrityFT = initializeFormCelebrity();
        FormTemplate animalFT = initializeFormAnimal();

        List<FormTemplate> formTemplates = new ArrayList<>();
        formTemplates.add(happinessFT);
        consultationPurposeSessionBeanLocal.relinkFormTemplates(happinessCP.getConsultationPurposeId(), formTemplates);
        formTemplates = new ArrayList<>();
        formTemplates.add(happinessFT);
        formTemplates.add(celebrityFT);
        consultationPurposeSessionBeanLocal.relinkFormTemplates(celebrityCP.getConsultationPurposeId(), formTemplates);
        formTemplates = new ArrayList<>();
        formTemplates.add(animalFT);
        consultationPurposeSessionBeanLocal.relinkFormTemplates(animalCP.getConsultationPurposeId(), formTemplates);

        consultationPurposes.add(happinessCP);
        consultationPurposes.add(celebrityCP);
        consultationPurposes.add(animalCP);
        return consultationPurposes;
    }

    private FormTemplate initializeFormHappiness() throws CreateFormTemplateException {
        FormTemplate formTemplate = new FormTemplate("Happiness Survey Declaration");
        formTemplate.setIsPublic(Boolean.FALSE);
        Long formTemplateId = formTemplateSessionBeanLocal.createFormTemplate(formTemplate);
        FormTemplate otherFormTemplate = new FormTemplate(formTemplate.getFormTemplateName());
        otherFormTemplate.setFormTemplateId(formTemplateId);

        List<FormField> formFields = new ArrayList<>();
        formFields.add(new FormField("Happiness Indicator", 1, InputTypeEnum.HEADER, Boolean.FALSE, FormFieldAccessEnum.SERVICEMAN, null));
        List<FormFieldOption> formFieldOptions = new ArrayList<>();
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("Very Unhappy"));
        formFieldOptions.add(new FormFieldOption("Not Happy"));
        formFieldOptions.add(new FormFieldOption("Neutral"));
        formFieldOptions.add(new FormFieldOption("Happy"));
        formFieldOptions.add(new FormFieldOption("Extremely Happy"));
        formFields.add(new FormField("How happy are you??", 2, InputTypeEnum.SINGLE_DROPDOWN, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, formFieldOptions));
        formFields.add(new FormField("Doctor Remarks:", 3, InputTypeEnum.TEXT, Boolean.TRUE, FormFieldAccessEnum.MO, null));

        otherFormTemplate.setFormFields(formFields);
        formTemplateSessionBeanLocal.saveFormTemplate(otherFormTemplate);
        formTemplateSessionBeanLocal.publishFormTemplate(otherFormTemplate.getFormTemplateId());

        System.out.println("Successfully created Form Template: " + formTemplate.getFormTemplateName() + "\n");
        return formTemplate;
    }

    private FormTemplate initializeFormCelebrity() throws CreateFormTemplateException {
        FormTemplate formTemplate = new FormTemplate("Celebrity Survey Form");
        formTemplate.setIsPublic(Boolean.FALSE);
        Long formTemplateId = formTemplateSessionBeanLocal.createFormTemplate(formTemplate);
        FormTemplate otherFormTemplate = new FormTemplate(formTemplate.getFormTemplateName());
        otherFormTemplate.setFormTemplateId(formTemplateId);

        List<FormField> formFields = new ArrayList<>();
        List<FormFieldOption> formFieldOptions = new ArrayList<>();
        formFields.add(new FormField("Who is your celebrity crush?", 1, InputTypeEnum.TEXT, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, null));
        formFields.add(new FormField("Doctor Remarks:", 2, InputTypeEnum.TEXT, Boolean.TRUE, FormFieldAccessEnum.MO, null));

        otherFormTemplate.setFormFields(formFields);
        formTemplateSessionBeanLocal.saveFormTemplate(otherFormTemplate);
        formTemplateSessionBeanLocal.publishFormTemplate(otherFormTemplate.getFormTemplateId());

        System.out.println("Successfully created Form Template: " + formTemplate.getFormTemplateName() + "\n");
        return formTemplate;
    }

    private FormTemplate initializeFormAnimal() throws CreateFormTemplateException {
        FormTemplate formTemplate = new FormTemplate("Animal questionnaire");
        formTemplate.setIsPublic(Boolean.FALSE);
        Long formTemplateId = formTemplateSessionBeanLocal.createFormTemplate(formTemplate);
        FormTemplate otherFormTemplate = new FormTemplate(formTemplate.getFormTemplateName());
        otherFormTemplate.setFormTemplateId(formTemplateId);

        List<FormField> formFields = new ArrayList<>();
        List<FormFieldOption> formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("YES"));
        formFieldOptions.add(new FormFieldOption("NO"));
        formFields.add(new FormField("Do you own a pet?", 1, InputTypeEnum.RADIO_BUTTON, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, formFieldOptions));
        formFields.add(new FormField("If yes, what pet do you own?", 2, InputTypeEnum.TEXT, Boolean.FALSE, FormFieldAccessEnum.SERVICEMAN, null));

        formFields.add(new FormField("Who is your favaourite doctor?", 3, InputTypeEnum.TEXT, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, null));
        formFields.add(new FormField("Who is your favourite serviceman?", 4, InputTypeEnum.TEXT, Boolean.TRUE, FormFieldAccessEnum.MO, null));
        formFields.add(new FormField("Who is your favourite chairman?", 5, InputTypeEnum.TEXT, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN_MO, null));
        formFields.add(new FormField("Who is your favourite serviceman / docotor", 6, InputTypeEnum.TEXT, Boolean.TRUE, FormFieldAccessEnum.BOARD, null));
        
        otherFormTemplate.setFormFields(formFields);
        formTemplateSessionBeanLocal.saveFormTemplate(otherFormTemplate);
        formTemplateSessionBeanLocal.publishFormTemplate(otherFormTemplate.getFormTemplateId());

        System.out.println("Successfully created Form Template: " + formTemplate.getFormTemplateName() + "\n");
        return formTemplate;
    }

    private Long initializeForm(ConsultationPurpose consultationPurpose) throws CreateConsultationPurposeException, CreateFormTemplateException, RelinkFormTemplatesException {
        FormTemplate formTemplate = new FormTemplate("Form Template Demo 1");
        formTemplate.setIsPublic(Boolean.TRUE);
        Long formTemplateId = formTemplateSessionBeanLocal.createFormTemplate(formTemplate);
        FormTemplate otherFormTemplate = new FormTemplate(formTemplate.getFormTemplateName());
        otherFormTemplate.setFormTemplateId(formTemplateId);

        List<FormField> formFields = new ArrayList<>();
        formFields.add(new FormField("Basic Questionnaire", 1, InputTypeEnum.HEADER, Boolean.FALSE, FormFieldAccessEnum.MO, null));
        formFields.add(new FormField("What is your name?", 2, InputTypeEnum.TEXT, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, null));
        formFields.add(new FormField("What is your middle name?", 3, InputTypeEnum.TEXT, Boolean.FALSE, FormFieldAccessEnum.SERVICEMAN, null));
        formFields.add(new FormField("How old are you?", 4, InputTypeEnum.NUMBER, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, null));
        formFields.add(new FormField("When was your last vaccination?", 5, InputTypeEnum.DATE, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, null));
        formFields.add(new FormField("What time do you usually sleep?", 6, InputTypeEnum.TIME, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, null));
        List<FormFieldOption> formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("MALE"));
        formFieldOptions.add(new FormFieldOption("FEMALE"));
        formFields.add(new FormField("What is your gender?", 7, InputTypeEnum.RADIO_BUTTON, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, formFieldOptions));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("YES"));
        formFieldOptions.add(new FormFieldOption("NO"));
        formFields.add(new FormField("Do you have any allergies?", 8, InputTypeEnum.RADIO_BUTTON, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, formFieldOptions));
        formFields.add(new FormField("If yes, what allergies?", 9, InputTypeEnum.TEXT, Boolean.FALSE, FormFieldAccessEnum.SERVICEMAN, null));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("North"));
        formFieldOptions.add(new FormFieldOption("South"));
        formFieldOptions.add(new FormFieldOption("East"));
        formFieldOptions.add(new FormFieldOption("West"));
        formFieldOptions.add(new FormFieldOption("Central"));
        formFields.add(new FormField("What is your preferred location?", 10, InputTypeEnum.CHECK_BOX, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, formFieldOptions));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("A+"));
        formFieldOptions.add(new FormFieldOption("A-"));
        formFieldOptions.add(new FormFieldOption("B+"));
        formFieldOptions.add(new FormFieldOption("B-"));
        formFieldOptions.add(new FormFieldOption("AB+"));
        formFieldOptions.add(new FormFieldOption("AB-"));
        formFieldOptions.add(new FormFieldOption("O+"));
        formFieldOptions.add(new FormFieldOption("O-"));
        formFields.add(new FormField("What is your blood type?", 11, InputTypeEnum.SINGLE_DROPDOWN, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, formFieldOptions));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("Monday"));
        formFieldOptions.add(new FormFieldOption("Tuesday"));
        formFieldOptions.add(new FormFieldOption("Wednesday"));
        formFieldOptions.add(new FormFieldOption("Thursday"));
        formFieldOptions.add(new FormFieldOption("Friday"));
        formFieldOptions.add(new FormFieldOption("Saturday"));
        formFieldOptions.add(new FormFieldOption("Sunday"));
        formFields.add(new FormField("What are your preferred available days?", 12, InputTypeEnum.MULTI_DROPDOWN, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, formFieldOptions));
        formFields.add(new FormField("Doctor Remarks:", 13, InputTypeEnum.TEXT, Boolean.TRUE, FormFieldAccessEnum.MO, null));
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
        formFields.add(new FormField("Were you born in Singapore?", 1, InputTypeEnum.RADIO_BUTTON, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, formFieldOptions));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("YES"));
        formFieldOptions.add(new FormFieldOption("NO"));
        formFields.add(new FormField("Are you on any long-term medication?", 2, InputTypeEnum.RADIO_BUTTON, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, formFieldOptions));
        formFields.add(new FormField("If yes, please declare the name of drugs?", 3, InputTypeEnum.TEXT, Boolean.FALSE, FormFieldAccessEnum.SERVICEMAN, null));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("YES"));
        formFieldOptions.add(new FormFieldOption("NO"));
        formFields.add(new FormField("Are you currently having any of the following: fever, acute illness, chronic disease, bleeding disorders?", 4, InputTypeEnum.RADIO_BUTTON, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, formFieldOptions));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("YES"));
        formFieldOptions.add(new FormFieldOption("NO"));
        formFields.add(new FormField("Do you have any drug allergies?", 5, InputTypeEnum.RADIO_BUTTON, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, formFieldOptions));
        formFields.add(new FormField("If yes, please declare your drug allergies?", 6, InputTypeEnum.TEXT, Boolean.FALSE, FormFieldAccessEnum.SERVICEMAN, null));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("YES"));
        formFieldOptions.add(new FormFieldOption("NO"));
        formFields.add(new FormField("[For Female Patients] Are you currently pregnant or suspect that you may be pregnant?", 7, InputTypeEnum.RADIO_BUTTON, Boolean.FALSE, FormFieldAccessEnum.SERVICEMAN, formFieldOptions));
        formFieldOptions = new ArrayList<>();
        formFieldOptions.add(new FormFieldOption("YES"));
        formFieldOptions.add(new FormFieldOption("NO"));
        formFields.add(new FormField("Have you ever had chicken pox before or had the Varicella vaccine", 8, InputTypeEnum.RADIO_BUTTON, Boolean.TRUE, FormFieldAccessEnum.SERVICEMAN, formFieldOptions));
        formFields.add(new FormField("Vaccine to be given: Seasonal Flu", 9, InputTypeEnum.DATE, Boolean.FALSE, FormFieldAccessEnum.SERVICEMAN_MO, null));
        formFields.add(new FormField("Vaccine to be given: Tetanus", 10, InputTypeEnum.DATE, Boolean.FALSE, FormFieldAccessEnum.SERVICEMAN_MO, null));
        formFields.add(new FormField("Vaccine to be given: Hepatitis B", 11, InputTypeEnum.DATE, Boolean.FALSE, FormFieldAccessEnum.SERVICEMAN_MO, null));
        formFields.add(new FormField("Vaccine to be given: Hepatitis A", 12, InputTypeEnum.DATE, Boolean.FALSE, FormFieldAccessEnum.SERVICEMAN_MO, null));
        formFields.add(new FormField("Vaccine to be given: Typhoid", 13, InputTypeEnum.DATE, Boolean.FALSE, FormFieldAccessEnum.SERVICEMAN_MO, null));
        formFields.add(new FormField("Vaccine to be given: Varicella", 14, InputTypeEnum.DATE, Boolean.FALSE, FormFieldAccessEnum.SERVICEMAN_MO, null));
        formFields.add(new FormField("If others, please specify:", 15, InputTypeEnum.TEXT, Boolean.FALSE, FormFieldAccessEnum.SERVICEMAN_MO, null));

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
        Serviceman serviceman1 = new Serviceman("Audi More", "password", "ionic_user@hotmail.com", "98765432", ServicemanRoleEnum.REGULAR, new Date(), GenderEnum.MALE, BloodTypeEnum.A_POSITIVE, new Address("31 Kaki Bukit Road", "#06-08/11", "Techlink", "Singapore", "417818"));
        Serviceman serviceman2 = new Serviceman("Bee Am D. You", "password", "angular_user@hotmail.com", "98758434", ServicemanRoleEnum.NSF, new Date(), GenderEnum.MALE, BloodTypeEnum.A_NEGATIVE, new Address("487 Bedok South Avenue 2", "#01-00", "", "Singapore", "469316"));
        Serviceman serviceman3 = new Serviceman("Hew Jian Yiee", "password", "svcman3_user@hotmail.com", "97255472", ServicemanRoleEnum.NSMEN, new Date(), GenderEnum.MALE, BloodTypeEnum.AB_POSITIVE, new Address("172a 6th Ave", "", "", "Singapore", "276545"));
        Serviceman serviceman4 = new Serviceman("2 Way Account", "password", "dummyemailx5@hotmail.com", "87241222", ServicemanRoleEnum.NSF, new Date(), GenderEnum.MALE, BloodTypeEnum.AB_POSITIVE, new Address("513 Serangoon Road", "#04-01", "", "Singapore", "218154"));
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

        Serviceman serviceman1Otp = new Serviceman("Serviceman Activate 1", "serviceman_activate1@hotmail.com", "92856031", ServicemanRoleEnum.NSMEN, new Date(), GenderEnum.MALE, BloodTypeEnum.B_POSITIVE, new Address("Enterprise one", "#01-40", "", "Singapore", "415934"));
        Serviceman serviceman2Otp = new Serviceman("Serviceman Activate 2", "serviceman_activate2@hotmail.com", "97439534", ServicemanRoleEnum.OTHERS, new Date(), GenderEnum.MALE, BloodTypeEnum.B_NEGATIVE, new Address("2 Geylang East Avenue 2", "#04-109", "", "Singapore", "389754"));
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

        Employee emp1 = new SuperUser("Adrian Tan", "password", "dummyemailx1@hotmail.com", new Address("16 Raffles Quay", "#01-00", "Hong Leong Building", "Singapore", "048581"), "98765432", GenderEnum.MALE);
        Employee emp2 = new MedicalOfficer("Melissa Lim", "password", "dummyemailx2@hotmail.com", new Address("115A Commonwealth Drive", "#02-14", "", "Singapore", "149596"), "81234567", GenderEnum.FEMALE);
        Employee emp3 = new Clerk("Clyde", "password", "dummyemailx3@hotmail.com", new Address("501 Orchard Road", "#07-02", "Wheelock Place", "Singapore", "238880"), "92675567", GenderEnum.MALE);
        Employee emp4 = new MedicalBoardAdmin("Dylan", "password", "dummyemailx4@hotmail.com", new Address("woodlands east industrial estate 06-30", "06-30", "", "Singapore", "738733"), "88831888", GenderEnum.MALE);
        Employee emp5 = new Clerk("2 Way Account", "password", "dummyemailx5@hotmail.com", new Address("101 Eunos Avenue 3 EUNOS INDUSTRIAL ESTATE", "", "", "Singapore", "409835"), "87241222", GenderEnum.MALE);

        // NO Medical Centre Staff
        Employee emp6 = new MedicalOfficer("MedicalOfficer No MC", "password", "dummyemailx6@hotmail.com", new Address("9 Penang Road", "#10-05", "", "Singapore", "238459"), "91758375", GenderEnum.MALE);
        Employee emp7 = new Clerk("Clerk No MC", "password", "dummyemailx7@hotmail.com", new Address("141 Market Street", "#01-00", "INTERNATIONAL FACTORS BUILDING", "Singapore", "048944"), "91758375", GenderEnum.MALE);
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

        Employee emp1Otp = new SuperUser("Super User OTP", "dummyemailxxx11@hotmail.com", new Address("101 Up Cross St", "#03-38", "", "Singapore", "058357"), "92153472", GenderEnum.FEMALE);
        Employee emp2Otp = new MedicalOfficer("MO OTP", "dummyemailxxx12@hotmail.com", new Address("9 Pioneer Sector 1", "", "", "Singapore", "628421"), "94360875", GenderEnum.MALE);
        Employee emp3Otp = new Clerk("Hew Jian Yiee", "dummyemailxxx13@hotmail.com", new Address("170 Upper Bukit Timah Road", "03-02", "Bukit Timah Shopping Centre", "Singapore", "588179"), "97255472", GenderEnum.MALE);
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
        newMedicalCentre.setName("Home Team Academy Medical Centre");
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

    String[] randomStrings = {
        "Shingle color was not something the couple had ever talked about.",
        "Behind the window was a reflection that only instilled fear.",
        "She was too short to see over the fence.",
        "Greetings from the real universe.",
        "As the asteroid hurtled toward earth, Becky was upset her dentist appointment had been canceled.",
        "She always speaks to him in a loud voice.",
        "He turned in the research paper on Friday; otherwise, he would have not passed the class.",
        "He learned the hardest lesson of his life and had the scars, both physical and mental, to prove it.",
        "He used to get confused between soldiers and shoulders, but as a military man, he now soldiers responsibility.",
        "She saw the brake lights, but not in time.",
        "8% of 25 is the same as 25% of 8 and one of them is much easier to do in your head.",
        "Tom got a small piece of pie.",
        "Various sea birds are elegant, but nothing is as elegant as a gliding pelican.",
        "He went back to the video to see what had been recorded and was shocked at what he saw.",
        "Italy is my favorite country; in fact, I plan to spend two weeks there next year.",
        "The complicated school homework left the parents trying to help their kids quite confused.",
        "Excitement replaced fear until the final moment.",
        "Blue sounded too cold at the time and yet it seemed to work for gin.",
        "He told us a very exciting adventure story.",
        "Iron pyrite is the most foolish of all minerals."
    };

    private String getRandomString() {
        int randOption = ThreadLocalRandom.current().nextInt(1, randomStrings.length);
        return randomStrings[randOption];
    }

    private Integer getRandomNumber(int startInc, int endExc) {
        return ThreadLocalRandom.current().nextInt(startInc, endExc);
    }
}
