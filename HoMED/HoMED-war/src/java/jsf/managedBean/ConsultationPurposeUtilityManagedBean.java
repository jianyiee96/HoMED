/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.ConsultationPurposeSessionBeanLocal;
import ejb.session.stateless.FormTemplateSessionBeanLocal;
import entity.ConsultationPurpose;
import entity.FormTemplate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import util.exceptions.CreateConsultationPurposeException;
import util.exceptions.RelinkFormTemplatesException;

@Named(value = "consultationPurposeUtilityManagedBean")
@ViewScoped
public class ConsultationPurposeUtilityManagedBean implements Serializable {

    @EJB
    private ConsultationPurposeSessionBeanLocal consultationPurposeSessionBeanLocal;

    @EJB
    private FormTemplateSessionBeanLocal formTemplateSessionBeanLocal;

    private List<ConsultationPurpose> consultationPurposes;

    private DualListModel<FormTemplate> dualListFormTemplates;

    private List<FormTemplate> allFormTemplates;

    private ConsultationPurpose selectedConsultationPurpose;

    private String createConsultationPurposeName;

    public ConsultationPurposeUtilityManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        this.selectedConsultationPurpose = null;
        this.consultationPurposes = consultationPurposeSessionBeanLocal.retrieveAllConsultationPurposes();
        this.allFormTemplates = formTemplateSessionBeanLocal.retrieveAllFormTemplates();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("ConsultationPurposeUtilityManagedBean.allFormTemplates", allFormTemplates);

    }

    public void createConsultationPurpose() {
        try {
            if (this.createConsultationPurposeName != null || !this.createConsultationPurposeName.equals("")) {
                consultationPurposeSessionBeanLocal.createConsultationPurpose(new ConsultationPurpose(this.createConsultationPurposeName));
                this.createConsultationPurposeName = "";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully created new consultation purpose!", "Database has been updated."));
                this.consultationPurposes = consultationPurposeSessionBeanLocal.retrieveAllConsultationPurposes();
            }

        } catch (CreateConsultationPurposeException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to create consultation purpose!", ex.getMessage()));

        }

    }

    public void deleteConsultationPurpose(ActionEvent event) {
        Long consultationPurposeId = (Long) event.getComponent().getAttributes().get("consultationPurposeIdToDelete");
        consultationPurposeSessionBeanLocal.deleteConsultationPurpose(consultationPurposeId);

        this.consultationPurposes = consultationPurposeSessionBeanLocal.retrieveAllConsultationPurposes();
        this.allFormTemplates = formTemplateSessionBeanLocal.retrieveAllFormTemplates();

        for (ConsultationPurpose cp : this.consultationPurposes) {
            if (cp.getConsultationPurposeId().equals(consultationPurposeId)) {
                this.setSelectedConsultationPurpose(cp);
                break;

            }
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("ConsultationPurposeUtilityManagedBean.allFormTemplates", allFormTemplates);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully de-activate consultation purpose!", "Database has been updated."));
    }

    public void restoreConsultationPurpose(ActionEvent event) {
        Long consultationPurposeId = (Long) event.getComponent().getAttributes().get("consultationPurposeIdToRestore");
        consultationPurposeSessionBeanLocal.restoreConsultationPurpose(consultationPurposeId);

        this.consultationPurposes = consultationPurposeSessionBeanLocal.retrieveAllConsultationPurposes();
        this.allFormTemplates = formTemplateSessionBeanLocal.retrieveAllFormTemplates();

        for (ConsultationPurpose cp : this.consultationPurposes) {
            if (cp.getConsultationPurposeId().equals(consultationPurposeId)) {
                this.setSelectedConsultationPurpose(cp);
                break;
            }
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("ConsultationPurposeUtilityManagedBean.allFormTemplates", allFormTemplates);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully activate consultation purpose!", "Database has been updated."));
    }

    public void toggleConsultationPurposeReviewOnly(ActionEvent event) {
        Long consultationPurposeId = (Long) event.getComponent().getAttributes().get("consultationPurposeIdToToggle");
        consultationPurposeSessionBeanLocal.toggleConsultationPurposeReviewOnly(consultationPurposeId);

        this.consultationPurposes = consultationPurposeSessionBeanLocal.retrieveAllConsultationPurposes();
        this.allFormTemplates = formTemplateSessionBeanLocal.retrieveAllFormTemplates();
        
        for (ConsultationPurpose cp : this.consultationPurposes) {
            if (cp.getConsultationPurposeId().equals(consultationPurposeId)) {
                this.setSelectedConsultationPurpose(cp);
                break;
            }
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("ConsultationPurposeUtilityManagedBean.allFormTemplates", allFormTemplates);
    }

    public void selectConsultationPurpose(ActionEvent event) {
        selectedConsultationPurpose = (ConsultationPurpose) event.getComponent().getAttributes().get("consultationPurposeToView");
        setSelectedConsultationPurpose(selectedConsultationPurpose);
    }

    public List<ConsultationPurpose> getConsultationPurposes() {
        return consultationPurposes;
    }

    public void setConsultationPurposes(List<ConsultationPurpose> consultationPurposes) {
        this.consultationPurposes = consultationPurposes;
    }

    public List<FormTemplate> getAllFormTemplates() {
        return allFormTemplates;
    }

    public void setAllFormTemplates(List<FormTemplate> allFormTemplates) {
        this.allFormTemplates = allFormTemplates;
    }

    public ConsultationPurpose getSelectedConsultationPurpose() {
        return selectedConsultationPurpose;
    }

    public void setSelectedConsultationPurpose(ConsultationPurpose selectedConsultationPurpose) {
        this.selectedConsultationPurpose = consultationPurposeSessionBeanLocal.retrieveConsultationPurpose(selectedConsultationPurpose.getConsultationPurposeId());

        List<FormTemplate> allPublishedFormTemplate = formTemplateSessionBeanLocal.retrieveAllPublishedFormTemplates();
        List<FormTemplate> unlinkedFormTemplates = new ArrayList<>();
        List<FormTemplate> linkedFormTemplates = this.selectedConsultationPurpose.getFormTemplates();

        for (FormTemplate ft : allPublishedFormTemplate) {
            if (!linkedFormTemplates.contains(ft)) {
                unlinkedFormTemplates.add(ft);
            }
        }
        dualListFormTemplates = new DualListModel<>(unlinkedFormTemplates, linkedFormTemplates);
    }

    public void onTransfer(TransferEvent event) {
        try {
            consultationPurposeSessionBeanLocal.relinkFormTemplates(selectedConsultationPurpose.getConsultationPurposeId(), dualListFormTemplates.getTarget());
        } catch (RelinkFormTemplatesException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to link form templates", ex.getMessage()));
        }
    }

    public String getCreateConsultationPurposeName() {
        return createConsultationPurposeName;
    }

    public void setCreateConsultationPurposeName(String createConsultationPurposeName) {
        this.createConsultationPurposeName = createConsultationPurposeName;
    }

    public DualListModel<FormTemplate> getDualListFormTemplates() {
        return dualListFormTemplates;
    }

    public void setDualListFormTemplates(DualListModel<FormTemplate> dualListFormTemplates) {
        this.dualListFormTemplates = dualListFormTemplates;
    }

    public String renderIsActive(boolean value) {
        if (value) {
            return "Active";
        } else {
            return "Inactive";
        }
    }

    public String renderReview(boolean value) {
        if (value) {
            return "Review Only";
        } else {
            return "Any Consultation";
        }
    }

}
