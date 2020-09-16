/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.managedBean;

import ejb.session.stateless.FormTemplateSessionBeanLocal;
import entity.FormField;
import entity.FormFieldOption;
import entity.FormTemplate;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import jsf.classes.FormFieldWrapper;
import util.enumeration.FormStatusEnum;
import util.enumeration.InputTypeEnum;
import util.exceptions.InputDataValidationException;
import util.exceptions.UnknownPersistenceException;
import util.security.CryptographicHelper;

@Named(value = "formUtilityManagedBean")
@ViewScoped
public class FormUtilityManagedBean implements Serializable {

    @EJB
    private FormTemplateSessionBeanLocal formTemplateSessionBeanLocal;

    private List<FormTemplate> formTemplates;

    private FormTemplate selectedForm;

    private List<FormFieldWrapper> selectedFormFieldWrappers = new ArrayList<>();

    private List<InputTypeEnum> inputTypes = new ArrayList<>();

    private String createFormName;

    private String newFormName;

    private boolean fieldsDisabled;

    public FormUtilityManagedBean() {
        inputTypes = Arrays.asList(InputTypeEnum.class.getEnumConstants());
    }

    @PostConstruct
    public void postConstruct() {

        formTemplates = formTemplateSessionBeanLocal.retrieveAllFormTemplates();
        selectedFormFieldWrappers = new ArrayList<>();
    }

    public void selectForm(ActionEvent event) {
        selectedForm = (FormTemplate) event.getComponent().getAttributes().get("formToView");
        setSelectedForm(selectedForm);
    }

    public void createForm() {
        System.out.println("Created form");
        try {
            if (this.createFormName != null || !this.createFormName.equals("")) {
                formTemplateSessionBeanLocal.createFormTemplate(new FormTemplate(this.createFormName));
                this.createFormName = "";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully created new form template!", "Database has been updated."));
                formTemplates = formTemplateSessionBeanLocal.retrieveAllFormTemplates();
            }

        } catch (InputDataValidationException | UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to create form template!", "Please check the supplied form template name."));

        }

    }

    public void publishForm(ActionEvent event) {

        Long id = (Long) event.getComponent().getAttributes().get("formIdToPublish");
        boolean success = formTemplateSessionBeanLocal.publishFormTemplate(id);
        if (success) {
            postConstruct();
            for (FormTemplate ft : formTemplates) {
                if (ft.getFormTemplateId() == id) {
                    setSelectedForm(ft);
                }
            }
        }

    }

    public void deleteForm(ActionEvent event) {

        Long id = (Long) event.getComponent().getAttributes().get("formIdToDelete");
        boolean success = formTemplateSessionBeanLocal.deleteFormTemplate(id);
        if (success) {
            postConstruct();
            for (FormTemplate ft : formTemplates) {
                if (ft.getFormTemplateId() == id) {
                    setSelectedForm(ft);
                }
            }
        }
    }

    public void archiveForm(ActionEvent event) {

        Long id = (Long) event.getComponent().getAttributes().get("formIdToArchive");
        boolean success = formTemplateSessionBeanLocal.archiveFormTemplate(id);
        if (success) {
            postConstruct();
            for (FormTemplate ft : formTemplates) {
                if (ft.getFormTemplateId() == id) {
                    setSelectedForm(ft);
                }
            }
        }
    }

    public void saveFormFields() {

        boolean emptyTitle = false;
        boolean emptyOptions = false;
        boolean duplicateTitle = false;
        HashSet<String> titleSet = new HashSet<>();

        List<FormField> newFormFields = new ArrayList<>();
        int index = 1;
        for (FormFieldWrapper ffw : selectedFormFieldWrappers) {

            if (ffw.getFormField().getTitle().equals("")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to save form template!", "Empty question detected!"));
                emptyTitle = true;
                break;
            } else if (titleSet.contains(ffw.getFormField().getTitle())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to save form template!", "Duplicate question detected!"));
                duplicateTitle = true;
                break;
            } else if (ffw.getHasInputOption() && ffw.getFormFieldOptions().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to save form template!", "Please ensure there is at least one option for applicable fields!"));
                emptyOptions = true;
                break;
            } else {
                titleSet.add(ffw.getFormField().getTitle());
            }

            FormField ff = ffw.getFormField();
            List<FormFieldOption> formFieldOptions = new ArrayList<>();
            for (String fieldOption : ffw.getFormFieldOptions()) {
                formFieldOptions.add(new FormFieldOption(fieldOption));
            }
            ff.setFormFieldOptions(formFieldOptions);
            ff.setPosition(index);
            newFormFields.add(ff);

        }

        if (!emptyTitle && !emptyOptions && !duplicateTitle) {
            selectedForm.setFormFields(newFormFields);
            selectedForm.setFormTemplateName(this.newFormName);
            formTemplateSessionBeanLocal.saveFormTemplate(selectedForm);

            formTemplates = formTemplateSessionBeanLocal.retrieveAllFormTemplates();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully saved form template!", "Database has been updated."));
        }

    }

