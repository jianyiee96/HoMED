package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.io.IOException;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import util.enumeration.EmployeeRoleEnum;
import util.exceptions.EmployeeInvalidLoginCredentialException;
import util.helper.ThemeCustomiser;

@Named(value = "employeeLoginManagedBean")
@RequestScoped
public class EmployeeLoginManagedBean {

    private String nric;
    private String password;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @Inject
    private ThemeCustomiser themeCustomiser;

    public EmployeeLoginManagedBean() {
    }

    public void login(ActionEvent event) throws IOException {

        Employee currentEmployee;
        try {
            currentEmployee = employeeSessionBeanLocal.employeeLogin(nric, password);

            FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentEmployee", currentEmployee);

            if (currentEmployee.getRole() == EmployeeRoleEnum.ADMIN) {
                themeCustomiser.setComponentTheme("blbluegreyue");
                themeCustomiser.setTopbarColor("bluegrey");
            } else if (currentEmployee.getRole() == EmployeeRoleEnum.CLERK) {
                themeCustomiser.setComponentTheme("magenta");
                themeCustomiser.setTopbarColor("magenta");
            } else if (currentEmployee.getRole() == EmployeeRoleEnum.MEDICAL_OFFICER) {
                themeCustomiser.setComponentTheme("blue");
                themeCustomiser.setTopbarColor("blue");
            }

            FacesContext.getCurrentInstance().getExternalContext().redirect("homepage.xhtml");
        } catch (EmployeeInvalidLoginCredentialException ex) {
            FacesContext.getCurrentInstance().addMessage("loginForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }

    }

    public void logout(ActionEvent event) throws IOException {
        themeCustomiser.setComponentTheme("cyan");
        themeCustomiser.setTopbarColor("cyan");

        // Invalidating the session and causes the user to logout
        ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true)).invalidate();
        FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
    }

    public void timeoutLogout() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("inactiveSession", true);
        logout(null);
    }

    public void idleWarning() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Session about to expire", "You have been inactive for the past 15 mins"));
    }

    public void checkInactivity() {
        System.out.println("CheckINACTIVITY CALLED");
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        if (flash.get("inactiveSession") != null) {
            FacesContext.getCurrentInstance().addMessage("inactivityForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Session Expired", "You have been logged out due to inactivity"));
        }
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
}
