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
public class ServicemanUpdateReq {
    
    private Serviceman serviceman;

    public ServicemanUpdateReq() {
    }

    public ServicemanUpdateReq(Serviceman serviceman) {
        this.serviceman = serviceman;
    }

    public Serviceman getServiceman() {
        return serviceman;
    }

    public void setServiceman(Serviceman serviceman) {
        this.serviceman = serviceman;
    }
    
}
