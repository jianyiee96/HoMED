package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.SlotSessionBeanLocal;
import entity.BookingSlot;
import entity.Employee;
import entity.MedicalStaff;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.ScheduleBookingSlotException;

@Named(value = "schedulerManagedBean")
@ViewScoped
public class SchedulerManagedBean implements Serializable {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @EJB(name = "SlotSessionBeanLocal")
    private SlotSessionBeanLocal slotSessionBeanLocal;

    private MedicalStaff currentMedicalStaff;

    private List<BookingSlot> bookingSlots;

    private Boolean isScheduleState;

    private ScheduleModel existingEventModel;
    private ScheduleModel newEventModel;
    private ScheduleEvent event;

    private String slotLabelInterval = "01:00";
    private String minTime = "04:00:00";
    private String maxTime = "20:00:00";

    public SchedulerManagedBean() {
        this.isScheduleState = Boolean.FALSE;
        this.existingEventModel = new DefaultScheduleModel();
        this.newEventModel = new DefaultScheduleModel();
        this.event = new DefaultScheduleEvent();
    }

    @PostConstruct
    public void postConstruct() {
        Employee currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (currentEmployee != null) {
            try {
                currentEmployee = employeeSessionBeanLocal.retrieveEmployeeById(currentEmployee.getEmployeeId());
                if (currentEmployee instanceof MedicalStaff) {
                    currentMedicalStaff = (MedicalStaff) currentEmployee;
                }
            } catch (EmployeeNotFoundException ex) {
                addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Scheduler", ex.getMessage()));
            }
        }

        refreshBookingSlots();
    }

    private void refreshBookingSlots() {
        existingEventModel.clear();
        newEventModel.clear();

        bookingSlots = slotSessionBeanLocal.retrieveBookingSlotsByMedicalCentre(1L);
        for (BookingSlot bs : bookingSlots) {
            String title = bs.getBooking() == null ? "Unbooked Slot" : "Booked Slot";
            existingEventModel.addEvent(DefaultScheduleEvent.builder()
                    .title(title)
                    .startDate(bs
                            .getStartDateTime()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime())
                    .endDate(bs
                            .getEndDateTime()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime())
                    .overlapAllowed(false)
                    .build());
        }
    }

    public void onEventSelect(SelectEvent<ScheduleEvent> selectEvent) {
        System.out.println("eventSelected");
        event = selectEvent.getObject();
    }

    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
        System.out.println("dateSelected");

        if (isScheduleState && event.getId() == null) {
            if (selectEvent.getObject().isAfter(LocalDateTime.now())) {
                event = DefaultScheduleEvent.builder()
                        .title("Scheduled Slot")
                        .startDate(selectEvent.getObject())
                        .endDate(selectEvent.getObject().plusMinutes(15))
                        .overlapAllowed(false)
                        .build();

                existingEventModel.addEvent(event);
                newEventModel.addEvent(event);
                System.out.println("=== Start of Event ===");
                newEventModel.getEvents().forEach(e -> System.out.println(e));
                System.out.println("===  End of Event  ===");
            } else {
                addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid Booking Slots Selected", "Schedules cannot be made on past dates! Please select future dates for scheduling booking slots!"));
            }
        }

        event = new DefaultScheduleEvent();
    }

    public void saveSchedule() {
        newEventModel.getEvents().forEach(e -> {
            try {
                Date rangeStart = Date
                        .from(e.getStartDate()
                                .atZone(ZoneId.systemDefault())
                                .toInstant());
                Date rangeEnd = Date
                        .from(e.getEndDate()
                                .atZone(ZoneId.systemDefault())
                                .toInstant());
                slotSessionBeanLocal.createBookingSlots(1L, rangeStart, rangeEnd);
            } catch (ScheduleBookingSlotException ex) {
                addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Scheduler", ex.getMessage()));
            }
        });

        refreshBookingSlots();
        addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Booking Slots Created", "Booking slots for consultations have been created successfully!"));
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        System.out.println("eventMoved");

        // Moved delta duration
        Duration duration = event.getDeltaAsDuration();
        LocalDateTime originalStartDateTime = event.getScheduleEvent().getStartDate().minus(duration);
        LocalDateTime originalEndDateTime = event.getScheduleEvent().getEndDate().minus(duration);

        // If not in schedule state, disable dragging.
        if (!isScheduleState) {
            revertEventDateTime(event.getScheduleEvent(), originalStartDateTime, originalEndDateTime);
        } else {
            for (BookingSlot bs : bookingSlots) {
                // If the slot currently exists in the database, disable dragging.
                if (bs.getStartDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().isEqual(originalStartDateTime)
                        && bs.getEndDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().isEqual(originalEndDateTime)) {
                    revertEventDateTime(event.getScheduleEvent(), originalStartDateTime, originalEndDateTime);
                    break;
                }
            }
        }
    }

    private void revertEventDateTime(ScheduleEvent existingEvent, LocalDateTime originalStartDateTime, LocalDateTime originalEndDateTime) {
        existingEvent.setStartDate(originalStartDateTime);
        existingEvent.setEndDate(originalEndDateTime);
        addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Booking Slots Updating Prohibited", "Updating of existing booking slots is not allowed!"));
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {

        System.out.println("eventResized");
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Start-Delta:" + event.getDeltaStartAsDuration() + ", End-Delta: " + event.getDeltaEndAsDuration());

        addMessage(message);
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage("growl-message", message);
    }

    public Boolean getIsScheduleState() {
        return isScheduleState;
    }

    public void setIsScheduleState(Boolean isScheduleState) {
        this.isScheduleState = isScheduleState;
    }

    public ScheduleModel getExistingEventModel() {
        return existingEventModel;
    }

    public void setExistingEventModel(ScheduleModel existingEventModel) {
        this.existingEventModel = existingEventModel;
    }

    public ScheduleModel getNewEventModel() {
        return newEventModel;
    }

    public void setNewEventModel(ScheduleModel newEventModel) {
        this.newEventModel = newEventModel;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public String getSlotLabelInterval() {
        return slotLabelInterval;
    }

    public void setSlotLabelInterval(String slotLabelInterval) {
        this.slotLabelInterval = slotLabelInterval;
    }

    public String getMinTime() {
        return minTime;
    }

    public void setMinTime(String minTime) {
        this.minTime = minTime;
    }

    public String getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

}
