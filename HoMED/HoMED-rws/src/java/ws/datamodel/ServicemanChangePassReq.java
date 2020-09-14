/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
public class ServicemanChangePassReq {
    
    private String nric;
    private String oldPassword;
    private String newPassword;

    public ServicemanChangePassReq() {
    }

    public ServicemanChangePassReq(String nric, String oldPassword, String newPassword) {
        this.nric = nric;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
}
