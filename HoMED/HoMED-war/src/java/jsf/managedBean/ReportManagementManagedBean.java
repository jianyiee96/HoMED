package jsf.managedBean;

import ejb.session.stateless.ReportSessionBeanLocal;
import entity.Employee;
import entity.Report;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.PrimeFaces;
import util.exceptions.DeleteReportException;

@Named(value = "reportManagementManagedBean")
@ViewScoped
public class ReportManagementManagedBean implements Serializable {

    @EJB(name = "ReportSessionBeanLocal")
    private ReportSessionBeanLocal reportSessionBeanLocal;

    private Integer filterOption;
    private Employee currentEmployee;

    private List<Report> reports;
    private List<Report> filteredReports;

    public ReportManagementManagedBean() {
        reports = new ArrayList<>();
        filteredReports = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        currentEmployee = (Employee) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentEmployee");
        filterOption = 1;
        refreshData();
    }
    
    public void refreshData() {
        reports = reportSessionBeanLocal.retrieveAllReports();
        doFilter();
    }

    public void doCreateReport() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("manage-report.xhtml");
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to redirect!", ex.getMessage()));
        }
    }

    public void doDeleteReport(Long reportId) {
        try {
            reportSessionBeanLocal.deleteReport(reportId);
            refreshData();
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_INFO, "Delete Report", "Successfully deleted report [id:" + reportId + "]"));
        } catch (DeleteReportException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Delete Report", ex.getMessage()));
        }
    }

    public void doFilter() {
        // 1 - All
        // 2 - My Reports
        // 3 - By Others
        filteredReports = reports.stream()
                .filter(report -> {
                    if (filterOption == 1) {
                        return true;
                    } else if (filterOption == 2) {
                        return report.getEmployee().equals(currentEmployee);
                    } else if (filterOption == 3) {
                        return !report.getEmployee().equals(currentEmployee) && report.getDatePublished() != null;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public Integer getFilterOption() {
        return filterOption;
    }

    public void setFilterOption(Integer filterOption) {
        this.filterOption = filterOption;
    }

    public List<Report> getFilteredReports() {
        return filteredReports;
    }

    public void setFilteredReports(List<Report> filteredReports) {
        this.filteredReports = filteredReports;
    }

}
