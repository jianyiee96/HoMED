/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
public class UnassignFcmTokenReq {

    private Long servicemanId;

    public UnassignFcmTokenReq() {
    }

    public UnassignFcmTokenReq(Long servicemanId) {
        this.servicemanId = servicemanId;
    }

    public Long getServicemanId() {
        return servicemanId;
    }

    public void setServicemanId(Long servicemanId) {
        this.servicemanId = servicemanId;
    }

}
