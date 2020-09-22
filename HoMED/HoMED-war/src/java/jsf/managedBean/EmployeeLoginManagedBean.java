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
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import org.primefaces.PrimeFaces;
import util.enumeration.EmployeeRoleEnum;
import util.exceptions.ActivateEmployeeException;
import util.exceptions.EmployeeInvalidLoginCredentialException;
import util.exceptions.ResetEmployeePasswordException;
import util.helper.ThemeCustomiser;

@Named(value = "employeeLoginManagedBean")
@ViewScoped
public class EmployeeLoginManagedBean implements Serializable {

    private String email;
    private String password;
    private Employee currentEmployee;
    private String activatePassword;
    private String activateRePassword;
    private String forgetPasswordEmail;
    private String forgetPasswordPhoneNumber;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @Inject
    private ThemeCustomiser themeCustomiser;

    @PostConstruct
    public void postConstruct() {
    }

    public EmployeeLoginManagedBean() {
    }

    public void login() throws IOException {

        try {
            currentEmployee = employeeSessionBeanLocal.employeeLogin(email, password);
            if (currentEmployee.getIsActivated()) {
                FacesContext.getCurrentInstance().getExternalContext().getSession(true);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentEmployee", currentEmployee);

                setRoleTheme(currentEmployee.getRole());

                FacesContext.getCurrentInstance().getExternalContext().redirect("homepage.xhtml");
            } else {
                PrimeFaces.current().executeScript("PF('dlgActivatePassword').show()");
            }
        } catch (EmployeeInvalidLoginCredentialException ex) {
            FacesContext.getCurrentInstance().addMessage("formLogin", new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }

    }

    public void logout() throws IOException {
        themeCustomiser.setComponentTheme("cyan");
        themeCustomiser.setTopbarColor("cyan");

        // Invalidating the session and causes the user to logout
        ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true)).invalidate();
        FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
    }

    public void activateAccount() throws IOException {
        try {
            currentEmployee = employeeSessionBeanLocal.activateEmployee(currentEmployee.getEmail(), activatePassword, activateRePassword);
            FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentEmployee", currentEmployee);

            setRoleTheme(currentEmployee.getRole());

            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("activatedAccount", true);
            FacesContext.getCurrentInstance().getExternalContext().redirect("homepage.xhtml");
        } catch (ActivateEmployeeException ex) {
            FacesContext.getCurrentInstance().addMessage("formActivatePassword", new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public void checkIfActivatedUponLogin() {
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        if (flash.get("activatedAccount") != null) {
            FacesContext.getCurrentInstance().addMessage("formTemplateGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully Activated Account!", "Password has been successfully changed"));
        }
    }

    public void sendOtp() {
        try {
            employeeSessionBeanLocal.resetEmployeePassword(forgetPasswordEmail, forgetPasswordPhoneNumber);
            PrimeFaces.current().executeScript("PF('dlgForgetPassword').hide()");
            FacesContext.getCurrentInstance().addMessage("formInactivity", new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully reset password!", "Do check your email for the new OTP"));
        } catch (ResetEmployeePasswordException ex) {
            FacesContext.getCurrentInstance().addMessage("formForgetPassword", new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public void checkInactivity() {
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        if (flash.get("inactiveSession") != null) {
            FacesContext.getCurrentInstance().addMessage("formInactivity", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Session Expired", "You have been logged out due to inactivity"));
        }
    }

    private void setRoleTheme(EmployeeRoleEnum role) {
        if (role == EmployeeRoleEnum.ADMIN) {
            themeCustomiser.setComponentTheme("blbluegreyue");
            themeCustomiser.setTopbarColor("bluegrey");
        } else if (role == EmployeeRoleEnum.CLERK) {
            themeCustomiser.setComponentTheme("teal");
            themeCustomiser.setTopbarColor("teal");
        } else if (role == EmployeeRoleEnum.MEDICAL_OFFICER) {
            themeCustomiser.setComponentTheme("blue");
            themeCustomiser.setTopbarColor("blue");
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getForgetPasswordEmail() {
        return forgetPasswordEmail;
    }

    public void setForgetPasswordEmail(String forgetPasswordEmail) {
        this.forgetPasswordEmail = forgetPasswordEmail;
    }

    public String getForgetPasswordPhoneNumber() {
        return forgetPasswordPhoneNumber;
    }

    public void setForgetPasswordPhoneNumber(String forgetPasswordPhoneNumber) {
        this.forgetPasswordPhoneNumber = forgetPasswordPhoneNumber;
    }

}
