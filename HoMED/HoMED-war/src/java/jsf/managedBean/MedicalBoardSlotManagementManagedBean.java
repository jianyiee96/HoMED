/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.SlotSessionBeanLocal;
import entity.Employee;
import entity.MedicalBoardAdmin;
import entity.MedicalBoardSlot;
import entity.MedicalStaff;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import util.exceptions.RemoveSlotException;
import util.exceptions.ScheduleBookingSlotException;

@Named(value = "medicalBoardSlotManagementManagedBean")
@ViewScoped
public class MedicalBoardSlotManagementManagedBean implements Serializable {

    @EJB(name = "SlotSessionBeanLocal")
    private SlotSessionBeanLocal slotSessionBeanLocal;

    private MedicalStaff currentMedicalBoardAdmin;

    private List<MedicalBoardSlot> medicalBoardSlots;

    private Map<MedicalBoardSlot, ScheduleEvent> selectedMedicalBoardSlotsTreeMap;
    private Set<MedicalBoardSlot> selectedMedicalBoardSlotsTreeSet;
    private Map<MedicalBoardSlot, Integer> selectedMedicalBoardSlotsMapping;

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
        this.selectedMedicalBoardSlotsTreeMap = new TreeMap<>();
        Employee currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (currentEmployee != null && currentEmployee instanceof MedicalBoardAdmin) {
            currentMedicalBoardAdmin = (MedicalBoardAdmin) currentEmployee;
        }
        refreshMedicalBoardSlots();
    }

    private void refreshMedicalBoardSlots() {
        existingEventModel.clear();
        newEventModel.clear();

        selectedMedicalBoardSlotsMapping = new HashMap<>();
        medicalBoardSlots = slotSessionBeanLocal.retrieveMedicalBoardSlots();

        int startHour = 24;
        int endHour = 0;

        for (MedicalBoardSlot mbs : medicalBoardSlots) {
            startHour = Math.min(startHour, mbs.getStartHour());
            endHour = Math.max(endHour, mbs.getEndHour());

            // Need to handle for Medical Board Status in SR4
            if (mbs.getMedicalBoard() != null) {
                existingEventModel.addEvent(DefaultScheduleEvent.builder()
                        .title("Upcoming")
                        .startDate(mbs.getStartDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                        .endDate(mbs.getEndDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                        .overlapAllowed(false)
                        .editable(false)
                        .styleClass("booked-medical-board-slot")
                        .data(mbs)
                        .build());
            } else {
                if (mbs.getEndDateTime().after(new Date())) {
                    existingEventModel.addEvent(DefaultScheduleEvent.builder()
                            .title("Available")
                            .startDate(mbs.getStartDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                            .endDate(mbs.getEndDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                            .overlapAllowed(false)
                            .editable(false)
                            .styleClass("available-medical-board-slot")
                            .data(mbs)
                            .build());
                } else {
                    existingEventModel.addEvent(DefaultScheduleEvent.builder()
                            .title("Expired")
                            .startDate(mbs.getStartDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                            .endDate(mbs.getEndDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                            .overlapAllowed(false)
                            .editable(false)
                            .styleClass("expired-medical-board-slot")
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

    private void updateSelectedMedicalBoardSlotsView(MedicalBoardSlot medicalBoardSlotToRemove) {
        int idx = 0;
        for (Map.Entry<MedicalBoardSlot, ScheduleEvent> entrySet : selectedMedicalBoardSlotsTreeMap.entrySet()) {
            MedicalBoardSlot mbs = entrySet.getKey();
            ScheduleEvent event = entrySet.getValue();

            idx++;
            String title = "";
            String styleClass = "selected-booking-slot";
            boolean isSameMedicalBoardSlot = mbs.equals(medicalBoardSlotToRemove);
            if (isSameMedicalBoardSlot) {
                idx--;
            }

            if (mbs.getMedicalBoard() != null) {
                title = "Upcoming";
                if (isSameMedicalBoardSlot) {
                    styleClass = "booked-medical-board-slot";
                } else {
                    title += "[" + idx + "]";
                }
            } else {
                if (mbs.getEndDateTime().after(new Date())) {
                    title = "Available";
                    if (isSameMedicalBoardSlot) {
                        styleClass = "available-medical-board-slot";
                    } else {
                        title += "[" + idx + "]";
                    }
                } else {
                    title = "Expired";
                    if (isSameMedicalBoardSlot) {
                        styleClass = "expired-medical-board-slot";
                    } else {
                        title += "[" + idx + "]";
                    }
                }
            }

            selectedMedicalBoardSlotsMapping.put(mbs, idx);

            existingEventModel.updateEvent(DefaultScheduleEvent.builder()
                    .id(event.getId())
                    .title(title)
                    .startDate(event.getStartDate())
                    .endDate(event.getEndDate())
                    .overlapAllowed(event.isOverlapAllowed())
                    .editable(event.isEditable())
                    .styleClass(styleClass)
                    .data(mbs)
                    .build());
        }
    }

    public void onEventSelect(SelectEvent<ScheduleEvent> selectEvent) {
        if (!isScheduleState) {
            event = selectEvent.getObject();

            if (event.getData() != null) {
                MedicalBoardSlot mbs = (MedicalBoardSlot) event.getData();
                if (!this.selectedMedicalBoardSlotsTreeMap.containsKey(mbs)) {
                    if (selectedMedicalBoardSlotsTreeMap.size() >= 10) {
                        addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Too Many Booking Slots Selected", "Only a maximum of 10 medical board slots can be selected in one shot!"));
                    } else {
                        this.selectedMedicalBoardSlotsTreeMap.put(mbs, event);
                        updateSelectedMedicalBoardSlotsView(null);
                    }
                } else {
                    updateSelectedMedicalBoardSlotsView(mbs);
                    this.selectedMedicalBoardSlotsTreeMap.remove(mbs);
                }
            }
        } else {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Existing Medical Board Slot Selection Not Allowed", "You are not allowed to select existing medical board slots while scheduling new medical board slots!"));
        }

        event = new DefaultScheduleEvent();
    }

    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
        if (isScheduleState && event.getId() == null) {
            if (selectEvent.getObject().isAfter(LocalDateTime.now())) {
                event = DefaultScheduleEvent.builder()
                        .title("New")
                        .startDate(selectEvent.getObject())
                        .endDate(selectEvent.getObject().plusMinutes(30))
                        .overlapAllowed(false)
                        .styleClass("new-medical-board-slot")
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
                    Date rangeStart = Date.from(e.getStartDate().atZone(ZoneId.systemDefault()).toInstant());
                    Date rangeEnd = Date.from(e.getEndDate().atZone(ZoneId.systemDefault()).toInstant());
                    slotSessionBeanLocal.createMedicalBoardSlots(rangeStart, rangeEnd);
                } catch (ScheduleBookingSlotException ex) {
                    addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Scheduler", ex.getMessage()));
                }
            });
            addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Medical Board Slots Created", "Medical Board slots for medical board reviews have been created successfully!"));
        }

        refreshMedicalBoardSlots();
    }

    public void reset() {
        refreshMedicalBoardSlots();
    }

    public String renderDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        return dateFormat.format(date);
    }

    public void closeSelectedMedicalBoardSlot(MedicalBoardSlot medicalBoardSlot) {
        updateSelectedMedicalBoardSlotsView(medicalBoardSlot);
        this.selectedMedicalBoardSlotsTreeMap.remove(medicalBoardSlot);
    }

    public void deleteSelectedMedicalBoardSlot(MedicalBoardSlot medicalBoardSlot) {
        try {
            slotSessionBeanLocal.removeMedicalBoardSlot(medicalBoardSlot.getSlotId());

            updateSelectedMedicalBoardSlotsView(medicalBoardSlot);
            this.existingEventModel.deleteEvent(this.selectedMedicalBoardSlotsTreeMap.get(medicalBoardSlot));

            this.selectedMedicalBoardSlotsTreeMap.remove(medicalBoardSlot);
            this.selectedMedicalBoardSlotsTreeSet.remove(medicalBoardSlot);
        } catch (RemoveSlotException ex) {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Unable to Delete Booking Slot", ex.getMessage()));
        }
    }

    public void deleteAllSelectedMedicalBoardSlots() {
        List<MedicalBoardSlot> mbsToBeRemoved = new ArrayList<>();

        this.selectedMedicalBoardSlotsTreeSet.forEach(mbs -> {
            if (mbs.getMedicalBoard() == null && !beforeNow(mbs.getStartDateTime())) {
                try {
                    slotSessionBeanLocal.removeMedicalBoardSlot(mbs.getSlotId());
                    mbsToBeRemoved.add(mbs);

                } catch (RemoveSlotException ex) {
                    addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Unable to Delete Medical Board Slot", ex.getMessage()));
                }
            }
        });

        mbsToBeRemoved.forEach(mbs -> {
            updateSelectedMedicalBoardSlotsView(mbs);
            this.existingEventModel.deleteEvent(this.selectedMedicalBoardSlotsTreeMap.get(mbs));

            this.selectedMedicalBoardSlotsTreeMap.remove(mbs);
            this.selectedMedicalBoardSlotsTreeSet.remove(mbs);
        });
    }

    public boolean beforeNow(Date date) {
        return date.before(new Date());
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage("growl-message", message);
    }

    public String getMedicalBoardSlotStatus(MedicalBoardSlot medicalBoardSlot) {
        if (medicalBoardSlot.getMedicalBoard() != null) {
            return "BOOKED";
        } else {
            if (medicalBoardSlot.getStartDateTime().after(new Date())) {
                return "AVAILABLE";
            } else {
                return "EXPIRED";
            }
        }
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

    public Map<MedicalBoardSlot, ScheduleEvent> getSelectedMedicalBoardSlotsTreeMap() {
        return selectedMedicalBoardSlotsTreeMap;
    }

    public Set<MedicalBoardSlot> getSelectedMedicalBoardSlotsTreeSet() {
        selectedMedicalBoardSlotsTreeSet = new TreeSet<>();

        selectedMedicalBoardSlotsTreeMap.entrySet().forEach(entrySet -> {
            selectedMedicalBoardSlotsTreeSet.add(entrySet.getKey());
        });

        return selectedMedicalBoardSlotsTreeSet;
    }

    public Map<MedicalBoardSlot, Integer> getSelectedMedicalBoardSlotsMapping() {
        return selectedMedicalBoardSlotsMapping;
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
        if (isScheduleState) {
            return "00:00:00";
        } else {
            return minTime;
        }
    }

    public void setMinTime(String minTime) {
        this.minTime = minTime;
    }

    public String getMaxTime() {
        if (isScheduleState) {
            return "24:00:00";
        } else {
            return maxTime;
        }
    }

    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

}
