/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
public class ReadAllNotificationsReq {

    private String servicemanId;

    public ReadAllNotificationsReq() {
    }

    public ReadAllNotificationsReq(String servicemanId) {
        this.servicemanId = servicemanId;
    }

    public String getServicemanId() {
        return servicemanId;
    }

    public void setServicemanId(String servicemanId) {
        this.servicemanId = servicemanId;
    }

}
