/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.classes;

import entity.Serviceman;
import java.util.ArrayList;
import java.util.List;

public class ServicemanWrapper {
    
    private Serviceman existingServiceman;
    private Serviceman newServiceman;
    private List<String> errorMessages;

    public ServicemanWrapper() {
        this.errorMessages = new ArrayList<>(11);
    }

    public Serviceman getExistingServiceman() {
        return existingServiceman;
    }

    public void setExistingServiceman(Serviceman existingServiceman) {
        this.existingServiceman = existingServiceman;
    }

    public Serviceman getNewServiceman() {
        return newServiceman;
    }

    public void setNewServiceman(Serviceman newServiceman) {
        this.newServiceman = newServiceman;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
    
    
    
}
