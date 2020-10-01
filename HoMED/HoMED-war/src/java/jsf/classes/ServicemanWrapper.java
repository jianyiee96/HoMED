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
    private Boolean isValid;
    private Boolean isSelected;
    private Boolean isDismissed;

    public ServicemanWrapper() {
        this.errorMessages = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            this.errorMessages.add(null);
        }
        this.isValid = Boolean.TRUE;
        this.isSelected = Boolean.FALSE;
        this.isDismissed = Boolean.FALSE;
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

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Boolean getIsDismissed() {
        return isDismissed;
    }

    public void setIsDismissed(Boolean isDismissed) {
        this.isDismissed = isDismissed;
    }

}
