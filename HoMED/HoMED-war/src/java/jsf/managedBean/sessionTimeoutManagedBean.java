package jsf.managedBean;

import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

@Named(value = "sessionTimeoutManagedBean")
@ViewScoped
public class sessionTimeoutManagedBean implements Serializable {

    private Boolean isCountdownState;
    private int countdownRemainingSeconds;
    private String counterString;

    @Inject
    private EmployeeLoginManagedBean employeeLoginManagedBean;

    public sessionTimeoutManagedBean() {
    }

    @PostConstruct
    public void postCounstrct() {
        this.isCountdownState = false;
    }

    public void startCountdown() {
        this.isCountdownState = true;
        this.countdownRemainingSeconds = 60;
    }

    public void decrease() throws IOException {
        this.countdownRemainingSeconds -= 1;
        if (this.countdownRemainingSeconds == 0) {
            logout();
        }
    }

    public Boolean getIsCountdownState() {
        return isCountdownState;
    }

    public void setIsCountdownState(Boolean isCountdownState) {
        this.isCountdownState = isCountdownState;
    }

    public String getCounterString() {
        int mins = countdownRemainingSeconds / 60;
        int remainder = countdownRemainingSeconds - (mins * 60);
        int secs = remainder;
        return String.format("%02d:%02d", mins, remainder);
    }

    public void logout() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("inactiveSession", true);
        this.employeeLoginManagedBean.logout();
    }

    public EmployeeLoginManagedBean getEmployeeLoginManagedBean() {
        return employeeLoginManagedBean;
    }

}
