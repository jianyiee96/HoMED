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
import entity.FormTemplate;
import entity.MedicalCentre;
import entity.MedicalStaff;
import entity.Serviceman;
import entity.Slot;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import util.exceptions.CancelBookingException;
import util.exceptions.CreateBookingException;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.ServicemanNotFoundException;

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

    @Temporal(TemporalType.DATE)
    @NotNull(message = "Date must be provided")
    private Date dateToCreateBooking;

    private List<Serviceman> servicemen;

    @NotNull(message = "Booking Slot must be provided")
    private Integer createBookingIdx;

    DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public BookingManagedBean() {
        bookingSlots = new ArrayList<>();
        servicemanToCreateBooking = new Serviceman();
        formTemplateHm = new HashMap<>();
    }

    @PostConstruct
    public void postConstruct() {
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
    }

    public void initCreate() {
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
    }

    public void deleteBooking(BookingSlot slot) {
        try {
            bookingSessionBean.cancelBooking(slot.getBooking().getBookingId());
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Cancel Booking", "Successfully cancelled booking " + slot.getBooking()));
            initBookingSlots();
        } catch (CancelBookingException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cancel Booking", ex.getMessage()));
        }
    }

    public void createBooking() {
        try {
            Booking booking = bookingSessionBean.createBookingByClerk(servicemanToCreateBooking.getServicemanId(), consultationPurposeToCreateId, bookingSlotToCreateId, selectedAdditionalFormTemplatesToCreate);
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
            servicemanToCreateBooking = servicemanSessionBean.retrieveServicemanByEmail(servicemanToCreateBooking.getEmail());
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

}
