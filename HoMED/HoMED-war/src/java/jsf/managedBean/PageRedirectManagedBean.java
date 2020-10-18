package jsf.managedBean;

import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "pageRedirectManagedBean")
@ViewScoped
public class PageRedirectManagedBean implements Serializable {

    private int countdownRemainingSeconds;

    public PageRedirectManagedBean() {

    }

    @PostConstruct
    public void postConstruct() {
        countdownRemainingSeconds = 5;
    }

    public int getCountdownRemainingSeconds() {
        return countdownRemainingSeconds;
    }

    public String getCountdownValue() {
        int mins = countdownRemainingSeconds / 60;
        int remainder = countdownRemainingSeconds - (mins * 60);
        return String.format("%02d:%02d", mins, remainder);
    }

    public void decreaseCountdown(String outcome) {
        if (outcome.charAt(0) == '\\' || outcome.charAt(0) == '/') {
            outcome = outcome.substring(1, outcome.length());
        }
        this.countdownRemainingSeconds -= 1;
        if (this.countdownRemainingSeconds == 0) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(outcome);
            } catch (IOException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to redirect!", ex.getMessage()));
            }
        }
    }

}
