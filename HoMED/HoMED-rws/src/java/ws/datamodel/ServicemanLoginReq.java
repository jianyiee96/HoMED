/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

public class ServicemanLoginReq {

    private String email;
    private String password;

    public ServicemanLoginReq() {
    }

    public ServicemanLoginReq(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
