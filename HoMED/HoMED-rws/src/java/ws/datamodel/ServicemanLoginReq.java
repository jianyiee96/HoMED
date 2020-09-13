/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

public class ServicemanLoginReq {

    private String nric;
    private String password;

    public ServicemanLoginReq() {
    }

    public ServicemanLoginReq(String nric, String password) {
        this.nric = nric;
        this.password = password;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNric() {
        return nric;
    }

    public String getPassword() {
        return password;
    }

}
