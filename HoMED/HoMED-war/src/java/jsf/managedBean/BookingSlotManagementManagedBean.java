package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.SlotSessionBeanLocal;
import entity.BookingSlot;
import entity.Employee;
import entity.MedicalCentre;
import entity.MedicalStaff;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
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
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.RemoveSlotException;
import util.exceptions.ScheduleBookingSlotException;

@Named(value = "bookingSlotManagementManagedBean")
@ViewScoped
public class BookingSlotManagementManagedBean implements Serializable {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @EJB(name = "SlotSessionBeanLocal")
    private SlotSessionBeanLocal slotSessionBeanLocal;

    private MedicalStaff currentMedicalStaff;

    private MedicalCentre selectedMedicalCentre;

    private List<BookingSlot> bookingSlots;

    private TreeSet<BookingSlot> selectedBookingSlots;

    private Boolean isScheduleState;

    private ScheduleModel existingEventModel;
    private ScheduleModel newEventModel;
    private ScheduleEvent event;

    private String slotLabelInterval = "01:00";
    private String minTime = "00:00:00";
    private String maxTime = "24:00:00";

    public BookingSlotManagementManagedBean() {
        this.isScheduleState = Boolean.FALSE;
        this.existingEventModel = new DefaultScheduleModel();
        this.newEventModel = new DefaultScheduleModel();
        this.event = new DefaultScheduleEvent();
    }

    @PostConstruct
    public void postConstruct() {
        this.selectedBookingSlots = new TreeSet<>();
        Employee currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        if (currentEmployee != null && currentEmployee instanceof MedicalStaff) {
            currentMedicalStaff = (MedicalStaff) currentEmployee;
            selectedMedicalCentre = currentMedicalStaff.getMedicalCentre();
        }
        refreshBookingSlots();
    }

    private void refreshBookingSlots() {
        existingEventModel.clear();
        newEventModel.clear();

        if (selectedMedicalCentre != null) {
            Date now = new Date();

            bookingSlots = slotSessionBeanLocal.retrieveBookingSlotsByMedicalCentre(selectedMedicalCentre.getMedicalCentreId());

            int startHour = 24;
            int endHour = 0;

            for (BookingSlot bs : bookingSlots) {

                startHour = Math.min(startHour, bs.getStartHour());
                endHour = Math.max(endHour, bs.getEndHour());

                if (bs.getBooking() != null && bs.getBooking().getBookingStatusEnum() == BookingStatusEnum.UPCOMING) {
                    existingEventModel.addEvent(DefaultScheduleEvent.builder()
                            .title("Upcoming Booking")
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
                            .editable(false)
                            .styleClass("booked-booking-slot")
                            .data(bs)
                            .build());
                } else if (bs.getBooking() != null && bs.getBooking().getBookingStatusEnum() != BookingStatusEnum.CANCELLED) {
                    existingEventModel.addEvent(DefaultScheduleEvent.builder()
                            .title("Past Booking")
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
                            .editable(false)
                            .styleClass("past-booking-slot")
                            .data(bs)
                            .build());
                } else if (bs.getBooking() == null) {

                    if (bs.getEndDateTime().after(now)) {
                        existingEventModel.addEvent(DefaultScheduleEvent.builder()
                                .title("Available")
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
                                .editable(false)
                                .styleClass("booking-slot")
                                .data(bs)
                                .build());

                    } else {
                        existingEventModel.addEvent(DefaultScheduleEvent.builder()
                                .title("Expired")
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
                                .editable(false)
                                .styleClass("expired-booking-slot")
                                .data(bs)
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

        } else {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Not Link to Medical Centre", "You are currently not assigned to any medical centre!"));
            PrimeFaces.current().ajax().update(":growl-message");
        }
    }

    public void onEventSelect(SelectEvent<ScheduleEvent> selectEvent) {
        event = selectEvent.getObject();

        if (event.getData() != null) {
            BookingSlot bs = (BookingSlot) event.getData();
            if (!this.selectedBookingSlots.contains(bs)) {
                this.selectedBookingSlots.add(bs);
            }
        }

    }

    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
        System.out.println("dateSelected");

        if (isScheduleState && event.getId() == null) {
            if (selectEvent.getObject().isAfter(LocalDateTime.now())) {
                event = DefaultScheduleEvent.builder()
                        .title("New Booking Slots")
                        .startDate(selectEvent.getObject())
                        .endDate(selectEvent.getObject().plusMinutes(15))
                        .overlapAllowed(false)
                        .styleClass("new-booking-slot")
                        .build();

                existingEventModel.addEvent(event);
                newEventModel.addEvent(event);
            } else {
                addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid Booking Slots Selected", "Schedules cannot be made on past dates! Please select future dates for scheduling booking slots!"));
            }
        }

        event = new DefaultScheduleEvent();
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
                    slotSessionBeanLocal.createBookingSlots(1L, rangeStart, rangeEnd);
                } catch (ScheduleBookingSlotException ex) {
                    addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Scheduler", ex.getMessage()));
                }
            });
            addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Booking Slots Created", "Booking slots for consultations have been created successfully!"));
        }

