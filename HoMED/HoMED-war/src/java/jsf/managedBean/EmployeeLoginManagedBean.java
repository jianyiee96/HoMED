package jsf.managedBean;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import util.helper.ThemeCustomiser;

@Named(value = "employeeLoginManagedBean")
@RequestScoped
public class EmployeeLoginManagedBean {

    @Inject
    private ThemeCustomiser themeCustomiser;

    public EmployeeLoginManagedBean() {
    }

    // Method to test dynamic theming, not official login method.
    public void login(ActionEvent event) throws IOException {
        System.out.println("Testing.....2");

        // To dynamically change theme colours next time.
        themeCustomiser.setComponentTheme("bluegrey");
        themeCustomiser.setTopbarColor("bluegrey");
        System.out.println("Testing.....");
        FacesContext.getCurrentInstance().getExternalContext().redirect("homepage.xhtml");
    }
}
