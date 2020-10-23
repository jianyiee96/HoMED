package jsf.managedBean;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import util.enumeration.BookingStatusEnum;
import util.exceptions.RemoveSlotException;
import util.exceptions.ScheduleBookingSlotException;

@Named(value = "bookingSlotManagementManagedBean")
@ViewScoped
public class BookingSlotManagementManagedBean implements Serializable {

    @EJB(name = "SlotSessionBeanLocal")
    private SlotSessionBeanLocal slotSessionBeanLocal;

    private MedicalStaff currentMedicalStaff;
    private MedicalCentre selectedMedicalCentre;

    private List<BookingSlot> bookingSlots;

    private Map<BookingSlot, ScheduleEvent> selectedBookingSlotsTreeMap;
    private Set<BookingSlot> selectedBookingSlotsTreeSet;
    private HashMap<BookingSlot, Integer> selectedBookingSlotsMapping;

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
        this.selectedBookingSlotsTreeMap = new TreeMap<>();
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
            selectedBookingSlotsMapping = new HashMap<>();
            bookingSlots = slotSessionBeanLocal.retrieveBookingSlotsByMedicalCentre(selectedMedicalCentre.getMedicalCentreId());

            int startHour = 24;
            int endHour = 0;

            for (BookingSlot bs : bookingSlots) {

                startHour = Math.min(startHour, bs.getStartHour());
                endHour = Math.max(endHour, bs.getEndHour());

                if (bs.getBooking() != null && bs.getBooking().getBookingStatusEnum() == BookingStatusEnum.UPCOMING) {
                    existingEventModel.addEvent(DefaultScheduleEvent.builder()
                            .title("Upcoming")
                            .startDate(bs.getStartDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                            .endDate(bs.getEndDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                            .overlapAllowed(false)
                            .editable(false)
                            .styleClass("booked-booking-slot")
                            .data(bs)
                            .build());
                } else if (bs.getBooking() != null && bs.getBooking().getBookingStatusEnum() == BookingStatusEnum.ABSENT) {
                    existingEventModel.addEvent(DefaultScheduleEvent.builder()
                            .title("Absent")
                            .startDate(bs.getStartDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                            .endDate(bs.getEndDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                            .overlapAllowed(false)
                            .editable(false)
                            .styleClass("absent-booking-slot")
                            .data(bs)
                            .build());
                } else if (bs.getBooking() != null && bs.getBooking().getBookingStatusEnum() == BookingStatusEnum.PAST) {
                    existingEventModel.addEvent(DefaultScheduleEvent.builder()
                            .title("Past Booking")
                            .startDate(bs.getStartDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                            .endDate(bs.getEndDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                            .overlapAllowed(false)
                            .editable(false)
                            .styleClass("past-booking-slot")
                            .data(bs)
                            .build());
                } else if (bs.getBooking() == null) {

                    if (bs.getStartDateTime().after(new Date())) {
                        existingEventModel.addEvent(DefaultScheduleEvent.builder()
                                .title("Available")
                                .startDate(bs.getStartDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                                .endDate(bs.getEndDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                                .overlapAllowed(false)
                                .editable(false)
                                .styleClass("available-booking-slot")
                                .data(bs)
                                .build());
                    } else {
                        existingEventModel.addEvent(DefaultScheduleEvent.builder()
                                .title("Expired")
                                .startDate(bs.getStartDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                                .endDate(bs.getEndDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
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

        }
    }

    private void updateSelectedBookingSlotsView(BookingSlot bookingSlotToRemove) {
        int idx = 0;
        for (Map.Entry<BookingSlot, ScheduleEvent> entrySet : selectedBookingSlotsTreeMap.entrySet()) {
            BookingSlot bs = entrySet.getKey();
            ScheduleEvent event = entrySet.getValue();

            idx++;
            String title = "";
            String styleClass = "selected-booking-slot";
            boolean isSameBookingSlot = bs.equals(bookingSlotToRemove);
            if (isSameBookingSlot) {
                idx--;
            }

            if (bs.getBooking() != null && bs.getBooking().getBookingStatusEnum() == BookingStatusEnum.UPCOMING) {
                title = "Upcoming";
                if (isSameBookingSlot) {
                    styleClass = "booked-booking-slot";
                } else {
                    title += "[" + idx + "]";
                }
            } else if (bs.getBooking() != null && bs.getBooking().getBookingStatusEnum() == BookingStatusEnum.ABSENT) {
                title = "Absent";
                if (isSameBookingSlot) {
                    styleClass = "absent-booking-slot";
                } else {
                    title += "[" + idx + "]";
                }
            } else if (bs.getBooking() != null && bs.getBooking().getBookingStatusEnum() == BookingStatusEnum.PAST) {
                title = "Past Booking";
                if (isSameBookingSlot) {
                    styleClass = "past-booking-slot";
                } else {
                    title += "[" + idx + "]";
                }
            } else if (bs.getBooking() == null) {
                if (bs.getStartDateTime().after(new Date())) {
                    title = "Available";
                    if (isSameBookingSlot) {
                        styleClass = "available-booking-slot";
                    } else {
                        title += "[" + idx + "]";
                    }
                } else {
                    title = "Expired";
                    if (isSameBookingSlot) {
                        styleClass = "expired-booking-slot";
                    } else {
                        title += "[" + idx + "]";
                    }
                }
            }

            selectedBookingSlotsMapping.put(bs, idx);

            existingEventModel.updateEvent(DefaultScheduleEvent.builder()
                    .id(event.getId())
                    .title(title)
                    .startDate(event.getStartDate())
                    .endDate(event.getEndDate())
                    .overlapAllowed(event.isOverlapAllowed())
                    .editable(event.isEditable())
                    .styleClass(styleClass)
                    .data(bs)
                    .build());
        }
    }

    public void onEventSelect(SelectEvent<ScheduleEvent> selectEvent) {
        if (!isScheduleState) {
            event = selectEvent.getObject();

            if (event.getData() != null) {
                BookingSlot bs = (BookingSlot) event.getData();

                if (!this.selectedBookingSlotsTreeMap.containsKey(bs)) {
                    if (this.selectedBookingSlotsTreeMap.size() >= 10) {
                        addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Too Many Booking Slots Selected", "Only a maximum of 10 medical board slots can be selected in one shot!"));
                    } else {
                        this.selectedBookingSlotsTreeMap.put(bs, event);
                        updateSelectedBookingSlotsView(null);
                    }
                } else {
                    updateSelectedBookingSlotsView(bs);
                    this.selectedBookingSlotsTreeMap.remove(bs);
                }
            }
        } else {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Existing Booking Slot Selection Not Allowed", "You are not allowed to select existing booking slots while scheduling new booking slots!"));
        }

        event = new DefaultScheduleEvent();
    }

    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
        if (isScheduleState && event.getId() == null) {
            if (selectEvent.getObject().isAfter(LocalDateTime.now())) {
                event = DefaultScheduleEvent.builder()
                        .title("New")
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

    public void onEventMove(ScheduleEntryMoveEvent event) {
        ScheduleEvent updatedEvent = event.getScheduleEvent();

        // Moved delta duration
        Duration duration = event.getDeltaAsDuration();
        LocalDateTime originalStartDateTime = event.getScheduleEvent().getStartDate().minus(duration);
        LocalDateTime originalEndDateTime = event.getScheduleEvent().getEndDate().minus(duration);

        // If the slot does not exist, but drag to invalid slots (past dates), revert.
        if (event.getScheduleEvent().getStartDate().isBefore(LocalDateTime.now())) {
            updatedEvent.setStartDate(originalStartDateTime);
            updatedEvent.setEndDate(originalEndDateTime);

            addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid Booking Slots Dragged", "Schedules cannot be made on past dates! Please select future dates for scheduling booking slots!"));
        }
    }

    public void saveSchedule() {
        if (!newEventModel.getEvents().isEmpty()) {
            newEventModel.getEvents().forEach(e -> {
                try {
                    Date rangeStart = Date.from(e.getStartDate().atZone(ZoneId.systemDefault()).toInstant());
                    Date rangeEnd = Date.from(e.getEndDate().atZone(ZoneId.systemDefault()).toInstant());
                    slotSessionBeanLocal.createBookingSlots(selectedMedicalCentre.getMedicalCentreId(), rangeStart, rangeEnd);
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

    public String renderDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        return dateFormat.format(date);
    }

    public void closeBookingSlot(BookingSlot bookingSlot) {
        updateSelectedBookingSlotsView(bookingSlot);
        this.selectedBookingSlotsTreeMap.remove(bookingSlot);
    }

    public void deleteBookingSlot(BookingSlot bookingSlot) {
        try {
            slotSessionBeanLocal.removeBookingSlot(bookingSlot.getSlotId());

            updateSelectedBookingSlotsView(bookingSlot);
            this.existingEventModel.deleteEvent(this.selectedBookingSlotsTreeMap.get(bookingSlot));

            this.selectedBookingSlotsTreeMap.remove(bookingSlot);
            this.selectedBookingSlotsTreeSet.remove(bookingSlot);
        } catch (RemoveSlotException ex) {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Unable to Delete Booking Slot", ex.getMessage()));
        }
    }

    public void deleteAllSelectedBookingSlots() {
        List<BookingSlot> bsToBeRemoved = new ArrayList<>();

        this.selectedBookingSlotsTreeSet.forEach(bs -> {
            if (bs.getBooking() == null && !beforeNow(bs.getStartDateTime())) {
                try {
                    slotSessionBeanLocal.removeBookingSlot(bs.getSlotId());
                    bsToBeRemoved.add(bs);

                } catch (RemoveSlotException ex) {
                    addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Unable to Delete Booking Slot", ex.getMessage()));
                }
            }
        });

        bsToBeRemoved.forEach(bs -> {
            updateSelectedBookingSlotsView(bs);
            this.existingEventModel.deleteEvent(this.selectedBookingSlotsTreeMap.get(bs));

            this.selectedBookingSlotsTreeMap.remove(bs);
            this.selectedBookingSlotsTreeSet.remove(bs);
        });
    }

    public boolean beforeNow(Date date) {
        return date.before(new Date());
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage("growl-message", message);
    }

    public String getBookingSlotStatus(BookingSlot bookingSlot) {
        if (bookingSlot.getBooking() != null) {
            return "BOOKED";
        } else {
            if (bookingSlot.getStartDateTime().after(new Date())) {
                return "AVAILABLE";
            } else {
                return "EXPIRED";
            }
        }
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

    public Set<BookingSlot> getSelectedBookingSlotsTreeSet() {
        selectedBookingSlotsTreeSet = new TreeSet<>();

        selectedBookingSlotsTreeMap.entrySet().forEach(entrySet -> {
            selectedBookingSlotsTreeSet.add(entrySet.getKey());
        });

        return selectedBookingSlotsTreeSet;
    }

    public Map<BookingSlot, ScheduleEvent> getSelectedBookingSlotsTreeMap() {
        return selectedBookingSlotsTreeMap;
    }

    public HashMap<BookingSlot, Integer> getSelectedBookingSlotsMapping() {
        return selectedBookingSlotsMapping;
    }

    public void setSelectedBookingSlotsMapping(HashMap<BookingSlot, Integer> selectedBookingSlotsMapping) {
        this.selectedBookingSlotsMapping = selectedBookingSlotsMapping;
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
