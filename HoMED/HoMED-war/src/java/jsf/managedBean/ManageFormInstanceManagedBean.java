package jsf.managedBean;

import ejb.session.stateless.FormInstanceSessionBeanLocal;
import entity.FormInstance;
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

@Named(value = "manageFormInstanceManagedBean")
@ViewScoped
public class ManageFormInstanceManagedBean implements Serializable {

    @EJB
    private FormInstanceSessionBeanLocal formInstanceSessionBean;

    //States
    private Boolean isViewState;
    private Boolean isManageState;

    private FormInstance formInstanceToView;

    private List<FormInstanceFieldWrapper> formInstanceFieldWrappers;

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
    }

    public void initView() {
        init();
        isViewState = true;
    }

    public void initManage() {
        init();
        isManageState = true;
    }

    public void saveFormInstance() {

    }

    public void refreshFormInstance() {
        formInstanceToView = formInstanceSessionBean.retrieveFormInstance(formInstanceToView.getFormInstanceId());
        formInstanceFieldWrappers = formInstanceToView.getFormInstanceFields().stream()
                .map(fif -> new FormInstanceFieldWrapper(fif))
                .collect(Collectors.toList());
    }

    public void submitFormInstance() {
        formInstanceFieldWrappers.stream()
                .filter(wrapper -> wrapper.getFormInstanceField().getFormFieldMapping().getIsRequired())
                .forEach(wrapper -> {
                    if (wrapper.getFormInstanceField().getFormInstanceFieldValues().isEmpty() || wrapper.getFormInstanceField().getFormInstanceFieldValues().get(0).getInputValue() == null
                            || wrapper.getFormInstanceField().getFormInstanceFieldValues().get(0).getInputValue().equals("")) {
                        wrapper.setErrorMessage("Question " + wrapper.getFormInstanceField().getFormFieldMapping().getPosition() + " cannot be left empty");
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, wrapper.getErrorMessage()));
                    } else {
                        wrapper.setErrorMessage(null);
                    }
                });
        Boolean errorPresent = formInstanceFieldWrappers.stream()
                .anyMatch(wrapper -> wrapper.getErrorMessage() != null);

        if (!errorPresent) {
//            try {
//                CALL SESSION BEAN
            System.out.println("NO ERROR PRESENT");
            PrimeFaces.current().executeScript("PF('dlgManageFormInstance').hide()");
//            } catch () {
//                
//            }
        } else {
            System.out.println("ERROR PRESENT");

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

}
