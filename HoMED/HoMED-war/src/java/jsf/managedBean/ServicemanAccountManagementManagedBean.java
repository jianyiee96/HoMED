package jsf.managedBean;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.ServicemanSessionBeanLocal;
import entity.Address;
import entity.Employee;
import entity.Serviceman;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
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
import util.enumeration.PesStatusEnum;
import util.enumeration.ServicemanRoleEnum;
import util.exceptions.CreateServicemanException;
import util.exceptions.EmployeeNotFoundException;
import util.exceptions.ServicemanNotFoundException;
import util.exceptions.UpdateServicemanException;

@Named(value = "servicemanAccountManagementManagedBean")
@ViewScoped
public class ServicemanAccountManagementManagedBean implements Serializable {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;
    @EJB
    private ServicemanSessionBeanLocal servicemanSessionBeanLocal;

    @Inject
    private ManageServicemanManagedBean manageServicemanManagedBean;

    private List<Serviceman> servicemen;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    private Boolean isUploaded;
    private final List<String> dateFormats;
    private List<ServicemanWrapper> servicemanWrappers;
    private List<Serviceman> servicemenToCreate;
    private List<Serviceman> servicemenToUpdate;

    private Boolean isSelectAll;
    private Boolean isHideAll;

    public ServicemanAccountManagementManagedBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();

        this.dateFormats = Arrays.asList("d-M-yy", "d/M/yy");

