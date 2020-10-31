/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.SlotSessionBeanLocal;
import entity.Employee;
import entity.MedicalBoardAdmin;
import entity.MedicalBoardSlot;
import entity.MedicalOfficer;
import entity.MedicalStaff;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import jsf.classes.MedicalBoardSlotWrapper;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import util.exceptions.RemoveSlotException;
import util.exceptions.ScheduleMedicalBoardSlotException;
import util.exceptions.UpdateMedicalBoardSlotException;

@Named(value = "medicalBoardSlotManagementManagedBean")
@ViewScoped
public class MedicalBoardSlotManagementManagedBean implements Serializable {
    
    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;
    
    @EJB(name = "SlotSessionBeanLocal")
    private SlotSessionBeanLocal slotSessionBeanLocal;
    
    private MedicalStaff currentMedicalBoardAdmin;
    
    private Set<MedicalBoardSlotWrapper> selectedMedicalBoardSlotWrappersTreeSet;
    
    private Boolean isScheduleState;
    private Boolean isEditState;
    
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
        this.selectedMedicalBoardSlotWrappersTreeSet = new TreeSet<>();
        Employee currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (currentEmployee != null && currentEmployee instanceof MedicalBoardAdmin) {
            currentMedicalBoardAdmin = (MedicalBoardAdmin) currentEmployee;
        }
        refreshMedicalBoardSlots();
    }
    
    private void refreshMedicalBoardSlots() {
        existingEventModel.clear();
        newEventModel.clear();
        
        List<MedicalBoardSlot> medicalBoardSlots = slotSessionBeanLocal.retrieveMedicalBoardSlots();
        
        int startHour = 24;
        int endHour = 0;
        
        for (MedicalBoardSlot mbs : medicalBoardSlots) {
            startHour = Math.min(startHour, mbs.getStartHour());
            endHour = Math.max(endHour, mbs.getEndHour());
            
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
        
        for (MedicalBoardSlotWrapper mbsWrapper : this.selectedMedicalBoardSlotWrappersTreeSet) {
            MedicalBoardSlot mbs = mbsWrapper.getMedicalBoardSlot();
            ScheduleEvent scheduleEvent = mbsWrapper.getScheduleEvent();
            
            String title;
            String styleClass = "selected-booking-slot";
            
            boolean isSameMedicalBoardSlot = mbs.equals(medicalBoardSlotToRemove);
            if (!isSameMedicalBoardSlot) {
                idx++;
            }
            
            if (mbs.getEndDateTime().after(new Date())) {
                title = "Available";

                // If the current iterating medical board slot is the same as the one to be removed, reset the colour.
                if (isSameMedicalBoardSlot) {
                    styleClass = "available-medical-board-slot";
                } else {
                    title += "[" + idx + "]";
                }
            } else {
                title = "Expired";

                // If the current iterating medical board slot is the same as the one to be removed, reset the colour.
                if (isSameMedicalBoardSlot) {
                    styleClass = "expired-medical-board-slot";
                } else {
                    title += "[" + idx + "]";
                }
            }
            
            mbsWrapper.setIndex(idx);
            
            existingEventModel.updateEvent(DefaultScheduleEvent.builder()
                    .id(scheduleEvent.getId())
                    .title(title)
                    .startDate(scheduleEvent.getStartDate())
                    .endDate(scheduleEvent.getEndDate())
                    .overlapAllowed(scheduleEvent.isOverlapAllowed())
                    .editable(scheduleEvent.isEditable())
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
                Optional<MedicalBoardSlotWrapper> optionalMbsWrapper = this.selectedMedicalBoardSlotWrappersTreeSet
                        .stream()
                        .filter(wrapper -> wrapper.getMedicalBoardSlot().equals(mbs))
                        .findFirst();
                
                if (optionalMbsWrapper.isPresent()) {
                    MedicalBoardSlotWrapper mbsWrapper = optionalMbsWrapper.get();

                    // To reset the colour of the event first before removing the mbs-event pair from the tree map.
                    updateSelectedMedicalBoardSlotsView(mbs);
                    this.selectedMedicalBoardSlotWrappersTreeSet.remove(mbsWrapper);
                    
                    if (mbsWrapper.getIsEditMode()) {
                        this.isEditState = Boolean.FALSE;
                    }
                    mbsWrapper.setIsEditMode(Boolean.FALSE);
                } else {
                    if (this.selectedMedicalBoardSlotWrappersTreeSet.size() >= 10) {
                        addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Too Many Medical Board Slots Selected", "Only a maximum of 10 medical board slots can be selected in one shot!"));
                    } else {
                        // To add the mbs-event pair to the tree map first before updating the colour of the event.
                        this.selectedMedicalBoardSlotWrappersTreeSet.add(new MedicalBoardSlotWrapper(mbs, event));
                        updateSelectedMedicalBoardSlotsView(null);
                    }
                }
            }
        } else {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Existing Medical Board Slot Selection Not Allowed", "You are not allowed to select existing medical board slots while scheduling new medical board slots!"));
        }
        
        event = new DefaultScheduleEvent();
    }
    
    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
        if (isScheduleState && event.getId() == null) {
            Boolean isValid = Boolean.TRUE;
            
            for (ScheduleEvent e : existingEventModel.getEvents()) {
                Date existingEventStartDate = Date.from(e.getStartDate().atZone(ZoneId.systemDefault()).toInstant());
                Date newEventStartDate = Date.from(selectEvent.getObject().atZone(ZoneId.systemDefault()).toInstant());
                
                if (isSameDay(existingEventStartDate, newEventStartDate)) {
                    addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid Medical Board Slots Selected", "Multiple medical boards are not allowed on the same day!"));
                    isValid = Boolean.FALSE;
                    break;
                }
            }
            
            if (isValid) {
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
        }
        
        event = new DefaultScheduleEvent();
    }
    
    public void onEventMove(ScheduleEntryMoveEvent event) {
        ScheduleEvent movedEvent = event.getScheduleEvent();
        Boolean isValid = Boolean.TRUE;

        // Moved delta duration
        Duration duration = event.getDeltaAsDuration();
        LocalDateTime originalStartDateTime = movedEvent.getStartDate().minus(duration);
        LocalDateTime originalEndDateTime = movedEvent.getEndDate().minus(duration);
        
        for (ScheduleEvent e : existingEventModel.getEvents()) {
            Date existingEventStartDate = Date.from(e.getStartDate().atZone(ZoneId.systemDefault()).toInstant());
            Date movedEventStartDate = Date.from(movedEvent.getStartDate().atZone(ZoneId.systemDefault()).toInstant());
            
            if (!e.equals(movedEvent) && isSameDay(existingEventStartDate, movedEventStartDate)) {
                addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid Medical Board Slots Moved", "Multiple medical boards are not allowed on the same day!"));
                isValid = Boolean.FALSE;
                break;
            }
        }

        // If the slot does not exist, but drag to invalid slots (past dates), revert.
        if (movedEvent.getStartDate().isBefore(LocalDateTime.now())) {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid Medical Board Slots Dragged", "Schedules cannot be made on past dates! Please select future dates for scheduling medical board slots!"));
            isValid = Boolean.FALSE;
        }
        
        if (!isValid) {
            movedEvent.setStartDate(originalStartDateTime);
            movedEvent.setEndDate(originalEndDateTime);
        }
    }
    
    public void saveSchedule() {
        if (!newEventModel.getEvents().isEmpty()) {
            newEventModel.getEvents().forEach(e -> {
                try {
                    Date startDate = Date.from(e.getStartDate().atZone(ZoneId.systemDefault()).toInstant());
                    Date endDate = Date.from(e.getEndDate().atZone(ZoneId.systemDefault()).toInstant());
                    slotSessionBeanLocal.createMedicalBoardSlot(startDate, endDate);
                    
                    addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Medical Board Slots Created", "Medical Board slots for medical board reviews have been created successfully!"));
                    
                    refreshMedicalBoardSlots();
                } catch (ScheduleMedicalBoardSlotException ex) {
                    addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Medical Board Slot Scheduler", ex.getMessage()));
                }
            });
        } else {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "No Medical Board Slot Scheduled", "Please schedule at least one medical board slot!"));
        }
    }
    
    public void saveEdit(MedicalBoardSlotWrapper medicalBoardSlotWrapper) {
        MedicalOfficer chairman = employeeSessionBeanLocal.retrieveMedicalOfficerById(medicalBoardSlotWrapper.getChairmanId());
        MedicalOfficer medicalOfficerOne = employeeSessionBeanLocal.retrieveMedicalOfficerById(medicalBoardSlotWrapper.getMedicalOfficerOneId());
        MedicalOfficer medicalOfficerTwo = employeeSessionBeanLocal.retrieveMedicalOfficerById(medicalBoardSlotWrapper.getMedicalOfficerTwoId());
        
        medicalBoardSlotWrapper.getMedicalBoardSlot().setChairman(chairman);
        medicalBoardSlotWrapper.getMedicalBoardSlot().setMedicalOfficerOne(medicalOfficerOne);
        medicalBoardSlotWrapper.getMedicalBoardSlot().setMedicalOfficerTwo(medicalOfficerTwo);
        
        try {
            slotSessionBeanLocal.updateMedicalBoardSlot(medicalBoardSlotWrapper.getMedicalBoardSlot());
        } catch (UpdateMedicalBoardSlotException ex) {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Medical Board Slot Scheduler", ex.getMessage()));
        }
    }
    
    public void reset() {
        refreshMedicalBoardSlots();
    }
    
    public String renderDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
    
    public String renderTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("kk:mm");
        return dateFormat.format(date);
    }
    
    public String renderDuration(Date start, Date end) {
        Long diffInMillies = Math.abs(end.getTime() - start.getTime());
        Long diffInMinutes = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
        
        Long hours = diffInMinutes / 60;
        Long minutes = diffInMinutes % 60;
        
        String duration = "";
        if (hours != 0) {
            duration += hours;
            if (hours == 1) {
                duration += " hour ";
            } else {
                duration += " hours ";
            }
        }
        
        if (minutes != 0) {
            duration += minutes;
            if (minutes == 1) {
                duration += " minute";
            } else {
                duration += " minutes";
            }
        }
        
        return duration;
    }
    
    public void closeSelectedMedicalBoardSlot(MedicalBoardSlotWrapper medicalBoardSlotWrapper) {
        updateSelectedMedicalBoardSlotsView(medicalBoardSlotWrapper.getMedicalBoardSlot());
        
        this.selectedMedicalBoardSlotWrappersTreeSet.remove(medicalBoardSlotWrapper);
        
        if (medicalBoardSlotWrapper.getIsEditMode()) {
            this.isEditState = Boolean.FALSE;
        }
        medicalBoardSlotWrapper.setIsEditMode(Boolean.FALSE);
    }
    
    public void deleteSelectedMedicalBoardSlot(MedicalBoardSlotWrapper medicalBoardSlotWrapper) {
        try {
            slotSessionBeanLocal.removeMedicalBoardSlot(medicalBoardSlotWrapper.getMedicalBoardSlot().getSlotId());
            
            updateSelectedMedicalBoardSlotsView(medicalBoardSlotWrapper.getMedicalBoardSlot());

            // Remove the selected event from the schedule component
            this.existingEventModel.deleteEvent(medicalBoardSlotWrapper.getScheduleEvent());

            // Remove the selected wrapper from the tree set
            this.selectedMedicalBoardSlotWrappersTreeSet.remove(medicalBoardSlotWrapper);
            
            if (medicalBoardSlotWrapper.getIsEditMode()) {
                this.isEditState = Boolean.FALSE;
            }
            medicalBoardSlotWrapper.setIsEditMode(Boolean.FALSE);
        } catch (RemoveSlotException ex) {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Unable to Delete Medical Board Slot", ex.getMessage()));
        }
    }
    
    public boolean isSameDay(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
                && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
    }
    
    public boolean beforeNow(Date date) {
        return date.before(new Date());
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
    
    public Set<MedicalBoardSlotWrapper> getSelectedMedicalBoardSlotWrappersTreeSet() {
        return selectedMedicalBoardSlotWrappersTreeSet;
    }
    
    public Boolean getIsScheduleState() {
        return isScheduleState;
    }
    
    public void setIsScheduleState(Boolean isScheduleState) {
        this.isScheduleState = isScheduleState;
    }
    
    public Boolean getIsEditState() {
        return isEditState;
    }
    
    public void setIsEditState(Boolean isEditState) {
        this.isEditState = isEditState;
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
