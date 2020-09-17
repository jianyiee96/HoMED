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
public class ServicemanUpdateRsp {

    private Serviceman serviceman;

    public ServicemanUpdateRsp() {
    }

    public ServicemanUpdateRsp(Serviceman serviceman) {
        this.serviceman = serviceman;
    }

    public Serviceman getServiceman() {
        return serviceman;
    }

    public void setServiceman(Serviceman serviceman) {
        this.serviceman = serviceman;
    }

}
