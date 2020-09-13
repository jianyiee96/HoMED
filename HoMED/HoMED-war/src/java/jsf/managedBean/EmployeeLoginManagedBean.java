package jsf.managedBean;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import util.helper.ThemeCustomiser;

@Named(value = "employeeLoginManagedBean")
@RequestScoped
public class EmployeeLoginManagedBean {

    private String username;
    private String password;

    @Inject
    private ThemeCustomiser themeCustomiser;

    public EmployeeLoginManagedBean() {
    }

    // Method to test dynamic theming, not official login method.
    public void login(ActionEvent event) throws IOException {

        // IF ADMIN ==> use this theme
        themeCustomiser.setComponentTheme("blbluegreyue");
        themeCustomiser.setTopbarColor("bluegrey");

        // IF DOCTOR ==> use this theme
//        themeCustomiser.setComponentTheme("blue");
//        themeCustomiser.setTopbarColor("blue");
        // Login code here
//        try {
//            Employee currentEmployee = employeeSessionBeanLocal.employeeLogin(username, password);
//
//            FacesContext.getCurrentInstance().getExternalContext().getSession(true);
//            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentEmployee", currentEmployee);
//            FacesContext.getCurrentInstance().getExternalContext().redirect("homepage.xhtml");
//        } catch (EmployeeInvalidLoginCredentialException ex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid login credential: " + ex.getMessage(), null));
//        }
        FacesContext.getCurrentInstance().getExternalContext().redirect("homepage.xhtml");
    }

    public void logout(ActionEvent event) throws IOException {
        themeCustomiser.setComponentTheme("cyan");
        themeCustomiser.setTopbarColor("cyan");

        // Invalidating the session and causes the user to logout
        // ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true)).invalidate();
        // Temporary Measure - By right security filter will invoke redirection
        FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