        this.isSelectAll = Boolean.FALSE;
        this.isHideAll = Boolean.FALSE;
    }

    @PostConstruct
    public void postConstruct() {
        this.servicemen = servicemanSessionBeanLocal.retrieveAllServicemen();
    }

    public void dialogActionListener() {
        this.servicemen = servicemanSessionBeanLocal.retrieveAllServicemen();
        PrimeFaces.current().ajax().update("formAllServicemen:dataTableServicemen");
    }

    public void initBulkImport() {
        this.isUploaded = Boolean.FALSE;
        this.servicemanWrappers = new ArrayList<>();
        this.servicemenToCreate = new ArrayList<>();
        this.servicemenToUpdate = new ArrayList<>();

        this.isHideAll = Boolean.FALSE;
        this.isSelectAll = Boolean.FALSE;
    }

    public void uploadCsv(FileUploadEvent event) {
        this.initBulkImport();
        UploadedFile csv = event.getFile();

        if (csv != null) {
            try {
                InputStream csvInputStream = csv.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(csvInputStream));

                String line = "";
                Integer idx = 1;
                while (reader.ready() && (line = reader.readLine()) != null) {
                    String[] serviceman = line.split(",");

                    ServicemanWrapper servicemanWrapper = new ServicemanWrapper();
                    validateServicemanByImport(serviceman, servicemanWrapper);
                    checkExistingServiceman(servicemanWrapper);
                    checkExistingEmployee(servicemanWrapper);

//                    String email = serviceman[3];
//                    try {
//                        Serviceman existingServiceman = servicemanSessionBeanLocal.retrieveServicemanByEmail(email);
//                        servicemanWrapper.setExistingServiceman(existingServiceman);
//                        servicemanWrapper.getNewServiceman().setServicemanId(existingServiceman.getServicemanId());
//                        servicemanWrapper.getNewServiceman().setPassword(existingServiceman.getPassword());
//                    } catch (ServicemanNotFoundException ex) {
//                        servicemanWrapper.setExistingServiceman(null);
//                    }
                }

                this.isUploaded = Boolean.TRUE;
            } catch (IOException ex) {
                ex.printStackTrace();
                printUnexpectedErrorMessage("Please contact IT admins for assistance!");
            } catch (ArrayIndexOutOfBoundsException ex) {
                ex.printStackTrace();
                printUnexpectedErrorMessage("Please make sure the CSV file is in the correct format!");
            }
        } else {
            printUnexpectedErrorMessage("Please contact IT admins for assistance!");
        }
    }

    private void validateServicemanByImport(String[] serviceman, ServicemanWrapper servicemanWrapper) throws ArrayIndexOutOfBoundsException {
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
        String role = serviceman[11];
        String pes = serviceman[12];

        Serviceman newServiceman = new Serviceman();
        newServiceman.setName(name);
        newServiceman.setEmail(email);
        newServiceman.setPhoneNumber(phone);
        if (gender.toUpperCase().equals("MALE") || gender.toUpperCase().equals("FEMALE")) {
            newServiceman.setGender(GenderEnum.valueOf(gender.toUpperCase()));
        }
        if (role.toUpperCase().equals("NSF") || role.toUpperCase().equals("NSMEN") || role.toUpperCase().equals("REGULAR") || role.toUpperCase().equals("OTHERS")) {
            newServiceman.setRole(ServicemanRoleEnum.valueOf(role.toUpperCase()));
        }
        if (pes.toUpperCase().equals("A") || pes.toUpperCase().equals("B1") || pes.toUpperCase().equals("B2") || pes.toUpperCase().equals("B3")
                || pes.toUpperCase().equals("B4") || pes.toUpperCase().equals("BP") || pes.toUpperCase().equals("C2") || pes.toUpperCase().equals("C9")
                || pes.toUpperCase().equals("E1") || pes.toUpperCase().equals("E9") || pes.toUpperCase().equals("F")) {
            newServiceman.setPesStatus(PesStatusEnum.valueOf(pes.toUpperCase()));
        }
        if (BloodTypeEnum.valueOfLabel(bloodType) != null) {
            newServiceman.setBloodType(BloodTypeEnum.valueOfLabel(bloodType));
        }
        for (String df : dateFormats) {
            try {
                newServiceman.setRod(new SimpleDateFormat(df).parse(rod));
                break;
            } catch (ParseException e) {
            }
        }

        Address address = new Address(streetName, unitNumber, buildingName, country, postalCode);
        newServiceman.setAddress(address);
        servicemanWrapper.setNewServiceman(newServiceman);

        List<String> validationErrorMessages = servicemanWrapper.getErrorMessages();

        Set<ConstraintViolation<Object>> servicemanConstraintViolations = validator.validate(newServiceman);
        if (!servicemanConstraintViolations.isEmpty()) {
            for (String errorMsg : prepareInputDataValidationErrorsMessage(servicemanConstraintViolations)) {
                if (errorMsg.contains("Name")) {
                    addErrorMessage(validationErrorMessages, 0, "name", name, errorMsg);
                    servicemanWrapper.setIsValid(Boolean.FALSE);
                } else if (errorMsg.contains("Gender")) {
                    addErrorMessage(validationErrorMessages, 1, "gender", gender, "Please change it to MALE/FEMALE.");
                    servicemanWrapper.setIsValid(Boolean.FALSE);
                } else if (errorMsg.contains("Phone")) {
                    addErrorMessage(validationErrorMessages, 2, "phone", phone, errorMsg);
                    servicemanWrapper.setIsValid(Boolean.FALSE);
                } else if (errorMsg.contains("Email")) {
                    addErrorMessage(validationErrorMessages, 3, "email", email, errorMsg);
                    servicemanWrapper.setIsValid(Boolean.FALSE);
                } else if (errorMsg.contains("Blood type")) {
                    addErrorMessage(validationErrorMessages, 9, "blood type", bloodType, "Please change it to A-/A+/B-/B+/AB-/AB+/O-/O+.");
                    servicemanWrapper.setIsValid(Boolean.FALSE);
                } else if (errorMsg.contains("ROD")) {
                    addErrorMessage(validationErrorMessages, 10, "ROD", rod, "Please change it to \"DD/MM/YYYY\" or \"DD-MM-YYYY\".");
                    servicemanWrapper.setIsValid(Boolean.FALSE);
                } else if (errorMsg.contains("Role")) {
                    addErrorMessage(validationErrorMessages, 11, "role", role, "Please change it to REGULAR/NSF/NSMEN/OTHERS");
                    servicemanWrapper.setIsValid(Boolean.FALSE);
                } else if (errorMsg.contains("Pes")) {
                    addErrorMessage(validationErrorMessages, 12, "pes status", pes, "Please change it to A/B1/B2/B3/B4/BP/C2/C9/E1/E9/F");
                    servicemanWrapper.setIsValid(Boolean.FALSE);
                }
            }
        }

        Set<ConstraintViolation<Object>> addressConstraintViolations = validator.validate(address);
        if (!addressConstraintViolations.isEmpty()) {
            for (String errorMsg : prepareInputDataValidationErrorsMessage(addressConstraintViolations)) {
                if (errorMsg.contains("Street Name")) {
                    addErrorMessage(validationErrorMessages, 4, "street name", streetName, errorMsg);
                    servicemanWrapper.setIsValid(Boolean.FALSE);
                } else if (errorMsg.contains("Postal Code")) {
                    addErrorMessage(validationErrorMessages, 8, "postal code", postalCode, errorMsg);
                    servicemanWrapper.setIsValid(Boolean.FALSE);
                }
            }
        }

        validateDuplicateServicemanEmailOnImport(servicemanWrapper);
        this.servicemanWrappers.add(servicemanWrapper);
    }

    private void validateDuplicateServicemanEmailOnImport(ServicemanWrapper servicemanWrapper) {
        List<String> validationErrorMessages = servicemanWrapper.getErrorMessages();

        for (ServicemanWrapper sw : this.servicemanWrappers) {
            String existingEmail = sw.getNewServiceman().getEmail();
            String incomingEmail = servicemanWrapper.getNewServiceman().getEmail();
            if (sw != servicemanWrapper && existingEmail.equals(incomingEmail)) {
                servicemanWrapper.setIsValid(Boolean.FALSE);
                servicemanWrapper.setIsDuplicate(Boolean.TRUE);
                addErrorMessage(validationErrorMessages, 3, "email", existingEmail, "Duplicate email(s) detected!");
                return;
            }
        }
    }

    private void addErrorMessage(List<String> validationErrorMessages, int errorMsgIdx, String errorType, String inputtedValue, String errorMsg) {
        String curr = validationErrorMessages.get(errorMsgIdx);
        validationErrorMessages.remove(errorMsgIdx);
        if (curr != null) {
            validationErrorMessages.add(errorMsgIdx, (curr + "\nIncorrect " + errorType + " format [" + inputtedValue + "]. " + errorMsg));
        } else {
            validationErrorMessages.add(errorMsgIdx, "Incorrect " + errorType + " format [" + inputtedValue + "]. " + errorMsg);
        }
    }

    public void enterEditMode(ServicemanWrapper servicemanWrapper) {
        for (ServicemanWrapper sw : this.servicemanWrappers) {
            sw.setIsEditable(Boolean.FALSE);
        }

        servicemanWrapper.setNewServicemanClone(new Serviceman(servicemanWrapper.getNewServiceman()));
    }

    public void validateDuplicateServicemanOnEdit(ServicemanWrapper servicemanWrapper) {
        checkExistingServiceman(servicemanWrapper);
        checkExistingEmployee(servicemanWrapper);

        for (ServicemanWrapper sw : this.servicemanWrappers) {
            String existingEmail = sw.getNewServiceman().getEmail();
            String incomingEmail = servicemanWrapper.getNewServiceman().getEmail();
            if (sw != servicemanWrapper && !sw.getIsDuplicate() && existingEmail.equals(incomingEmail)) {
                servicemanWrapper.setIsDuplicate(Boolean.TRUE);
                return;
            }
        }

        servicemanWrapper.setIsDuplicate(Boolean.FALSE);
    }

    public void doEditServiceman(ServicemanWrapper servicemanWrapper) {
        for (int i = 0; i < servicemanWrapper.getErrorMessages().size(); i++) {
            servicemanWrapper.getErrorMessages().set(i, null);
        }

        servicemanWrapper.setIsValid(Boolean.TRUE);
        servicemanWrapper.setIsDuplicate(Boolean.FALSE);

        // To make all servicemen editable once can save edit.
        for (ServicemanWrapper sw : servicemanWrappers) {
            sw.setIsEditable(Boolean.TRUE);
        }

        String email = servicemanWrapper.getNewServicemanClone().getEmail();

        for (int i = 0; servicemanWrappers.size() > i; i++) {
            ServicemanWrapper currLoopingServicemanWrapper = servicemanWrappers.get(i);

            // 1st occurrence of duplicates
            if (servicemanWrapper != currLoopingServicemanWrapper && email.equals(currLoopingServicemanWrapper.getNewServiceman().getEmail())) {
                // If the email is modified to be different
                if (!servicemanWrapper.getNewServiceman().getEmail().equals(email)) {
                    currLoopingServicemanWrapper.setIsDuplicate(Boolean.FALSE);
                    currLoopingServicemanWrapper.getErrorMessages().set(3, null);

                    for (String errorMsg : currLoopingServicemanWrapper.getErrorMessages()) {
                        if (errorMsg != null) {
                            return;
                        }
                    }

                    currLoopingServicemanWrapper.setIsValid(Boolean.TRUE);
                    return;
                }
            }
        }
    }

    public void resetServiceman(ServicemanWrapper servicemanWrapper) {
        for (ServicemanWrapper sw : this.servicemanWrappers) {
            sw.setIsEditable(Boolean.TRUE);
        }

        servicemanWrapper.setNewServiceman(new Serviceman(servicemanWrapper.getNewServicemanClone()));
        validateDuplicateServicemanOnEdit(servicemanWrapper);
    }

    private void checkExistingServiceman(ServicemanWrapper servicemanWrapper) {
        try {
            Serviceman existingServiceman = servicemanSessionBeanLocal.retrieveServicemanByEmail(servicemanWrapper.getNewServiceman().getEmail());
            servicemanWrapper.setExistingServiceman(existingServiceman);
            servicemanWrapper.getNewServiceman().setServicemanId(existingServiceman.getServicemanId());
            servicemanWrapper.getNewServiceman().setPassword(existingServiceman.getPassword());
        } catch (ServicemanNotFoundException ex) {
            servicemanWrapper.setExistingServiceman(null);
        }
    }

    private void checkExistingEmployee(ServicemanWrapper servicemanWrapper) {
        try {
            Employee existingEmployee = employeeSessionBeanLocal.retrieveEmployeeByEmail(servicemanWrapper.getNewServiceman().getEmail());
            servicemanWrapper.setExistingEmployee(existingEmployee);
        } catch (EmployeeNotFoundException ex) {
            servicemanWrapper.setExistingEmployee(null);
        }
    }

    public void copyEmployeeDetails(ServicemanWrapper servicemanWrapper) {
        removeErrorMessagesByEdit(servicemanWrapper);
        Serviceman serviceman = servicemanWrapper.getNewServiceman();
        Employee employee = servicemanWrapper.getExistingEmployee();

        serviceman.setName(employee.getName());
        serviceman.setGender(employee.getGender());
        serviceman.setPhoneNumber(employee.getPhoneNumber());
        serviceman.setAddress(employee.getAddress());
    }

    public void removeErrorMessagesByEdit(ServicemanWrapper servicemanWrapper) {
        for (int i = 0; i < servicemanWrapper.getErrorMessages().size(); i++) {
            servicemanWrapper.getErrorMessages().set(i, null);
        }
    }

    public void hideAllInvalidServicemen() {
        if (isHideAll) {
            for (ServicemanWrapper sw : servicemanWrappers) {
                if (!sw.getIsValid()) {
                    sw.setIsHidden(Boolean.TRUE);
                }
            }
        } else {
            for (ServicemanWrapper sw : servicemanWrappers) {
                if (!sw.getIsValid()) {
                    sw.setIsHidden(Boolean.FALSE);
                }
            }
        }
    }

    public void selectServicemanToHide() {
        for (ServicemanWrapper sw : servicemanWrappers) {
            if (!sw.getIsValid() && !sw.getIsHidden()) {
                isHideAll = Boolean.FALSE;
                return;
            }
        }
        isHideAll = Boolean.TRUE;
    }

    public void selectAllValidServicemenToImport() {
        if (isSelectAll) {
            servicemenToCreate.clear();
            servicemenToUpdate.clear();
            for (ServicemanWrapper sw : servicemanWrappers) {
                if (sw.getIsValid()) {
                    sw.setIsSelected(Boolean.TRUE);
                    selectServicemanToImport(sw, Boolean.FALSE);
                }
            }
        } else {
            for (ServicemanWrapper sw : servicemanWrappers) {
                if (sw.getIsValid()) {
                    sw.setIsSelected(Boolean.FALSE);
                    selectServicemanToImport(sw, Boolean.FALSE);
                }
            }
        }
    }

    public void selectServicemanToImport(ServicemanWrapper servicemanWrapper, Boolean checkSelectAll) {
        if (servicemanWrapper.getIsSelected()) {
            if (servicemanWrapper.getExistingServiceman() == null) {
                servicemenToCreate.add(servicemanWrapper.getNewServiceman());
            } else {
                servicemenToUpdate.add(servicemanWrapper.getNewServiceman());
            }
        } else {
            servicemenToCreate.remove(servicemanWrapper.getNewServiceman());
            servicemenToUpdate.remove(servicemanWrapper.getNewServiceman());
        }

        if (checkSelectAll) {
            for (ServicemanWrapper sw : servicemanWrappers) {
                if (sw.getIsValid() && !sw.getIsSelected()) {
                    isSelectAll = Boolean.FALSE;
                    return;
                }
            }
            isSelectAll = Boolean.TRUE;
        }
    }

    public void importSelectedServicemen() {
        for (Serviceman serviceman : servicemenToCreate) {
            try {
                servicemanSessionBeanLocal.createServiceman(serviceman);
            } catch (CreateServicemanException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        serviceman.getEmail(), "An unexpected error has occurred while importing servicemen in bulk create. " + ex.getMessage()));
            }
        }

        for (Serviceman serviceman : servicemenToUpdate) {
            try {
                servicemanSessionBeanLocal.updateServiceman(serviceman);
            } catch (UpdateServicemanException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        serviceman.getEmail(), "An unexpected error has occurred while importing servicemen in bulk update. " + ex.getMessage()));
            }
        }

        this.servicemen = servicemanSessionBeanLocal.retrieveAllServicemen();
    }

    private void printUnexpectedErrorMessage(String errorMessage) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Bulk Import",
                        "An unexpected error has occurred while importing servicemen in bulk. " + errorMessage
                )
        );
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

    public List<Serviceman> getServicemenToCreate() {
        return servicemenToCreate;
    }

    public void setServicemenToCreate(List<Serviceman> servicemenToCreate) {
        this.servicemenToCreate = servicemenToCreate;
    }

    public List<Serviceman> getServicemenToUpdate() {
        return servicemenToUpdate;
    }

    public void setServicemenToUpdate(List<Serviceman> servicemenToUpdate) {
        this.servicemenToUpdate = servicemenToUpdate;
    }

    public ManageServicemanManagedBean getManageServicemanManagedBean() {
        return manageServicemanManagedBean;
    }

    public void setManageServicemanManagedBean(ManageServicemanManagedBean manageServicemanManagedBean) {
        this.manageServicemanManagedBean = manageServicemanManagedBean;
    }

    public String renderDate(Date date) {
        if (date == null) {
            return null;
        }

        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public GenderEnum[] getGenders() {
        return GenderEnum.values();
    }

    public ServicemanRoleEnum[] getServicemanRoles() {
        return ServicemanRoleEnum.values();
    }

    public BloodTypeEnum[] getBloodTypes() {
        return BloodTypeEnum.values();
    }

    public PesStatusEnum[] getPesStatuses() {
        return PesStatusEnum.values();
    }

    public Boolean getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(Boolean isUploaded) {
        this.isUploaded = isUploaded;
    }

    private List<String> prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Object>> constraintViolations) {
        List<String> msgs = new ArrayList<>();

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msgs.add(constraintViolation.getMessage());
        }

        return msgs;
    }

    public Boolean getIsSelectAll() {
        return isSelectAll;
    }

    public void setIsSelectAll(Boolean isSelectAll) {
        this.isSelectAll = isSelectAll;
    }

    public Boolean getIsHideAll() {
        return isHideAll;
    }

    public void setIsHideAll(Boolean isHideAll) {
        this.isHideAll = isHideAll;
    }
}
