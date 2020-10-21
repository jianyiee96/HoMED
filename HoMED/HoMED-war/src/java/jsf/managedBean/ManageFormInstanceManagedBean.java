package jsf.managedBean;

import ejb.session.stateless.FormInstanceSessionBeanLocal;
import entity.FormInstance;
import entity.MedicalOfficer;
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
import jsf.classes.FormInstanceFieldWrapper;
import org.primefaces.PrimeFaces;
import util.exceptions.SubmitFormInstanceException;
import util.exceptions.UpdateFormInstanceException;

@Named(value = "manageFormInstanceManagedBean")
@ViewScoped
public class ManageFormInstanceManagedBean implements Serializable {

    @EJB
    private FormInstanceSessionBeanLocal formInstanceSessionBean;

    //States
    private Boolean isViewState;
    private Boolean isManageState;

    private Boolean isSuccessfulSubmit;
    private Boolean isSuccessfulSave;
    private Boolean isReloadMainPage;

    private FormInstance formInstanceToView;

    private List<FormInstanceFieldWrapper> formInstanceFieldWrappers;

    private MedicalOfficer medicalOfficer;

    public ManageFormInstanceManagedBean() {
        this.formInstanceToView = new FormInstance();
    }

    @PostConstruct
    public void postConstruct() {
        formInstanceFieldWrappers = new ArrayList<>();
    }

    public void init() {
        refreshFormInstance();
        isManageState = false;
        isViewState = false;

        isReloadMainPage = false;
        isSuccessfulSubmit = false;
        isSuccessfulSave = false;
    }

    public void initView() {
        init();
        isViewState = true;
    }

    public void initManage() {
        init();
        isManageState = true;
    }

    public void refreshFormInstance() {
        formInstanceToView = formInstanceSessionBean.retrieveFormInstance(formInstanceToView.getFormInstanceId());
        formInstanceFieldWrappers = formInstanceToView.getFormInstanceFields().stream()
                .map(fif -> new FormInstanceFieldWrapper(fif))
                .collect(Collectors.toList());
//        System.out.println("RETRIEVING FORM INSTANCE:");
//        formInstanceFieldWrappers.forEach(wrapper -> {
//            System.out.println("POSITON " + wrapper.getFormInstanceField().getFormFieldMapping().getPosition() + ": " + wrapper.getFormInstanceField().getFormInstanceFieldValues().size());
//            wrapper.getFormInstanceField().getFormInstanceFieldValues().forEach(y -> {
//                System.out.println("\t- " + y.getInputValue());
//            });
//        });
    }

    public void saveFormInstance() {
        try {
            formInstanceFieldWrappers.forEach(wrapper -> wrapper.prepareSubmission(false));

//            System.out.println("SAVING FORM INSTANCE:");
//            formInstanceFieldWrappers.forEach(wrapper -> {
//                System.out.println("POSITON " + wrapper.getFormInstanceField().getFormFieldMapping().getPosition() + ": " + wrapper.getFormInstanceField().getFormInstanceFieldValues().size());
//                wrapper.getFormInstanceField().getFormInstanceFieldValues().forEach(y -> {
//                    System.out.println("\t- " + y.getInputValue());
//                });
//            });

            formInstanceSessionBean.updateFormInstanceFieldValues(formInstanceToView);
            PrimeFaces.current().executeScript("PF('dlgClose').hide()");
            PrimeFaces.current().executeScript("PF('dlgManageFormInstance').hide()");
            refreshFormInstance();
            isReloadMainPage = true;
            isSuccessfulSave = true;
        } catch (UpdateFormInstanceException ex) {
            formInstanceFieldWrappers.forEach(wrapper -> wrapper.prepareLoad());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ex.getMessage()));
        }
    }

    public void submitFormInstance() {
        formInstanceFieldWrappers.forEach(wrapper -> wrapper.prepareSubmission(true));

//        System.out.println("SAVING FORM INSTANCE:");
//        formInstanceFieldWrappers.forEach(wrapper -> {
//            System.out.println("POSITON " + wrapper.getFormInstanceField().getFormFieldMapping().getPosition() + ": " + wrapper.getFormInstanceField().getFormInstanceFieldValues().size());
//            wrapper.getFormInstanceField().getFormInstanceFieldValues().forEach(y -> {
//                System.out.println("\t- " + y.getInputValue());
//            });
//        });

        formInstanceFieldWrappers.stream()
                .forEach(wrapper -> {
                    if (wrapper.getErrorMessage() != null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, wrapper.getErrorMessage()));
                    }
                });

        Boolean errorPresent = formInstanceFieldWrappers.stream()
                .anyMatch(wrapper -> wrapper.getErrorMessage() != null);

        if (!errorPresent) {
            try {
                formInstanceSessionBean.submitFormInstanceByDoctor(formInstanceToView, medicalOfficer.getEmployeeId());
                isReloadMainPage = true;
                isSuccessfulSubmit = true;
                PrimeFaces.current().executeScript("PF('dlgManageFormInstance').hide()");
            } catch (SubmitFormInstanceException ex) {
                formInstanceFieldWrappers.forEach(wrapper -> wrapper.prepareLoad());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ex.getMessage()));
            }
        } else {
            formInstanceFieldWrappers.forEach(wrapper -> wrapper.prepareLoad());
        }
    }

    public FormInstance getFormInstanceToView() {
        return formInstanceToView;
    }

    public void setFormInstanceToView(FormInstance formInstanceToView) {
        formInstanceToView.getFormInstanceFields().sort((x, y) -> x.getFormFieldMapping().getPosition() - y.getFormFieldMapping().getPosition());
        this.formInstanceToView = formInstanceToView;
    }

    public Boolean getIsViewState() {
        return isViewState;
    }

    public Boolean getIsManageState() {
        return isManageState;
    }

    public List<FormInstanceFieldWrapper> getFormInstanceFieldWrappers() {
        return formInstanceFieldWrappers;
    }

    public void setFormInstanceFieldWrappers(List<FormInstanceFieldWrapper> formInstanceFieldWrappers) {
        this.formInstanceFieldWrappers = formInstanceFieldWrappers;
    }

    public Boolean getIsSuccessfulSubmit() {
        return isSuccessfulSubmit;
    }

    public MedicalOfficer getMedicalOfficer() {
        return medicalOfficer;
    }

    public void setMedicalOfficer(MedicalOfficer medicalOfficer) {
        this.medicalOfficer = medicalOfficer;
    }

    public Boolean getIsReloadMainPage() {
        return isReloadMainPage;
    }

    public Boolean getIsSuccessfulSave() {
        return isSuccessfulSave;
    }

}
