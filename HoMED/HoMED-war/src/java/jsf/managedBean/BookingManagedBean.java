package jsf.managedBean;

import ejb.session.stateless.BookingSessionBeanLocal;
import ejb.session.stateless.ConsultationPurposeSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.FormTemplateSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import ejb.session.stateless.SlotSessionBeanLocal;
import entity.Booking;
import entity.BookingSlot;
import entity.ConsultationPurpose;
import entity.Employee;
import entity.FormInstance;
import entity.FormTemplate;
import entity.MedicalCentre;
import entity.MedicalStaff;
import entity.Serviceman;
import entity.Slot;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.primefaces.PrimeFaces;
import util.enumeration.BookingStatusEnum;
import util.exceptions.AttachFormInstancesException;
import util.exceptions.CancelBookingException;
import util.exceptions.CreateBookingException;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.MarkBookingAttendanceException;
import util.exceptions.ServicemanNotFoundException;
import util.exceptions.UpdateBookingCommentException;

@Named(value = "bookingManagedBean")
@ViewScoped
public class BookingManagedBean implements Serializable {

    @EJB
    private FormTemplateSessionBeanLocal formTemplateSessionBean;

    @EJB
    private ConsultationPurposeSessionBeanLocal consultationPurposeSessionBean;

    @EJB
    private ServicemanSessionBeanLocal servicemanSessionBean;

    @EJB
    private BookingSessionBeanLocal bookingSessionBean;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;

    @EJB
    private SlotSessionBeanLocal slotSessionBean;

    private MedicalStaff currentMedicalStaff;

    private MedicalCentre currentMedicalCentre;

    private List<BookingSlot> bookingSlots;

    private List<BookingSlot> filteredBookingSlots;

    private String servicemanEmailToCreate;

    private Serviceman servicemanToCreateBooking;

    private Long bookingSlotToCreateId;

    private List<BookingSlot> bookingSlotsToCreate;

    private List<ConsultationPurpose> consultationPurposes;

    private Long consultationPurposeToCreateId;

    private List<FormTemplate> alreadyLinkedFormTemplates;

    private List<FormTemplate> additionalFormTemplates;

    private List<Long> selectedAdditionalFormTemplatesToCreate;

    private List<FormTemplate> publishedFormTemplates;

    private HashMap<Long, String> formTemplateHm;

    private BookingSlot bookingSlotToUpdateDetails;

    private String bookingComment;

    private Integer filterOption;

    private String cancelBookingComments;

    private BookingSlot bookingSlotToCancel;

    @Temporal(TemporalType.DATE)
    @NotNull(message = "Date must be provided")
    private Date dateToCreateBooking;

    private List<Serviceman> servicemen;

    @NotNull(message = "Booking Slot must be provided")
    private Integer createBookingIdx;

    DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public BookingManagedBean() {
        bookingSlots = new ArrayList<>();
        filteredBookingSlots = new ArrayList<>();
        servicemanToCreateBooking = new Serviceman();
        formTemplateHm = new HashMap<>();
    }

    @PostConstruct
    public void postConstruct() {
        filterOption = 1;
        Employee currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (currentEmployee != null) {
            try {
                currentEmployee = employeeSessionBean.retrieveEmployeeById(currentEmployee.getEmployeeId());
                if (currentEmployee instanceof MedicalStaff) {
                    currentMedicalStaff = (MedicalStaff) currentEmployee;
                    currentMedicalCentre = currentMedicalStaff.getMedicalCentre();
                    if (currentMedicalCentre != null) {
                        initBookingSlots();
                    }
                }
            } catch (EmployeeNotFoundException ex) {
                FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bookings", ex.getMessage()));
            }
        }
    }

    public void initBookingSlots() {
        bookingSlots = slotSessionBean.retrieveBookingSlotsWithBookingsByMedicalCentre(currentMedicalCentre.getMedicalCentreId());
        doFilterBookings();
    }

    public void initCreate() {
        servicemanEmailToCreate = "";
        servicemanToCreateBooking = new Serviceman();
        servicemen = servicemanSessionBean.retrieveAllServicemen();
        dateToCreateBooking = null;
        createBookingIdx = 0;
        bookingSlotsToCreate = new ArrayList<>();
        consultationPurposeToCreateId = null;
        consultationPurposes = consultationPurposeSessionBean.retrieveAllConsultationPurposes();
        publishedFormTemplates = formTemplateSessionBean.retrieveAllPublishedFormTemplates();
        selectedAdditionalFormTemplatesToCreate = new ArrayList<>();
        formTemplateHm = new HashMap<>();
        publishedFormTemplates.forEach(ft -> formTemplateHm.put(ft.getFormTemplateId(), ft.getFormTemplateName()));
        alreadyLinkedFormTemplates = new ArrayList<>();
        additionalFormTemplates = new ArrayList<>();
        bookingComment = null;
    }

