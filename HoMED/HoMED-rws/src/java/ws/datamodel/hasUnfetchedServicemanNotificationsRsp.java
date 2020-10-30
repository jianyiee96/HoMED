/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
public class hasUnfetchedServicemanNotificationsRsp {

    private Boolean hasUnfetchedNotifications;

    public hasUnfetchedServicemanNotificationsRsp() {
    }

    public hasUnfetchedServicemanNotificationsRsp(Boolean hasUnfetchedNotifications) {
        this.hasUnfetchedNotifications = hasUnfetchedNotifications;
    }

    public Boolean getHasUnfetchedNotifications() {
        return hasUnfetchedNotifications;
    }

    public void setHasUnfetchedNotifications(Boolean hasUnfetchedNotifications) {
        this.hasUnfetchedNotifications = hasUnfetchedNotifications;
    }

}
