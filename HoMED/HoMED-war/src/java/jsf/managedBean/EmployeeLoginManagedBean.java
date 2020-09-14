package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.io.IOException;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
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

    // Method to test dynamic theming, not official login method.
    public void login(ActionEvent event) throws IOException {

        // IF DOCTOR ==> use this theme
//        themeCustomiser.setComponentTheme("blue");
//        themeCustomiser.setTopbarColor("blue");
        // Login code here
        Employee currentEmployee;
        try {
            currentEmployee = employeeSessionBeanLocal.employeeLogin(nric, password);

            FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentEmployee", currentEmployee);
            // IF ADMIN ==> use this theme
            if (currentEmployee.getRole().equals("admin")) {
                themeCustomiser.setComponentTheme("blbluegreyue");
                themeCustomiser.setTopbarColor("bluegrey");
            } else if (currentEmployee.getRole().equals("clerk")) {
                themeCustomiser.setComponentTheme("magenta");
                themeCustomiser.setTopbarColor("magenta");
            } else if (currentEmployee.getRole().equals("mo")) {
                themeCustomiser.setComponentTheme("rose");
                themeCustomiser.setTopbarColor("rose");
            }
            
            FacesContext.getCurrentInstance().getExternalContext().redirect("homepage.xhtml");
        } catch (EmployeeInvalidLoginCredentialException ex) {
            System.out.println("*********** EmployeeInvalidLoginCredentialException caught");
            System.out.println("*********** Fired 1");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error! Contact admin." + ex.getMessage(), null));
            System.out.println("*********** Fired 2");
        }

        
    }

    public void logout(ActionEvent event) throws IOException {
        themeCustomiser.setComponentTheme("cyan");
        themeCustomiser.setTopbarColor("cyan");

        // Invalidating the session and causes the user to logout
         ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true)).invalidate();
        // Temporary Measure - By right security filter will invoke redirection
        FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
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
