/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
public class AssignFcmTokenReq {

    private Long servicemanId;
    private String fcmToken;

    public AssignFcmTokenReq() {
    }

    public AssignFcmTokenReq(Long servicemanId, String fcmToken) {
        this.servicemanId = servicemanId;
        this.fcmToken = fcmToken;
    }

    public Long getServicemanId() {
        return servicemanId;
    }

    public void setServicemanId(Long servicemanId) {
        this.servicemanId = servicemanId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

}
