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

    @Schedule(hour = "*", minute = "*/1", info = "startBookingExpiryManager")
    public void startBookingExpiryManager() {
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timeStamp = df.format(now);

        System.out.println("============================================================================");
        System.out.println("- ExpiryManager: Timeout at " + timeStamp);

        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.add(Calendar.HOUR, -1);
        Date oneHourBefore = c.getTime();

        List<Booking> upcomingBookings = bookingSessionBeanLocal.retrieveAllUpcomingBookings();

        for (Booking b : upcomingBookings) {

            if (b.getBookingSlot().getStartDateTime().before(oneHourBefore)) {

                try {
                    System.out.println("Booking has Expired -> Marking Absent! id: " + b.getBookingId() + " (" + df.format(b.getBookingSlot().getStartDateTime()) + ")");
                    bookingSessionBeanLocal.markBookingAbsent(b.getBookingId());
                } catch (MarkBookingAbsentException ex) {
                    System.out.println("Unable to mark booking (" + b.getBookingId() + ") as absent: " + ex.getMessage());
                }

            } else {
//                System.out.println("Booking not expired. id: " + b.getBookingId() + " (" + df.format(b.getBookingSlot().getStartDateTime()) + ")");
            }

        }

        System.out.println("============================================================================");

    }

}