    public void deleteBooking() {
        try {
            bookingSessionBean.cancelBookingByClerk(bookingSlotToCancel.getBooking().getBookingId(), cancelBookingComments);
            cancelBookingComments = "";
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Cancel Booking", "Successfully cancelled booking " + bookingSlotToCancel.getBooking()));
            initBookingSlots();
            PrimeFaces.current().executeScript("PF('dlgCancelBooking').hide()");
        } catch (CancelBookingException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cancel Booking: " + ex.getMessage(), null));
        }
    }

    public void doUpdateBookingDetails(BookingSlot slot) {
        bookingSlotToUpdateDetails = slotSessionBean.retrieveBookingSlotById(slot.getSlotId());
        if (bookingSlotToUpdateDetails.getBooking().getBookingComment() == null) {
            bookingSlotToUpdateDetails.getBooking().setBookingComment("");
        }
        selectedAdditionalFormTemplatesToCreate = new ArrayList<>();
        publishedFormTemplates = formTemplateSessionBean.retrieveAllPublishedFormTemplates();
        formTemplateHm = new HashMap<>();
        publishedFormTemplates.forEach(ft -> formTemplateHm.put(ft.getFormTemplateId(), ft.getFormTemplateName()));

        alreadyLinkedFormTemplates = bookingSlotToUpdateDetails.getBooking().getFormInstances().stream()
                .map(fi -> fi.getFormTemplateMapping())
                .collect(Collectors.toList());

        additionalFormTemplates = publishedFormTemplates.stream()
                .filter(ft -> alreadyLinkedFormTemplates.stream().noneMatch(lft -> lft.getFormTemplateId().equals(ft.getFormTemplateId())))
                .collect(Collectors.toList());
    }

    public void updateBooking() {

        try {
            bookingSessionBean.updateBookingComment(bookingSlotToUpdateDetails.getBooking().getBookingId(), bookingSlotToUpdateDetails.getBooking().getBookingComment());
            bookingSessionBean.attachFormInstancesByClerk(bookingSlotToUpdateDetails.getSlotId(), selectedAdditionalFormTemplatesToCreate);
            PrimeFaces.current().executeScript("PF('dialogAttachAdditionalForms').hide()");
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Update Booking Details", "Successfully updated and attached any additional forms for booking " + bookingSlotToUpdateDetails.getBooking()));
            initBookingSlots();
        } catch (AttachFormInstancesException | UpdateBookingCommentException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Update Booking Details", ex.getMessage()));
        }
    }

    public String initMarkAttendance(BookingSlot slot) {
        String msg = "You will not be allowed to revert your action.";
        
        Calendar date = Calendar.getInstance();
        date.add(Calendar.HOUR, 1);
        
        if (slot.getStartDateTime().after(date.getTime())) {
            return "<p>The booking is more than one hour ahead of the scheduled time. Are you sure you want to mark attendance?</p>" + msg;
        }
        
        return msg;
    }

    public void markAttendance(BookingSlot slot) {
        try {
            bookingSessionBean.markBookingAttendance(slot.getBooking().getBookingId());
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Mark Attendance", "Successfully marked attendance for booking " + slot.getBooking()));
            initBookingSlots();
        } catch (MarkBookingAttendanceException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Mark Attendance", ex.getMessage()));
        }
    }

