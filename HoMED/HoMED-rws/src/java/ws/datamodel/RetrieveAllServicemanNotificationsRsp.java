/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

import entity.Notification;
import java.util.List;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
public class RetrieveAllServicemanNotificationsRsp {

    private List<Notification> notifications;

    public RetrieveAllServicemanNotificationsRsp() {
    }

    public RetrieveAllServicemanNotificationsRsp(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

}
