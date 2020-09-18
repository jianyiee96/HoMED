package jsf.managedBean;

import entity.Employee;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "viewEmployeeManagedBean")
@ViewScoped
public class ViewEmployeeManagedBean implements Serializable {
    
    private Employee employeeToView;
    
    public ViewEmployeeManagedBean() {
        this.employeeToView = new Employee();
    }

    @PostConstruct
    public void postConstruct() {
    }

    public Employee getEmployeeToView() {
        return employeeToView;
    }

    public void setEmployeeToView(Employee employeeToView) {
        this.employeeToView = employeeToView;
    }
    
    
}