    public void resetEditor() {
        for (FormTemplate ft : formTemplates) {
            if (ft.getFormTemplateId().equals(this.selectedForm.getFormTemplateId())) {
                setSelectedForm(ft);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Editor reset!", "Loaded last saved version of form template from the database."));
                break;
            }
        }
    }

    public List<FormTemplate> getFormTemplates() {
        return formTemplates;
    }

    public void setFormTemplates(List<FormTemplate> formTemplates) {
        this.formTemplates = formTemplates;
    }

    public FormTemplate getSelectedForm() {
        return selectedForm;
    }

    public List<FormFieldWrapper> getSelectedFormFieldWrappers() {
        return selectedFormFieldWrappers;
    }

    public void setSelectedFormFieldWrappers(List<FormFieldWrapper> selectedFormFieldWrappers) {
        this.selectedFormFieldWrappers = selectedFormFieldWrappers;
    }

    public List<InputTypeEnum> getInputTypes() {
        return inputTypes;
    }

    public void setInputTypes(List<InputTypeEnum> inputTypes) {
        this.inputTypes = inputTypes;
    }

    public String getCreateFormName() {
        return createFormName;
    }

    public void setCreateFormName(String createFormName) {
        this.createFormName = createFormName;
    }

    public String getNewFormName() {
        return newFormName;
    }

    public void setNewFormName(String newFormName) {
        this.newFormName = newFormName;
    }

    public boolean isFieldsDisabled() {
        return fieldsDisabled;
    }

    public void setFieldsDisabled(boolean fieldsDisabled) {
        this.fieldsDisabled = fieldsDisabled;
    }

    public void setSelectedForm(FormTemplate selectedForm) {
        this.selectedForm = formTemplateSessionBeanLocal.retrieveFormTemplate(selectedForm.getFormTemplateId());
        this.newFormName = selectedForm.getFormTemplateName();
        this.selectedFormFieldWrappers = new ArrayList<>();

        for (FormField ff : this.selectedForm.getFormFields()) {
            this.selectedFormFieldWrappers.add(new FormFieldWrapper(ff));
        }

        if (this.selectedForm.getFormStatus() == FormStatusEnum.DRAFT) {
            this.fieldsDisabled = false;
        } else {
            this.fieldsDisabled = true;
        }
    }

    public void addCurrentFormFieldWrapper() {
        FormField newff = new FormField();
        FormFieldWrapper newffw = new FormFieldWrapper(newff);
        this.selectedFormFieldWrappers.add(newffw);
    }

    public void removeFormFieldWrapper(ActionEvent event) {

        String fieldToDelete = (String) event.getComponent().getAttributes().get("formfieldWrapperToRemove");
        List<FormFieldWrapper> remainingFormFieldWrappers = new ArrayList<>();

        for (FormFieldWrapper ffw : selectedFormFieldWrappers) {
            if (!ffw.getFormFieldCode().equals(fieldToDelete)) {
                System.out.println(ffw.getFormField().getTitle());
                System.out.println(ffw.getFormFieldCode());

                remainingFormFieldWrappers.add(ffw);
            }
        }

        this.selectedFormFieldWrappers = remainingFormFieldWrappers;

    }

    public void formFieldWrapperSwapUp(ActionEvent event) {
        String fieldToSwap = (String) event.getComponent().getAttributes().get("formfieldWrapperToSwap");
        List<FormFieldWrapper> swappedFormFieldWrappers = new ArrayList<>();
        int position = 0;
        for (FormFieldWrapper ffw : selectedFormFieldWrappers) {
            if (ffw.getFormFieldCode().equals(fieldToSwap)) {
                if (position == 0) {
                    return;
                } else {
                    break;
                }
            }
            position++;
        }
        for (int i = 0; i < selectedFormFieldWrappers.size(); i++) {
            if (i != position) {
                if (i == position - 1) {
                    swappedFormFieldWrappers.add(selectedFormFieldWrappers.get(position));
                }
                swappedFormFieldWrappers.add(selectedFormFieldWrappers.get(i));
            }
        }
        this.selectedFormFieldWrappers = swappedFormFieldWrappers;

    }

    public void formFieldWrapperSwapDown(ActionEvent event) {
        String fieldToSwap = (String) event.getComponent().getAttributes().get("formfieldWrapperToSwap");
        List<FormFieldWrapper> swappedFormFieldWrappers = new ArrayList<>();
        int position = 0;
        for (FormFieldWrapper ffw : selectedFormFieldWrappers) {
            if (ffw.getFormFieldCode().equals(fieldToSwap)) {

                if (position == selectedFormFieldWrappers.size() - 1) {
                    return;
                } else {
                    break;
                }
            }
            position++;
        }
        for (int i = 0; i < selectedFormFieldWrappers.size(); i++) {
            if (i != position) {
                swappedFormFieldWrappers.add(selectedFormFieldWrappers.get(i));
                if (i == position + 1) {
                    swappedFormFieldWrappers.add(selectedFormFieldWrappers.get(position));
                }
            }

        }
        this.selectedFormFieldWrappers = swappedFormFieldWrappers;

    }

    public String renderDate(Date date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
        return dateFormat.format(date);
    }
    
}
