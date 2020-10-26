package jsf.managedBean;

import entity.Report;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "manageReportManagedBean")
@ViewScoped
public class ManageReportManagedBean implements Serializable {

    private Long reportToViewId;
    private String redirectMessage;

    private Boolean isEditMode;
    private Report report;

    public ManageReportManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        this.report = new Report();
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        if (flash.get("isCreate") != null) {
            isEditMode = true;
        } else {
            try {
                String param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("reportToViewId");
                if (param != null) {
                    this.reportToViewId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("reportToViewId"));
                    // DO CHECK HERE
                    // Ensure that the report that is being viewed is either published/owned by current employee
                }
            } catch (NumberFormatException ex) {
                this.report = null;
                redirectMessage = "Invalid Report ID! Redirecting to Report Management.";
//                FacesContext.getCurrentInstance().addMessage("growl-message", new FacesMessage(FacesMessage.SEVERITY_ERROR, "View Report", "Invalid Report ID"));
            }
            isEditMode = false;
        }
    }

    public void doEdit() {
        isEditMode = !isEditMode;
    }

    public Long getReportToViewId() {
        return reportToViewId;
    }

    public void setReportToViewId(Long reportToViewId) {
        this.reportToViewId = reportToViewId;
    }

    public Boolean getIsEditMode() {
        return isEditMode;
    }

    public void setIsEditMode(Boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public String getRedirectMessage() {
        return redirectMessage;
    }

}
