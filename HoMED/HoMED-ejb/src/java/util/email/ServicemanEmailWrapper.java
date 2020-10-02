/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.email;

import entity.Serviceman;
import java.util.concurrent.Future;

/**
 *
 * @author Bryan
 */
public class ServicemanEmailWrapper {

    private Future<Boolean> emailResult;
    private Serviceman serviceman;

    public ServicemanEmailWrapper(Future<Boolean> emailResult, Serviceman serviceman) {
        this.emailResult = emailResult;
        this.serviceman = serviceman;
    }

    public Future<Boolean> getEmailResult() {
        return emailResult;
    }

    public void setEmailResult(Future<Boolean> emailResult) {
        this.emailResult = emailResult;
    }

    public Serviceman getServiceman() {
        return serviceman;
    }

    public void setServiceman(Serviceman serviceman) {
        this.serviceman = serviceman;
    }

}
