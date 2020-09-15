package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import org.primefaces.PrimeFaces;
import util.enumeration.EmployeeRoleEnum;
import util.exceptions.EmployeeInvalidLoginCredentialException;
import util.helper.ThemeCustomiser;

@Named(value = "employeeLoginManagedBean")
@ViewScoped
public class EmployeeLoginManagedBean implements Serializable {

    private String nric;
    private String password;
    private Boolean isActivateMode;
    private Employee currentEmployee;
    private String activatePassword;
    private String activateRePassword;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @Inject
    private ThemeCustomiser themeCustomiser;

    @PostConstruct
    public void postConstruct() {
        this.isActivateMode = false;
    }

    public EmployeeLoginManagedBean() {
    }

    public void login(ActionEvent event) throws IOException {

        try {
            currentEmployee = employeeSessionBeanLocal.employeeLogin(nric, password);
            if (isActivateMode) {
                if (currentEmployee.getIsActivated()) {
                    FacesContext.getCurrentInstance().addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Account has already been activated!", null));
                } else {
                    // DISPLAY MODAL HERE
                    PrimeFaces.current().executeScript("PF('dlg').show()");
                }
            } else {
                if (currentEmployee.getIsActivated()) {
                    FacesContext.getCurrentInstance().getExternalContext().getSession(true);
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentEmployee", currentEmployee);

                    setRoleTheme(currentEmployee.getRole());

                    FacesContext.getCurrentInstance().getExternalContext().redirect("homepage.xhtml");
                } else {
                    FacesContext.getCurrentInstance().addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Account is not yet activated!", null));
                }
            }
        } catch (EmployeeInvalidLoginCredentialException ex) {
            FacesContext.getCurrentInstance().addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed " + ex.getMessage(), null));
        }

    }

    public void logout(ActionEvent event) throws IOException {
        themeCustomiser.setComponentTheme("cyan");
        themeCustomiser.setTopbarColor("cyan");

        // Invalidating the session and causes the user to logout
        ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true)).invalidate();
        FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
    }

    public void activateAccount(ActionEvent event) throws IOException {
        if (!activatePassword.equals(activateRePassword)) {
            FacesContext.getCurrentInstance().addMessage("dialogForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Activation Failed! Passwords do not match.", null));
        } else {
            // HANDLE UPDATE PASSWORD LOGIC HERE
            // You can update currentEmployee in this ManagedBean
            // employeeSessionBeanLocal.(updatePasswordMethod).(activatePassword,activateRePassword, currentEmployee)
            // Make sure that in the session bean you are changing his isActivated status

            this.isActivateMode = false;
            try {
                currentEmployee = employeeSessionBeanLocal.employeeLogin(currentEmployee.getNric(), activatePassword);
                FacesContext.getCurrentInstance().getExternalContext().getSession(true);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentEmployee", currentEmployee);

                setRoleTheme(currentEmployee.getRole());

                FacesContext.getCurrentInstance().getExternalContext().redirect("homepage.xhtml");
            } catch (EmployeeInvalidLoginCredentialException ex) {
                FacesContext.getCurrentInstance().addMessage("dialogForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed " + ex.getMessage(), null));
            }
        }
    }

    public void timeoutLogout() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("inactiveSession", true);
        logout(null);
    }

    public void idleWarning() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Session about to expire", "You have been inactive for the past 15 mins. You will be logged out in the next minute."));
    }

    public void checkInactivity() {
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        if (flash.get("inactiveSession") != null) {
            FacesContext.getCurrentInstance().addMessage("inactivityForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Session Expired", "You have been logged out due to inactivity"));
        }
    }

    private void setRoleTheme(EmployeeRoleEnum role) {
        if (role == EmployeeRoleEnum.ADMIN) {
            themeCustomiser.setComponentTheme("blbluegreyue");
            themeCustomiser.setTopbarColor("bluegrey");
        } else if (role == EmployeeRoleEnum.CLERK) {
            themeCustomiser.setComponentTheme("magenta");
            themeCustomiser.setTopbarColor("magenta");
        } else if (role == EmployeeRoleEnum.MEDICAL_OFFICER) {
            themeCustomiser.setComponentTheme("blue");
            themeCustomiser.setTopbarColor("blue");
        }
    }
    
    public void toggleActivateMode() {
        this.isActivateMode = !this.isActivateMode;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsActivateMode() {
        return isActivateMode;
    }

    public void setIsActivateMode(Boolean isActivateMode) {
        this.isActivateMode = isActivateMode;
    }

    public String getActivatePassword() {
        return activatePassword;
    }

    public void setActivatePassword(String activatePassword) {
        this.activatePassword = activatePassword;
    }

    public String getActivateRePassword() {
        return activateRePassword;
    }

    public void setActivateRePassword(String activateRePassword) {
        this.activateRePassword = activateRePassword;
    }

}
