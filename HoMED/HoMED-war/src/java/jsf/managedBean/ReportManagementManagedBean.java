package jsf.managedBean;

import entity.Report;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.PrimeFaces;

@Named(value = "reportManagementManagedBean")
@ViewScoped
public class ReportManagementManagedBean implements Serializable {

    private Integer filterOption;

    private List<Report> reports;
    private List<Report> filteredReports;

    public ReportManagementManagedBean() {
        reports = new ArrayList<>();
        filteredReports = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        filterOption = 1;

    }

    public void doCreateReport() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("isCreate", true);
            FacesContext.getCurrentInstance().getExternalContext().redirect("manage-report.xhtml");
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to redirect!", ex.getMessage()));
        }
    }

    public void doFilter() {
        // 1 - All
        // 2 - My Reports
        // 3 - By Others
        filteredReports = reports.stream()
                .filter(report -> true)
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
