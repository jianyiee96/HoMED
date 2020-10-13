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
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import util.enumeration.BookingStatusEnum;

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

}
