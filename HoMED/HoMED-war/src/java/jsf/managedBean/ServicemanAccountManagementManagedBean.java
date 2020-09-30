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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.model.file.UploadedFile;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import jsf.classes.ServicemanWrapper;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import util.enumeration.BloodTypeEnum;
import util.enumeration.GenderEnum;
import util.exceptions.ServicemanNotFoundException;

@Named(value = "servicemanAccountManagementManagedBean")
@ViewScoped
public class ServicemanAccountManagementManagedBean implements Serializable {

    @EJB
    private ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    @Inject
    private ManageServicemanManagedBean manageServicemanManagedBean;

    private List<Serviceman> servicemen;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    private final List<String> dateFormats;
    private List<ServicemanWrapper> servicemanWrappers;
    private Boolean isUploaded;

    public ServicemanAccountManagementManagedBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();

        this.dateFormats = Arrays.asList("d-M-yy", "d/M/yy");
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
        this.servicemanWrappers = new ArrayList<>();
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

                    ServicemanWrapper servicemanWrapper = new ServicemanWrapper();
                    this.servicemanWrappers.add(servicemanWrapper);

                    validateServiceman(serviceman, servicemanWrapper);
                    verifyExistingServiceman(serviceman, servicemanWrapper);
                }

                this.isUploaded = Boolean.TRUE;
            } catch (IOException ex) {
                ex.printStackTrace();
                printUnexpectedErrorMessage();
            }
        } else {
            printUnexpectedErrorMessage();
        }
    }

    private void validateServiceman(String[] serviceman, ServicemanWrapper servicemanWrapper) {
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

        System.out.println("======================================================================");
        System.out.println("Name = " + name + "; Gender = " + gender + "; Phone = " + phone + "; Email = " + email
                + "; Address (" + streetName + ", " + unitNumber + ", " + buildingName + ", " + country + ", " + postalCode
                + "); Blood Type = " + bloodType + "; ROD = " + rod);
        System.out.println("======================================================================");

        Serviceman newServiceman = new Serviceman();
        Address address = new Address(streetName, unitNumber, buildingName, country, postalCode);
        
        newServiceman.setName(name);
        newServiceman.setEmail(email);
        newServiceman.setPhoneNumber(phone);
        newServiceman.setAddress(address);
        
        servicemanWrapper.setNewServiceman(newServiceman);
        
        // Gender
        if (gender.toUpperCase().equals("MALE") || gender.toUpperCase().equals("FEMALE")) {
            newServiceman.setGender(GenderEnum.valueOf(gender.toUpperCase()));
        }
        // Blood Type
        if (BloodTypeEnum.valueOfLabel(bloodType) != null) {
            newServiceman.setBloodType(BloodTypeEnum.valueOfLabel(bloodType));
        }
        // ROD
        for (String df : dateFormats) {
            try {
                newServiceman.setRod(new SimpleDateFormat(df).parse(rod));
                break;
            } catch (ParseException e) {
            }
        }

        List<String> validationErrorMessages = servicemanWrapper.getErrorMessages();

        Set<ConstraintViolation<Object>> servicemanConstraintViolations = validator.validate(newServiceman);
        if (!servicemanConstraintViolations.isEmpty()) {
            System.out.println("----- Serviceman -----");

            for (String errorMsg : prepareInputDataValidationErrorsMessage(servicemanConstraintViolations)) {
                System.out.println(errorMsg);

                if (errorMsg.contains("Name")) {
                    validationErrorMessages.add(0, "Incorrect name format [" + name + "]. " + errorMsg);
                } else if (errorMsg.contains("Gender")) {
                    validationErrorMessages.add(1, "Incorrect gender format [" + gender + "]. Please change it to MALE/FEMALE.");
                } else if (errorMsg.contains("Phone")) {
                    validationErrorMessages.add(2, "Incorrect phone format [" + phone + "]. " + errorMsg);
                } else if (errorMsg.contains("Email")) {
                    validationErrorMessages.add(3, "Incorrect email format [" + email + "]. " + errorMsg);
                } else if (errorMsg.contains("Blood type")) {
                    validationErrorMessages.add(9, "Incorrect blood type format [" + bloodType + "]. Please change it to A-/A+/B-/B+/AB-/AB+/O-/O+.");
                } else if (errorMsg.contains("ROD")) {
                    validationErrorMessages.add(10, "Incorrect ROD date format [" + rod + "]. Please change it to \"DD/MM/YYYY\" or \"DD-MM-YYYY\".");
                }
            }
        }

        Set<ConstraintViolation<Object>> addressConstraintViolations = validator.validate(address);
        if (!addressConstraintViolations.isEmpty()) {
            System.out.println("----- Address -----");

            for (String errorMsg : prepareInputDataValidationErrorsMessage(addressConstraintViolations)) {
                System.out.println(errorMsg);
                
                if (errorMsg.contains("Street Name")) {
                    validationErrorMessages.add(4, "Incorrect street name format [" + streetName + "]. " + errorMsg);
                } else if (errorMsg.contains("Postal Code")) {
                    validationErrorMessages.add(8, "Incorrect postal code format [" + postalCode + "]. " + errorMsg);
                }
            }
        }
    }

    private void verifyExistingServiceman(String[] serviceman, ServicemanWrapper servicemanWrapper) {
        String email = serviceman[3];

        try {
            servicemanWrapper.setExistingServiceman(servicemanSessionBeanLocal.retrieveServicemanByEmail(email));
        } catch (ServicemanNotFoundException ex) {
            servicemanWrapper.setExistingServiceman(null);
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

    public Boolean getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(Boolean isUploaded) {
        this.isUploaded = isUploaded;
    }

    private List<String> prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Object>> constraintViolations) {
        List<String> msgs = new ArrayList<>();
//        String msg = "";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msgs.add(constraintViolation.getMessage());
//            msg += constraintViolation.getMessage() + "\n";
        }

//        msg = msg.substring(0, msg.length() - 1);
        return msgs;
//        return msg;
    }

}
