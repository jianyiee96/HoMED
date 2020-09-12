/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

import entity.Serviceman;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
public class ServicemanLoginRsp {

    private Serviceman serviceman;

    public ServicemanLoginRsp() {
    }

    public ServicemanLoginRsp(Serviceman serviceman) {
        this.serviceman = serviceman;
    }

    public Serviceman getServiceman() {
        return serviceman;
    }

    public void setServiceman(Serviceman serviceman) {
        this.serviceman = serviceman;
    }

}
