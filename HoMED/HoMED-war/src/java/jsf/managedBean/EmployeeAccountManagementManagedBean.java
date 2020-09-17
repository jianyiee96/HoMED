/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
    
@Named(value = "employeeAccountManagementManagedBean")
@ViewScoped
public class EmployeeAccountManagementManagedBean implements Serializable {

    
    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;
    
    private List<Employee> employees;
    
    public EmployeeAccountManagementManagedBean() {
    }
    
    @PostConstruct
    public void postConstruct() {

        employees = employeeSessionBeanLocal.retrieveAllEmployees();
        
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
    
    
    
}