    public void createBooking() {
        try {
            Booking booking = bookingSessionBean.createBookingByClerk(servicemanToCreateBooking.getServicemanId(), consultationPurposeToCreateId, bookingSlotToCreateId, selectedAdditionalFormTemplatesToCreate, bookingComment);
            PrimeFaces.current().executeScript("PF('dialogCreateBooking').hide()");
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Create Booking", "Successfully created booking " + booking));
            initBookingSlots();
        } catch (CreateBookingException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public int sortByCustom(Object obj1, Object obj2) {
        BookingSlot slot1 = (BookingSlot) obj1;
        BookingSlot slot2 = (BookingSlot) obj2;

        if (slot1.getBooking().getBookingStatusEnum() == BookingStatusEnum.UPCOMING && slot2.getBooking().getBookingStatusEnum() == BookingStatusEnum.UPCOMING) {
            if (slot1.getStartDateTime().before(slot2.getStartDateTime())) {
                return -1;
            } else {
                return 1;
            }
        } else {
            if (slot1.getBooking().getBookingStatusEnum() == BookingStatusEnum.UPCOMING) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public List<String> completeEmail(String email) {
        String queryLowerCase = email.toLowerCase();
        return servicemen.stream()
                .map(s -> s.getEmail())
                .filter(s -> {
                    return s.toLowerCase().contains(queryLowerCase);
                })
                .collect(Collectors.toList());
    }

    public void retrieveServiceman() {
        try {
            servicemanToCreateBooking = servicemanSessionBean.retrieveServicemanByEmail(servicemanEmailToCreate);
        } catch (ServicemanNotFoundException ex) {
            servicemanToCreateBooking = new Serviceman();
        }
    }

    public void retrieveMedicalCentreBookingSlots() {
        Date currentTime = new Date();
        bookingSlotsToCreate = slotSessionBean.retrieveMedicalCentreBookingSlotsByDate(currentMedicalCentre.getMedicalCentreId(), dateToCreateBooking).stream()
                .filter(bs -> bs.getStartDateTime().after(currentTime) && bs.getBooking() == null)
                .collect(Collectors.toList());
    }

    public void selectConsultationPurpose() {

        if (consultationPurposeToCreateId == null) {
            alreadyLinkedFormTemplates = new ArrayList<>();
            additionalFormTemplates = new ArrayList<>();
        } else {
            selectedAdditionalFormTemplatesToCreate = new ArrayList<>();

            alreadyLinkedFormTemplates = consultationPurposes.stream()
                    .filter(cp -> cp.getConsultationPurposeId().equals(this.consultationPurposeToCreateId))
                    .findFirst()
                    .map(cp -> cp.getFormTemplates())
                    .orElse(new ArrayList<>());

            additionalFormTemplates = publishedFormTemplates.stream()
                    .filter(ft -> alreadyLinkedFormTemplates.stream().noneMatch(lft -> lft.getFormTemplateId().equals(ft.getFormTemplateId())))
                    .collect(Collectors.toList());
        }
    }

    public void doFilterBookings() {
        // 1 - Day
        // 2 - Week
        // 3 - Month
        // 4 - All
        filteredBookingSlots = bookingSlots.stream()
                .filter(bs -> {
                    if (filterOption == 1) {
                        return isDateInCurrentDay(bs.getStartDateTime());
                    } else if (filterOption == 2) {
                        return isDateInCurrentWeek(bs.getStartDateTime());
                    } else if (filterOption == 3) {
                        return isDateinCurrentMonth(bs.getStartDateTime());
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }

    public boolean isDateInCurrentDay(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        int day = currentCalendar.get(Calendar.DAY_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        int targetDay = targetCalendar.get(Calendar.DAY_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        return day == targetDay && year == targetYear;
    }

    public boolean isDateInCurrentWeek(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        return week == targetWeek && year == targetYear;
    }

    public boolean isDateinCurrentMonth(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        int month = currentCalendar.get(Calendar.MONTH);
        int year = currentCalendar.get(Calendar.YEAR);
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        int targetMonth = targetCalendar.get(Calendar.MONTH);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        return month == targetMonth && year == targetYear;
    }

    public boolean checkCompleted(FormInstance fi) {
        return !fi.getFormInstanceFields().stream()
                .anyMatch(fif -> {
                    if (fif.getFormFieldMapping().getIsServicemanEditable() && fif.getFormFieldMapping().getIsRequired()) {
                        return fif.getFormInstanceFieldValues().isEmpty() || fif.getFormInstanceFieldValues().get(0).getInputValue() == null
                                || fif.getFormInstanceFieldValues().get(0).getInputValue().equals("");
                    }
                    return false;
                });
    }

    public MedicalStaff getCurrentMedicalStaff() {
        return currentMedicalStaff;
    }

    public void setCurrentMedicalStaff(MedicalStaff currentMedicalStaff) {
        this.currentMedicalStaff = currentMedicalStaff;
    }

    public MedicalCentre getCurrentMedicalCentre() {
        return currentMedicalCentre;
    }

    public void setCurrentMedicalCentre(MedicalCentre currentMedicalCentre) {
        this.currentMedicalCentre = currentMedicalCentre;
    }

    public List<BookingSlot> getBookingSlots() {
        return bookingSlots;
    }

    public void setBookingSlots(List<BookingSlot> bookingSlots) {
        this.bookingSlots = bookingSlots;
    }

    public Serviceman getServicemanToCreateBooking() {
        return servicemanToCreateBooking;
    }

    public void setServicemanToCreateBooking(Serviceman servicemanToCreateBooking) {
        this.servicemanToCreateBooking = servicemanToCreateBooking;
    }

    public List<Serviceman> getServicemen() {
        return servicemen;
    }

    public void setServicemen(List<Serviceman> servicemen) {
        this.servicemen = servicemen;
    }

    public Integer getCreateBookingIdx() {
        return createBookingIdx;
    }

    public void setCreateBookingIdx(Integer createBookingIdx) {
        this.createBookingIdx = createBookingIdx;
    }

    public Date getDateToCreateBooking() {
        return dateToCreateBooking;
    }

    public void setDateToCreateBooking(Date dateToCreateBooking) {
        this.dateToCreateBooking = dateToCreateBooking;
    }

    public Date getCurrentDate() {
        return new Date();
    }

    public List<BookingSlot> getBookingSlotsToCreate() {
        return bookingSlotsToCreate;
    }

    public void setBookingSlotsToCreate(List<BookingSlot> bookingSlotsToCreate) {
        this.bookingSlotsToCreate = bookingSlotsToCreate;
    }

    public Long getBookingSlotToCreateId() {
        return bookingSlotToCreateId;
    }

    public void setBookingSlotToCreateId(Long bookingSlotToCreateId) {
        this.bookingSlotToCreateId = bookingSlotToCreateId;
    }

    public DateFormat getTimeFormat() {
        return timeFormat;
    }

    public List<ConsultationPurpose> getConsultationPurposes() {
        return consultationPurposes;
    }

    public void setConsultationPurposes(List<ConsultationPurpose> consultationPurposes) {
        this.consultationPurposes = consultationPurposes;
    }

    public Long getConsultationPurposeToCreateId() {
        return consultationPurposeToCreateId;
    }

    public void setConsultationPurposeToCreateId(Long consultationPurposeToCreateId) {
        this.consultationPurposeToCreateId = consultationPurposeToCreateId;
    }

    public List<FormTemplate> getPublishedFormTemplates() {
        return publishedFormTemplates;
    }

    public void setPublishedFormTemplates(List<FormTemplate> publishedFormTemplates) {
        this.publishedFormTemplates = publishedFormTemplates;
    }

    public List<FormTemplate> getAlreadyLinkedFormTemplates() {
        return alreadyLinkedFormTemplates;
    }

    public List<FormTemplate> getAdditionalFormTemplates() {
        return additionalFormTemplates;
    }

    public List<Long> getSelectedAdditionalFormTemplatesToCreate() {
        return selectedAdditionalFormTemplatesToCreate;
    }

    public void setSelectedAdditionalFormTemplatesToCreate(List<Long> selectedAdditionalFormTemplatesToCreate) {
        this.selectedAdditionalFormTemplatesToCreate = selectedAdditionalFormTemplatesToCreate;
    }

    public HashMap<Long, String> getFormTemplateHm() {
        return formTemplateHm;
    }

    public String getServicemanEmailToCreate() {
        return servicemanEmailToCreate;
    }

    public void setServicemanEmailToCreate(String servicemanEmailToCreate) {
        this.servicemanEmailToCreate = servicemanEmailToCreate;
    }

    public Integer getFilterOption() {
        return filterOption;
    }

    public void setFilterOption(Integer filterOption) {
        this.filterOption = filterOption;
    }

    public List<BookingSlot> getFilteredBookingSlots() {
        return filteredBookingSlots;
    }

    public void setFilteredBookingSlots(List<BookingSlot> filteredBookingSlots) {
        this.filteredBookingSlots = filteredBookingSlots;
    }

    public BookingSlot getBookingSlotToUpdateDetails() {
        return bookingSlotToUpdateDetails;
    }

    public void setBookingSlotToUpdateDetails(BookingSlot bookingSlotToUpdateDetails) {
        this.bookingSlotToUpdateDetails = bookingSlotToUpdateDetails;
    }

    public String getBookingComment() {
        return bookingComment;
    }

    public void setBookingComment(String bookingComment) {
        this.bookingComment = bookingComment;
    }

    public String getCancelBookingComments() {
        return cancelBookingComments;
    }

    public void setCancelBookingComments(String cancelBookingComments) {
        this.cancelBookingComments = cancelBookingComments;
    }

    public BookingSlot getBookingSlotToCancel() {
        return bookingSlotToCancel;
    }

    public void setBookingSlotToCancel(BookingSlot bookingSlotToCancel) {
        this.bookingSlotToCancel = bookingSlotToCancel;
    }

}
