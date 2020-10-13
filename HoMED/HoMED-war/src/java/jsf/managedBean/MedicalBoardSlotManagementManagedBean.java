/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.SlotSessionBeanLocal;
import entity.BookingSlot;
import entity.Employee;
import entity.MedicalBoardAdmin;
import entity.MedicalBoardSlot;
import entity.MedicalCentre;
import entity.MedicalStaff;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import util.enumeration.BookingStatusEnum;
import util.exceptions.ScheduleBookingSlotException;

@Named(value = "medicalBoardSlotManagementManagedBean")
@ViewScoped
public class MedicalBoardSlotManagementManagedBean implements Serializable {

    @EJB(name = "SlotSessionBeanLocal")
    private SlotSessionBeanLocal slotSessionBeanLocal;

    private MedicalStaff currentMedicalBoardAdmin;

    private List<MedicalBoardSlot> medicalBoardSlots;

    private TreeSet<MedicalBoardSlot> selectedMedicalBoardSlots;

    private Boolean isScheduleState;

    private ScheduleModel existingEventModel;
    private ScheduleModel newEventModel;
    private ScheduleEvent event;

    private String slotLabelInterval = "01:00";
    private String minTime = "00:00:00";
    private String maxTime = "24:00:00";

    public MedicalBoardSlotManagementManagedBean() {
        this.isScheduleState = Boolean.FALSE;
        this.existingEventModel = new DefaultScheduleModel();
        this.newEventModel = new DefaultScheduleModel();
        this.event = new DefaultScheduleEvent();
    }

    @PostConstruct
    public void postConstruct() {
        this.selectedMedicalBoardSlots = new TreeSet<>();
        Employee currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (currentEmployee != null && currentEmployee instanceof MedicalBoardAdmin) {
            currentMedicalBoardAdmin = (MedicalBoardAdmin) currentEmployee;
        }
        refreshBookingSlots();
    }

    private void refreshBookingSlots() {
        existingEventModel.clear();
        newEventModel.clear();

        Date now = new Date();

        medicalBoardSlots = slotSessionBeanLocal.retrieveMedicalBoardSlots();

        int startHour = 24;
        int endHour = 0;

        for (MedicalBoardSlot mbs : medicalBoardSlots) {

            startHour = Math.min(startHour, mbs.getStartHour());
            endHour = Math.max(endHour, mbs.getEndHour());

            // Need to handle for Medical Board Status
            if (mbs.getMedicalBoard() != null) {
                existingEventModel.addEvent(DefaultScheduleEvent.builder()
                        .title("Upcoming")
                        .startDate(mbs
                                .getStartDateTime()
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                        )
                        .endDate(mbs
                                .getEndDateTime()
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                        )
                        .overlapAllowed(false)
                        .editable(false)
                        .styleClass("booked-booking-slot")
                        .data(mbs)
                        .build()
                );
            } else {
                if (mbs.getEndDateTime().after(now)) {
                    existingEventModel.addEvent(DefaultScheduleEvent.builder()
                            .title("Available")
                            .startDate(mbs
                                    .getStartDateTime()
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime())
                            .endDate(mbs
                                    .getEndDateTime()
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime())
                            .overlapAllowed(false)
                            .editable(false)
                            .styleClass("booking-slot")
                            .data(mbs)
                            .build());
                } else {
                    existingEventModel.addEvent(DefaultScheduleEvent.builder()
                            .title("Expired")
                            .startDate(mbs
                                    .getStartDateTime()
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime())
                            .endDate(mbs
                                    .getEndDateTime()
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime())
                            .overlapAllowed(false)
                            .editable(false)
                            .styleClass("expired-booking-slot")
                            .data(mbs)
                            .build());
                }
            }
        }

        if (startHour >= endHour) {
            startHour = 0;
            endHour = 24;
        }

        if (startHour >= 1) {
            startHour--;
        }

        if (endHour <= 23) {
            endHour++;
        }

        this.minTime = startHour + ":00:00";
        this.maxTime = endHour + ":00:00";
    }

    public void onEventSelect(SelectEvent<ScheduleEvent> selectEvent) {
        System.out.println("onEventSelect");
        event = selectEvent.getObject();

        if (event.getData() != null) {
            MedicalBoardSlot mbs = (MedicalBoardSlot) event.getData();
            if (!this.selectedMedicalBoardSlots.contains(mbs)) {
                this.selectedMedicalBoardSlots.add(mbs);
            }
        }
    }

    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
        System.out.println("onDateSelect");
        if (isScheduleState && event.getId() == null) {
            if (selectEvent.getObject().isAfter(LocalDateTime.now())) {
                event = DefaultScheduleEvent.builder()
                        .title("New Medical Board Slots")
                        .startDate(selectEvent.getObject())
                        .endDate(selectEvent.getObject().plusMinutes(30))
                        .overlapAllowed(false)
                        .styleClass("new-booking-slot")
                        .build();

                existingEventModel.addEvent(event);
                newEventModel.addEvent(event);
            } else {
                addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid Medical Board Slots Selected", "Schedules cannot be made on past dates! Please select future dates for scheduling medical board slots!"));
            }
        }

        event = new DefaultScheduleEvent();
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        ScheduleEvent updatedEvent = event.getScheduleEvent();

        // Moved delta duration
        Duration duration = event.getDeltaAsDuration();
        LocalDateTime originalStartDateTime = updatedEvent.getStartDate().minus(duration);
        LocalDateTime originalEndDateTime = updatedEvent.getEndDate().minus(duration);

        // If the slot does not exist, but drag to invalid slots (past dates), revert.
        if (updatedEvent.getStartDate().isBefore(LocalDateTime.now())) {
            updatedEvent.setStartDate(originalStartDateTime);
            updatedEvent.setEndDate(originalEndDateTime);

            addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid Medical Board Slots Dragged", "Schedules cannot be made on past dates! Please select future dates for scheduling medical board slots!"));
        }
    }

    public void saveSchedule() {

        if (!newEventModel.getEvents().isEmpty()) {
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
                    slotSessionBeanLocal.createMedicalBoardSlots(rangeStart, rangeEnd);
                } catch (ScheduleBookingSlotException ex) {
                    addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Scheduler", ex.getMessage()));
                }
            });
            addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Medical Board Slots Created", "Medical Board slots for consultations have been created successfully!"));
        }

        refreshBookingSlots();

    }

    public void reset() {
        refreshBookingSlots();
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage("growl-message", message);
    }

    public MedicalStaff getCurrentMedicalBoardAdmin() {
        return currentMedicalBoardAdmin;
    }

    public void setCurrentMedicalBoardAdmin(MedicalStaff currentMedicalBoardAdmin) {
        this.currentMedicalBoardAdmin = currentMedicalBoardAdmin;
    }

    public List<MedicalBoardSlot> getMedicalBoardSlots() {
        return medicalBoardSlots;
    }

    public void setMedicalBoardSlots(List<MedicalBoardSlot> medicalBoardSlots) {
        this.medicalBoardSlots = medicalBoardSlots;
    }

    public TreeSet<MedicalBoardSlot> getSelectedMedicalBoardSlots() {
        return selectedMedicalBoardSlots;
    }

    public void setSelectedMedicalBoardSlots(TreeSet<MedicalBoardSlot> selectedMedicalBoardSlots) {
        this.selectedMedicalBoardSlots = selectedMedicalBoardSlots;
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
