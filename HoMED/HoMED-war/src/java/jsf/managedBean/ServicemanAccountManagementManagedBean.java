package jsf.managedBean;

import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Address;
import entity.Serviceman;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import jsf.classes.ServicemanWrapper;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import util.enumeration.BloodTypeEnum;
import util.enumeration.GenderEnum;

@Named(value = "servicemanAccountManagementManagedBean")
@ViewScoped
public class ServicemanAccountManagementManagedBean implements Serializable {

    @EJB
    private ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    @Inject
    private ManageServicemanManagedBean manageServicemanManagedBean;

    private List<Serviceman> servicemen;

    private List<ServicemanWrapper> servicemanWrappers;

    public ServicemanAccountManagementManagedBean() {
        this.servicemanWrappers = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        servicemen = servicemanSessionBeanLocal.retrieveAllServicemen();
    }

    public void dialogActionListener() {
        this.servicemen = servicemanSessionBeanLocal.retrieveAllServicemen();
        PrimeFaces.current().ajax().update("formAllServicemen:dataTableServicemen");
    }

    public void uploadCsv(FileUploadEvent event) {
        System.out.println("Uploading...");
        UploadedFile csv = event.getFile();

        if (csv != null) {
            System.out.println("Uploaded file --> " + csv.getFileName() + " Size --> " + csv.getSize() + " ContentType --> " + csv.getContentType());

            try {
                InputStream csvInputStream = csv.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(csvInputStream));

                String line = "";
                Integer idx = 1;
                while (reader.ready() && (line = reader.readLine()) != null) {
                    String[] serviceman = line.split(",");
                    String name = serviceman[0];
                    String gender = serviceman[1];
                    String phone = serviceman[2];
                    String email = serviceman[3];
                    String streetName = serviceman[4];
                    String unitNumber = serviceman[5];
                    String buildingName = serviceman[6];
                    String country = serviceman[7];
                    String postalCode = serviceman[8];
                    String bloodType = serviceman[9];
                    String rod = serviceman[10];

                    System.out.println("Name = " + name + "; Gender = " + gender + "; Phone = " + phone + "; Email = " + email
                            + "; Address (" + streetName + ", " + unitNumber + ", " + buildingName + ", " + country + ", " + postalCode
                            + "); Blood Type = " + bloodType + "; ROD = " + rod);

                    ServicemanWrapper servicemanWrapper = new ServicemanWrapper();
                    this.servicemanWrappers.add(servicemanWrapper);

                    Serviceman newServiceman = new Serviceman();
                    servicemanWrapper.setNewServiceman(newServiceman);

                    // Name
                    newServiceman.setName(name);

                    // Gender
                    if (gender.toUpperCase().equals("MALE") || gender.toUpperCase().equals("FEMALE")) {
                        newServiceman.setGender(GenderEnum.valueOf(gender.toUpperCase()));
                    } else {
                        servicemanWrapper.getErrorMessages().add(1, "Incorrect gender input (" + gender + "). Please change it to MALE/FEMALE.");
//                        facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
//                        facesMessage.setSummary("Bulk Import");
//                        facesMessage.setDetail("[Line " + idx + " - " + name + "] has incorrect gender input (" + gender + ")! Please modify it to MALE/FEMALE.");
//                        facesContext.addMessage("growl-message", facesMessage);
                    }

                    // Phone
                    newServiceman.setPhoneNumber(phone);
                    // Email
                    newServiceman.setEmail(email);
                    // Address
                    Address address = new Address(streetName, unitNumber, buildingName, country, postalCode);
                    // Blood Type
                    BloodTypeEnum bloodTypeEnum = BloodTypeEnum.valueOfLabel(bloodType);
                    if (bloodTypeEnum != null) {
                        newServiceman.setBloodType(bloodTypeEnum);
                    } else {
                        servicemanWrapper.getErrorMessages().add(9, "Incorrect blood type input (" + bloodType + "). Please change it to A-/A+/B-/B+/AB-/AB+/O-/O+.");
                    }
                    // ROD
                    try {
                        Date rodDate = new Date(rod);
                        newServiceman.setRod(rodDate);
                    } catch (IllegalArgumentException ex) {
                        servicemanWrapper.getErrorMessages().add(9, "Incorrect ROD date input (" + rod + "). Please change it to \"YYYY/MM/DD\".");
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                printUnexpectedErrorMessage();
            }
        } else {
            printUnexpectedErrorMessage();
        }
    }

    private void printUnexpectedErrorMessage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage = new FacesMessage();

        facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
        facesMessage.setSummary("Bulk Import");
        facesMessage.setDetail("An unexpected error has occurred while creating servicemen in bulk. Please contact IT admins for assistance!");
        facesContext.addMessage("growl-message", facesMessage);
    }

    public List<Serviceman> getServicemen() {
        return servicemen;
    }

    public void setServicemen(List<Serviceman> servicemen) {
        this.servicemen = servicemen;
    }

    public List<ServicemanWrapper> getServicemanWrappers() {
        return servicemanWrappers;
    }

    public void setServicemanWrappers(List<ServicemanWrapper> servicemanWrappers) {
        this.servicemanWrappers = servicemanWrappers;
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

    public GenderEnum[] getGenders() {
        return GenderEnum.values();
    }

    public BloodTypeEnum[] getBloodTypes() {
        return BloodTypeEnum.values();
    }

}
