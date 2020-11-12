/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.singleton;

import ejb.session.stateless.BookingSessionBeanLocal;
import ejb.session.stateless.MedicalBoardCaseSessionBeanLocal;
import ejb.session.stateless.SlotSessionBeanLocal;
import entity.Booking;
import entity.MedicalBoardCase;
import entity.MedicalBoardSlot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import util.enumeration.MedicalBoardSlotStatusEnum;
import util.exceptions.ExpireSlotException;
import util.exceptions.MarkBookingAbsentException;
import util.exceptions.UpdateMedicalBoardSlotException;

@Singleton
@LocalBean
@Startup
@DependsOn("DataInitializationSessionBean")
public class MedicalBoardSlotExpiryManager {

    @EJB
    SlotSessionBeanLocal slotSessionBeanLocal;

    @EJB
    MedicalBoardCaseSessionBeanLocal medicalBoardCaseSessionBeanLocal;

    @Resource
    private TimerService timerService;

    @PostConstruct
    public void initialTrigger() {
        startMedicalBoardSlotExpiryManager();
    }

    @Schedule(hour = "0", info = "startMedicalBoardSlotExpiryManager")
    public void startMedicalBoardSlotExpiryManager() {
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timeStamp = df.format(now);

        System.out.println("============================================================================");
        System.out.println("- MedicalBoardSlotExpiryManager: Timeout at " + timeStamp);

        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
//        c.add(Calendar.DATE, 1);
        Date floorToday = c.getTime();

        System.out.println("- ExpiryManager: Marking bookings absent if start time is before " + df.format(floorToday));

        List<MedicalBoardSlot> slots = slotSessionBeanLocal.retrieveMedicalBoardSlots();

        for (MedicalBoardSlot mbs : slots) {

            if (mbs.getStartDateTime().before(floorToday)
                    && (mbs.getMedicalBoardSlotStatusEnum() == MedicalBoardSlotStatusEnum.UNASSIGNED
                    || mbs.getMedicalBoardSlotStatusEnum() == MedicalBoardSlotStatusEnum.ASSIGNED
                    || mbs.getMedicalBoardSlotStatusEnum() == MedicalBoardSlotStatusEnum.ALLOCATED)) {

                try {
                    System.out.println("MedicalBoardSlot has Expired -> Marking Absent! id: " + mbs.getSlotId());

                    medicalBoardCaseSessionBeanLocal.allocateMedicalBoardCasesToMedicalBoardSlot(mbs, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                    slotSessionBeanLocal.expireMedicalBoardSlot(mbs.getSlotId());

                } catch (UpdateMedicalBoardSlotException ex) {
                    System.out.println("Unable to remove case allocation to medical board slot: " + ex.getMessage());
                } catch (ExpireSlotException ex) {
                    System.out.println("Unable to expire medical board slot: " + ex.getMessage());
                }

            }

        }

        System.out.println("============================================================================");

    }

}
