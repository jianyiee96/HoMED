/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.singleton;

import ejb.session.stateless.BookingSessionBeanLocal;
import entity.Booking;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import javax.ejb.TimerService;
import util.exceptions.MarkBookingAbsentException;

@Singleton
@LocalBean
@Startup
public class BookingExpiryManager {

    @EJB
    BookingSessionBeanLocal bookingSessionBeanLocal;

    @Resource
    private TimerService timerService;

    @PostConstruct
    public void initialTrigger() {
        startBookingExpiryManager();
    }

    @Schedule(hour = "0", info = "startBookingExpiryManager")
    public void startBookingExpiryManager() {
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timeStamp = df.format(now);

        System.out.println("============================================================================");
        System.out.println("- ExpiryManager: Timeout at " + timeStamp);

        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
//        c.add(Calendar.DATE, 1);
        Date floorToday = c.getTime();

        System.out.println("- ExpiryManager: Marking bookings absent if start time is before " + df.format(floorToday));

        List<Booking> upcomingBookings = bookingSessionBeanLocal.retrieveAllUpcomingBookings();

        for (Booking b : upcomingBookings) {

            if (b.getBookingSlot().getStartDateTime().before(floorToday)) {

                try {
                    System.out.println("Booking has Expired -> Marking Absent! id: " + b.getBookingId() + " (" + df.format(b.getBookingSlot().getStartDateTime()) + ")");
                    bookingSessionBeanLocal.markBookingAbsent(b.getBookingId(), "Marked Absent By System");
                    
                } catch (MarkBookingAbsentException ex) {
                    System.out.println("Unable to mark booking (" + b.getBookingId() + ") as absent: " + ex.getMessage());
                }

            }

        }

        System.out.println("============================================================================");

    }

}
