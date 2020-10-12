package jsf.managedBean;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

@Named(value = "schedulerManagedBean")
@ViewScoped
public class SchedulerManagedBean implements Serializable {

    private Boolean isCreateState;

    private ScheduleModel eventModel;
    private ScheduleEvent event;

    private String slotLabelInterval = "01:00";
    private String scrollTime = "06:00:00";
    private String minTime = "04:00:00";
    private String maxTime = "20:00:00";
    private String locale = "en";
    private String timeZone = "";
    private String clientTimeZone = "local";

    public SchedulerManagedBean() {
        this.isCreateState = Boolean.FALSE;
        this.eventModel = new DefaultScheduleModel();
        this.event = new DefaultScheduleEvent();
    }

    @PostConstruct
    public void postConstruct() {
        DefaultScheduleEvent event = DefaultScheduleEvent.builder()
                .title("Champions League Match")
                .startDate(previousDay8Pm())
                .endDate(previousDay11Pm())
                .description("Team A vs. Team B")
                .build();
        eventModel.addEvent(event);

        event = DefaultScheduleEvent.builder()
                .title("Birthday Party")
                .startDate(today1Pm())
                .endDate(today6Pm())
                .description("Aragon")
                .overlapAllowed(true)
                .build();
        eventModel.addEvent(event);

        event = DefaultScheduleEvent.builder()
                .title("Breakfast at Tiffanys")
                .startDate(nextDay9Am())
                .endDate(nextDay11Am())
                .description("all you can eat")
                .overlapAllowed(true)
                .build();
        eventModel.addEvent(event);

        event = DefaultScheduleEvent.builder()
                .title("Plant the new garden stuff")
                .startDate(theDayAfter3Pm())
                .endDate(fourDaysLater3pm())
                .description("Trees, flowers, ...")
                .build();
        eventModel.addEvent(event);

        DefaultScheduleEvent scheduleEventAllDay = DefaultScheduleEvent.builder()
                .title("Holidays (AllDay)")
                .startDate(sevenDaysLater0am())
                .endDate(eightDaysLater0am())
                .description("sleep as long as you want")
                .allDay(true)
                .build();
        eventModel.addEvent(scheduleEventAllDay);
    }

    public Boolean getIsCreateState() {
        return isCreateState;
    }

    public void setIsCreateState(Boolean isCreateState) {
        this.isCreateState = isCreateState;
    }

    public LocalDateTime getRandomDateTime(LocalDateTime base) {
        LocalDateTime dateTime = base.withMinute(0).withSecond(0).withNano(0);
        return dateTime.plusDays(((int) (Math.random() * 30)));
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    private LocalDateTime previousDay8Pm() {
        return LocalDateTime.now().minusDays(1).withHour(20).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime previousDay11Pm() {
        return LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime today1Pm() {
        return LocalDateTime.now().withHour(13).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime theDayAfter3Pm() {
        return LocalDateTime.now().plusDays(1).withHour(15).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime today6Pm() {
        return LocalDateTime.now().withHour(18).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime nextDay9Am() {
        return LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime nextDay11Am() {
        return LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime fourDaysLater3pm() {
        return LocalDateTime.now().plusDays(4).withHour(15).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime sevenDaysLater0am() {
        return LocalDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime eightDaysLater0am() {
        return LocalDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public LocalDate getInitialDate() {
        return LocalDate.now().plusDays(1);
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public void addEvent() {
        if (event.isAllDay()) {
            //see https://github.com/primefaces/primefaces/issues/1164
            if (event.getStartDate().toLocalDate().equals(event.getEndDate().toLocalDate())) {
                event.setEndDate(event.getEndDate().plusDays(1));
            }
        }

        if (event.getId() == null) {
            eventModel.addEvent(event);
            System.out.println("event added");
            System.out.println("start date: " + event.getStartDate());
            System.out.println("  end date: " + event.getEndDate());

            eventModel.getEvents().forEach(e -> System.out.println(e));
        } else {
            eventModel.updateEvent(event);
        }

//        event = new DefaultScheduleEvent();
    }

    public void onEventSelect(SelectEvent<ScheduleEvent> selectEvent) {
        event = selectEvent.getObject();
    }

    public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {

        if (event.getId() == null) {
            event = DefaultScheduleEvent.builder()
                    .title("Scheduled Slot")
                    .startDate(selectEvent.getObject())
                    .endDate(selectEvent.getObject().plusMinutes(15))
                    .overlapAllowed(false)
                    .build();
            
            eventModel.addEvent(event);
        }
        
        event = new DefaultScheduleEvent();
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Delta:" + event.getDeltaAsDuration());

        addMessage(message);
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Start-Delta:" + event.getDeltaStartAsDuration() + ", End-Delta: " + event.getDeltaEndAsDuration());

        addMessage(message);
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public String getSlotLabelInterval() {
        return slotLabelInterval;
    }

    public void setSlotLabelInterval(String slotLabelInterval) {
        this.slotLabelInterval = slotLabelInterval;
    }

    public String getScrollTime() {
        return scrollTime;
    }

    public void setScrollTime(String scrollTime) {
        this.scrollTime = scrollTime;
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

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getClientTimeZone() {
        return clientTimeZone;
    }

    public void setClientTimeZone(String clientTimeZone) {
        this.clientTimeZone = clientTimeZone;
    }

}
