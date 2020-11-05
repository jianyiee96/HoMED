/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.Notification;
import java.util.List;
import javax.ejb.Local;
import util.exceptions.CreateNotificationException;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
@Local
public interface NotificationSessionBeanLocal {
    
    public void sendPushNotification(String title, String body, String svcmanFCMToken);
    
    public Long createNewNotification(Notification newNotification, Long servicemanId, Boolean sendEmail) throws CreateNotificationException;

    public List<Notification> retrieveAllNotificationsByServicemanId(Long servicemanId, Boolean markAsFetched);

    public List<Notification> retrieveAllUnfetchedNotificationsByServicemanId(Long servicemanId);

    public void readNotification(Long notificationId);

    public void deleteNotification(Long notificationId);

}
