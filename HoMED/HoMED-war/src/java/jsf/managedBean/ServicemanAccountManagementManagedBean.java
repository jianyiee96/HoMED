/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Serviceman;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.model.file.UploadedFile;
import javax.inject.Inject;
import org.primefaces.PrimeFaces;
import util.enumeration.BloodTypeEnum;
import util.enumeration.GenderEnum;
import util.exceptions.CreateServicemanException;

@Named(value = "servicemanAccountManagementManagedBean")
@ViewScoped
public class ServicemanAccountManagementManagedBean implements Serializable {

    @EJB
    private ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    @Inject
    private ManageServicemanManagedBean manageServicemanManagedBean;

    private List<Serviceman> servicemen;

    private UploadedFile csvFile;
    private Serviceman servicemanToCreate;
    private Boolean isEditableCreateServiceman;

    public ServicemanAccountManagementManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        servicemen = servicemanSessionBeanLocal.retrieveAllServicemen();
    }

    public void dialogActionListener() {
        this.servicemen = servicemanSessionBeanLocal.retrieveAllServicemen();
        PrimeFaces.current().ajax().update("formAllServicemen:dataTableServicemen");
    }

    public void doViewServiceman(Serviceman serviceman) {
        this.manageServicemanManagedBean.setServicemanToView(serviceman);
        this.manageServicemanManagedBean.init();
    }

    public void doCreateServiceman() {
        this.servicemanToCreate = new Serviceman();
        this.isEditableCreateServiceman = true;
    }

    public void createServiceman() {
        try {
            servicemanSessionBeanLocal.createServiceman(servicemanToCreate);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully created serviceman! Please inform serviceman that OTP has been sent to his/her email.", null));
            this.isEditableCreateServiceman = false;
            this.servicemen = servicemanSessionBeanLocal.retrieveAllServicemen();
        } catch (CreateServicemanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    public List<Serviceman> getServicemen() {
        return servicemen;
    }

    public void setServicemen(List<Serviceman> servicemen) {
        this.servicemen = servicemen;
    }

    public ManageServicemanManagedBean getManageServicemanManagedBean() {
        return manageServicemanManagedBean;
    }

    public void setManageServicemanManagedBean(ManageServicemanManagedBean manageServicemanManagedBean) {
        this.manageServicemanManagedBean = manageServicemanManagedBean;
    }

    public String renderDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
    
    public UploadedFile getCsvFile() {
        System.out.println("getting CSV File");
        return csvFile;
    }

    public void setCsvFile(UploadedFile csvFile) {
        System.out.println("Setting csv file");
        
        this.csvFile = csvFile;
    }

    public void uploadCsv() {
        System.out.println("Uploading...");

//        System.out.println(csvFile);

        if (csvFile != null) {
            System.out.println("Uploaded file --> " + csvFile.getFileName() + " Size --> " + csvFile.getSize() + " ContentType --> " + csvFile.getContentType());

            FacesMessage message = new FacesMessage("Successful", csvFile.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage("growl", message);
        }
    }
    
    public Serviceman getServicemanToCreate() {
        return servicemanToCreate;
    }

    public void setServicemanToCreate(Serviceman servicemanToCreate) {
        this.servicemanToCreate = servicemanToCreate;
    }

    public GenderEnum[] getGenders() {
        return GenderEnum.values();
    }

    public BloodTypeEnum[] getBloodTypes() {
        return BloodTypeEnum.values();
    }

    public Boolean getIsEditableCreateServiceman() {
        return isEditableCreateServiceman;
    }

    public void setIsEditableCreateServiceman(Boolean isEditableCreateServiceman) {
        this.isEditableCreateServiceman = isEditableCreateServiceman;
    }

}