        refreshBookingSlots();

    }

    public void reset() {
        refreshBookingSlots();
    }

    public void closeBookingSlot(ActionEvent event) {
        BookingSlot bookingSlot = (BookingSlot) event.getComponent().getAttributes().get("bookingSlot");
        this.selectedBookingSlots.remove(bookingSlot);
    }

    public void deleteBookingSlot(ActionEvent event) {
        BookingSlot bookingSlot = (BookingSlot) event.getComponent().getAttributes().get("bookingSlot");
        this.selectedBookingSlots.remove(bookingSlot);
        try {
            slotSessionBeanLocal.removeBookingSlot(bookingSlot.getSlotId());
            refreshBookingSlots();
        } catch (RemoveSlotException ex) {
            System.out.println("Unable to remove booking slot: " + ex.getMessage());
        }

    }

    public void deleteAllSelectedBookingSlots() {

        List<BookingSlot> removedBs = new ArrayList<>();
        
        
        this.selectedBookingSlots.forEach(bs -> {
            if (bs.getBooking() == null) {
                try {
                    slotSessionBeanLocal.removeBookingSlot(bs.getSlotId());
                    removedBs.add(bs);
                } catch (RemoveSlotException ex) {
                    System.out.println("Unable to remove booking slot: " + ex.getMessage());
                }

            }
        });
        
        removedBs.forEach(bs -> {
            this.selectedBookingSlots.remove(bs);
        });
        
        refreshBookingSlots();

    }

    public String renderDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        return dateFormat.format(date);
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
            addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Not In Schedule Mode", "Please enter schedule mode to schedule booking slots!"));

        } else {

            boolean error = false;

            for (BookingSlot bs : bookingSlots) {

                // If the slot currently exists in the database, disable dragging.
                if (bs.getStartDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().isEqual(originalStartDateTime)
                        && bs.getEndDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().isEqual(originalEndDateTime)) {

                    revertEventDateTime(event.getScheduleEvent(), originalStartDateTime, originalEndDateTime);
                    addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Booking Slots Updating Prohibited", "Updating of existing booking slots is not allowed!"));
                    error = true;
                    break;

                }
            }

            // If the slot does not exist, but drag to invalid slots (past dates), revert.
            if (!error && event.getScheduleEvent().getStartDate().isBefore(LocalDateTime.now())) {

                revertEventDateTime(event.getScheduleEvent(), originalStartDateTime, originalEndDateTime);
                addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid Booking Slots Dragged", "Schedules cannot be made on past dates! Please select future dates for scheduling booking slots!"));

            }
        }
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        System.out.println("eventResized");

        // Moved delta duration
        Duration startDurationDelta = event.getDeltaStartAsDuration();
        Duration endDurationDelta = event.getDeltaEndAsDuration();

        LocalDateTime originalStartDateTime = event.getScheduleEvent().getStartDate().minus(startDurationDelta);
        LocalDateTime originalEndDateTime = event.getScheduleEvent().getEndDate().minus(endDurationDelta);

        // If not in schedule state, disable resizing.
        if (!isScheduleState) {

            revertEventDateTime(event.getScheduleEvent(), originalStartDateTime, originalEndDateTime);
            addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Not In Schedule Mode", "Please enter schedule mode to schedule booking slots!"));

        } else {

            for (BookingSlot bs : bookingSlots) {

                // If the slot currently exists in the database, disable resizing.
                if (bs.getStartDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().isEqual(originalStartDateTime)
                        && bs.getEndDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().isEqual(originalEndDateTime)) {

                    revertEventDateTime(event.getScheduleEvent(), originalStartDateTime, originalEndDateTime);
                    addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Booking Slots Updating Prohibited", "Updating of existing booking slots is not allowed!"));
                    break;

                }
            }
        }
    }

    private void revertEventDateTime(ScheduleEvent existingEvent, LocalDateTime originalStartDateTime, LocalDateTime originalEndDateTime) {
        existingEvent.setStartDate(originalStartDateTime);
        existingEvent.setEndDate(originalEndDateTime);
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage("growl-message", message);
    }

    public MedicalStaff getCurrentMedicalStaff() {
        return currentMedicalStaff;
    }

    public void setCurrentMedicalStaff(MedicalStaff currentMedicalStaff) {
        this.currentMedicalStaff = currentMedicalStaff;
    }

    public MedicalCentre getSelectedMedicalCentre() {
        return selectedMedicalCentre;
    }

    public void setSelectedMedicalCentre(MedicalCentre selectedMedicalCentre) {
        this.selectedMedicalCentre = selectedMedicalCentre;
    }

    public Boolean getIsScheduleState() {
        return isScheduleState;
    }

    public void setIsScheduleState(Boolean isScheduleState) {
        this.isScheduleState = isScheduleState;
    }

    public List<BookingSlot> getBookingSlots() {
        return bookingSlots;
    }

    public void setBookingSlots(List<BookingSlot> bookingSlots) {
        this.bookingSlots = bookingSlots;
    }

    public TreeSet<BookingSlot> getSelectedBookingSlots() {
        return selectedBookingSlots;
    }

    public void setSelectedBookingSlots(TreeSet<BookingSlot> selectedBookingSlots) {
        this.selectedBookingSlots = selectedBookingSlots;
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