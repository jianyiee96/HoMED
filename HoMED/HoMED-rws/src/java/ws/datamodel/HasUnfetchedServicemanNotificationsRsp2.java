/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
public class HasUnfetchedServicemanNotificationsRsp2 {

    private Boolean hasUnfetchedNotifications;

    public HasUnfetchedServicemanNotificationsRsp2() {
    }

    public HasUnfetchedServicemanNotificationsRsp2(Boolean hasUnfetchedNotifications) {
        this.hasUnfetchedNotifications = hasUnfetchedNotifications;
    }

    public Boolean getHasUnfetchedNotifications() {
        return hasUnfetchedNotifications;
    }

    public void setHasUnfetchedNotifications(Boolean hasUnfetchedNotifications) {
        this.hasUnfetchedNotifications = hasUnfetchedNotifications;
    }

}
