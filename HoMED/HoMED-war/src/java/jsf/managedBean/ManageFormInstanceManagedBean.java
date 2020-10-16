package jsf.managedBean;

import ejb.session.stateless.FormInstanceSessionBeanLocal;
import entity.FormInstance;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named(value = "manageFormInstanceManagedBean")
@ViewScoped
public class ManageFormInstanceManagedBean implements Serializable {

    //States
    private Boolean isViewState;
    private Boolean isManageState;

    private FormInstance formInstanceToView;

    public ManageFormInstanceManagedBean() {
        this.formInstanceToView = new FormInstance();
    }

    @PostConstruct
    public void postConstruct() {
    }

    public void init() {
        isManageState = false;
        isViewState = false;
    }

    public void initView() {
        init();
        isViewState = true;
        System.out.println("INIT VIEW");
        formInstanceToView.getFormInstanceFields().stream()
                .forEach(x -> x.getFormInstanceFieldValues().stream().forEach(y-> System.out.print(y.getInputValue())));
    }

    public void initManage() {
        init();
        isManageState = true;
        System.out.println("INIT MANAGE");
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

}
