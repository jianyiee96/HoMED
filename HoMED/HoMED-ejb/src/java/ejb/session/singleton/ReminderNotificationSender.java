/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.singleton;

import ejb.session.stateless.BookingSessionBeanLocal;
import ejb.session.stateless.MedicalBoardCaseSessionBeanLocal;
import ejb.session.stateless.NotificationSessionBeanLocal;
import entity.Booking;
import entity.MedicalBoardCase;
import entity.Notification;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import javax.ejb.TimerService;
import util.enumeration.NotificationTypeEnum;
import util.exceptions.CreateNotificationException;

@Singleton
@LocalBean
@Startup
@DependsOn("DataInitializationSessionBean")
public class ReminderNotificationSender {

    @EJB
    BookingSessionBeanLocal bookingSessionBeanLocal;

    @EJB
    NotificationSessionBeanLocal notificationSessionBeanLocal;

    @EJB
    MedicalBoardCaseSessionBeanLocal medicalBoardCaseSessionBeanLocal;

    @Resource
    private TimerService timerService;

    @PostConstruct
    public void initialTrigger() {
        sendDailyNotifications();
    }

    @Schedule(hour = "0", info = "sendDailyNotifications")
    public void sendDailyNotifications() {
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        String timeStamp = df.format(now);

        System.out.println("============================================================================");
        System.out.println("- Daily Notification Sender: Timeout at " + timeStamp);

        Calendar c = Calendar.getInstance();
        c.setTime(now);
//        c.add(Calendar.DATE, 1);

        System.out.println("- Daily Notification Sender: Sending Notifications for upcoming bookings");

        List<Booking> upcomingBookings = bookingSessionBeanLocal.retrieveAllUpcomingBookings();

        for (Booking b : upcomingBookings) {

            Calendar c2 = Calendar.getInstance();
            c2.setTime(b.getBookingSlot().getStartDateTime());

            boolean isSameDay = c.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
                    && c.get(Calendar.YEAR) == c2.get(Calendar.YEAR);

            c2.add(Calendar.DAY_OF_YEAR, -3);

            boolean isThreeDayLater = c.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
                    && c.get(Calendar.YEAR) == c2.get(Calendar.YEAR);

            c2.add(Calendar.DAY_OF_YEAR, -4);

            boolean isWeekLater = c.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
                    && c.get(Calendar.YEAR) == c2.get(Calendar.YEAR);

            if (isSameDay) {

                Notification n = new Notification("Upcoming Booking Reminder", "Please be reminded that you have an upcoming booking today at " + df2.format(b.getBookingSlot().getStartDateTime()) + ". Please remember to submit linked forms (if any) before heading down to the medical centre.", NotificationTypeEnum.BOOKING, b.getBookingId());
                try {
                    notificationSessionBeanLocal.createNewNotification(n, b.getServiceman().getServicemanId(), Boolean.FALSE);
                    notificationSessionBeanLocal.sendPushNotification("Upcoming Booking Reminder", "Please be reminded that you have an upcoming booking today at " + df2.format(b.getBookingSlot().getStartDateTime()) + ". Please remember to submit linked forms (if any) before heading down to the medical centre.", b.getServiceman().getFcmToken());

                } catch (CreateNotificationException ex) {
                    System.out.println("Error in creating notification " + ex);
                }

            }

            if (isThreeDayLater) {

                Notification n = new Notification("Upcoming Booking Reminder", "Please be reminded that you have an upcoming booking 3 days later on " + df2.format(b.getBookingSlot().getStartDateTime()) + ". Please remember to submit linked forms (if any) before heading down to the medical centre.", NotificationTypeEnum.BOOKING, b.getBookingId());
                try {
                    notificationSessionBeanLocal.createNewNotification(n, b.getServiceman().getServicemanId(), Boolean.FALSE);
                    notificationSessionBeanLocal.sendPushNotification("Upcoming Booking Reminder", "Please be reminded that you have an upcoming booking 3 days later on " + df2.format(b.getBookingSlot().getStartDateTime()) + ". Please remember to submit linked forms (if any) before heading down to the medical centre.", b.getServiceman().getFcmToken());

                } catch (CreateNotificationException ex) {
                    System.out.println("Error in creating notification " + ex);
                }
            }

            if (isWeekLater) {

                Notification n = new Notification("Upcoming Booking Reminder", "Please be reminded that you have an upcoming booking next week on " + df2.format(b.getBookingSlot().getStartDateTime()) + ". Please remember to submit linked forms (if any) before heading down to the medical centre.", NotificationTypeEnum.BOOKING, b.getBookingId());
                try {
                    notificationSessionBeanLocal.createNewNotification(n, b.getServiceman().getServicemanId(), Boolean.FALSE);
                    notificationSessionBeanLocal.sendPushNotification("Upcoming Booking Reminder", "Please be reminded that you have an upcoming next week on " + df2.format(b.getBookingSlot().getStartDateTime()) + ". Please remember to submit linked forms (if any) before heading down to the medical centre.", b.getServiceman().getFcmToken());

                } catch (CreateNotificationException ex) {
                    System.out.println("Error in creating notification " + ex);
                }

            }

        }

        List<MedicalBoardCase> boardInPresenceCases = medicalBoardCaseSessionBeanLocal.retrieveAllMedicalBoardInPresenceCases();

        for (MedicalBoardCase b : boardInPresenceCases) {

            if (b.getMedicalBoardSlot() != null) {

                Calendar c2 = Calendar.getInstance();
                c2.setTime(b.getMedicalBoardSlot().getStartDateTime());

                boolean isSameDay = c.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
                        && c.get(Calendar.YEAR) == c2.get(Calendar.YEAR);

                c2.add(Calendar.DAY_OF_YEAR, -3);

                boolean isThreeDayLater = c.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
                        && c.get(Calendar.YEAR) == c2.get(Calendar.YEAR);

                c2.add(Calendar.DAY_OF_YEAR, -4);

                boolean isWeekLater = c.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
                        && c.get(Calendar.YEAR) == c2.get(Calendar.YEAR);

                if (isSameDay) {

                    Notification n = new Notification("Upcoming Medical Board in Presence Reminder", "Please be reminded that you have an upcoming medical board in presence today at " + df2.format(b.getMedicalBoardSlot().getStartDateTime()) + ".", NotificationTypeEnum.MEDICAL_BOARD, b.getMedicalBoardCaseId());
                    try {
                        notificationSessionBeanLocal.createNewNotification(n, b.getConsultation().getBooking().getServiceman().getServicemanId(), Boolean.FALSE);
                        notificationSessionBeanLocal.sendPushNotification("Upcoming Medical Board in Presence Reminder", "Please be reminded that you have an upcoming medical board in presence today at " + df2.format(b.getMedicalBoardSlot().getStartDateTime()) + ".", b.getConsultation().getBooking().getServiceman().getFcmToken());
                    } catch (CreateNotificationException ex) {
                        System.out.println("Error in creating notification " + ex);
                    }

                }

                if (isThreeDayLater) {

                    Notification n = new Notification("Upcoming Medical Board in Presence Reminder", "Please be reminded that you have an upcoming medical board in presence three days later on " + df2.format(b.getMedicalBoardSlot().getStartDateTime()) + ".", NotificationTypeEnum.MEDICAL_BOARD, b.getMedicalBoardCaseId());
                    try {
                        notificationSessionBeanLocal.createNewNotification(n, b.getConsultation().getBooking().getServiceman().getServicemanId(), Boolean.FALSE);
                        notificationSessionBeanLocal.sendPushNotification("Upcoming Medical Board in Presence Reminder", "Please be reminded that you have an upcoming medical board in presence three days later on " + df2.format(b.getMedicalBoardSlot().getStartDateTime()) + ".", b.getConsultation().getBooking().getServiceman().getFcmToken());

                    } catch (CreateNotificationException ex) {
                        System.out.println("Error in creating notification " + ex);
                    }
                }

                if (isWeekLater) {

                    Notification n = new Notification("Upcoming Medical Board in Presence Reminder", "Please be reminded that you have an upcoming medical board in presence next week on " + df2.format(b.getMedicalBoardSlot().getStartDateTime()) + ".", NotificationTypeEnum.MEDICAL_BOARD, b.getMedicalBoardCaseId());
                    try {
                        notificationSessionBeanLocal.createNewNotification(n, b.getConsultation().getBooking().getServiceman().getServicemanId(), Boolean.FALSE);
                        notificationSessionBeanLocal.sendPushNotification("Upcoming Medical Board in Presence Reminder", "Please be reminded that you have an upcoming medical board in presence next week on " + df2.format(b.getMedicalBoardSlot().getStartDateTime()) + ".", b.getConsultation().getBooking().getServiceman().getFcmToken());
                    } catch (CreateNotificationException ex) {
                        System.out.println("Error in creating notification " + ex);
                    }

                }

            }

        }

        System.out.println("============================================================================");

    }

}
