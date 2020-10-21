/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.singleton;

import ejb.session.stateless.MedicalCentreSessionBeanLocal;
import ejb.session.stateless.SlotSessionBeanLocal;
import entity.MedicalCentre;
import entity.OperatingHours;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import javax.ejb.TimerService;
import util.enumeration.DayOfWeekEnum;
import util.exceptions.ScheduleBookingSlotException;

@Singleton
@LocalBean
@Startup
public class BookingSlotScheduler {

    @EJB(name = "MedicalCentreSessionBeanLocal")
    private MedicalCentreSessionBeanLocal medicalCentreSessionBeanLocal;
    @EJB(name = "SlotSessionBeanLocal")
    private SlotSessionBeanLocal slotSessionBeanLocal;

    @Resource
    private TimerService timerService;

//    @Schedule(hour = "*", minute = "*", second = "*/10", info = "Schedule Booking Slots to be triggered during")
    @Schedule(dayOfWeek = "Mon", hour = "0", minute = "0", second = "0", info = "Schedule Booking Slots every Monday 12 a.m.")
    private void startBookingSlotScheduler() {
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timeStamp = df.format(now);

        System.out.println("============================================================================");
        System.out.println("- SlotScheduler: Timeout at " + timeStamp);

        List<MedicalCentre> medicalCentres = medicalCentreSessionBeanLocal.retrieveAllMedicalCentres();

        int numOfDaysToCreate = 7;

        for (MedicalCentre mc : medicalCentres) {
            System.out.println("Creating Booking Slots for " + mc.getName() + "...");
            List<OperatingHours> operatingHours = mc.getOperatingHours();

            try {
                for (int day = 0; day < numOfDaysToCreate; day++) {

                    Calendar date = Calendar.getInstance();
                    date.add(Calendar.DATE, 28);
                    date.set(Calendar.SECOND, 0);
                    date.set(Calendar.MILLISECOND, 0);
                    date.add(Calendar.DATE, day);

                    int dayIdx = date.get(Calendar.DAY_OF_WEEK);
                    DayOfWeekEnum dayOfWeekEnum = getDayOfWeekEnum(dayIdx);

                    OperatingHours daysOh = operatingHours.stream()
                            .filter(oh -> oh.getDayOfWeek() == dayOfWeekEnum)
                            .findFirst()
                            .get();

                    if (!daysOh.getIsOpen()) {
                        continue;
                    }

                    Calendar start = new GregorianCalendar();
                    Calendar end = new GregorianCalendar();
                    start.setTime(date.getTime());
                    end.setTime(date.getTime());

                    start.set(Calendar.HOUR_OF_DAY, daysOh.getOpeningHours().getHour());
                    start.set(Calendar.MINUTE, daysOh.getOpeningHours().getMinute());
                    end.set(Calendar.HOUR_OF_DAY, daysOh.getClosingHours().getHour());
                    end.set(Calendar.MINUTE, daysOh.getClosingHours().getMinute());

                    if (start.getTime().before(end.getTime())) {
                        slotSessionBeanLocal.createBookingSlots(mc.getMedicalCentreId(), start.getTime(), end.getTime());
                    }
                }

                System.out.println("Successfully created Booking Slots for " + mc.getName() + "...");

            } catch (ScheduleBookingSlotException ex) {
                System.out.println("Unable to schedule booking slots: " + ex.getMessage());
            }
        }

        System.out.println("============================================================================");
    }

    private DayOfWeekEnum getDayOfWeekEnum(int day) {
        switch (day) {
            case 1:
                return DayOfWeekEnum.SUNDAY;
            case 2:
                return DayOfWeekEnum.MONDAY;
            case 3:
                return DayOfWeekEnum.TUESDAY;
            case 4:
                return DayOfWeekEnum.WEDNESDAY;
            case 5:
                return DayOfWeekEnum.THURSDAY;
            case 6:
                return DayOfWeekEnum.FRIDAY;
            case 7:
                return DayOfWeekEnum.SATURDAY;
        }
        return DayOfWeekEnum.MONDAY;
    }
}
