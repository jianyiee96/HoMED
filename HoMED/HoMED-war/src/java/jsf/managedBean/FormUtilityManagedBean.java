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
import java.util.Collections;
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
import org.primefaces.PrimeFaces;
import util.enumeration.FormStatusEnum;
import util.enumeration.InputTypeEnum;

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

    private boolean newFormIsPublic;

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

        } catch (Exception ex) { //Create form template exception TODO: Add a crete form exception
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
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to publish form template!", "Please ensure that the form template has at least 1 form field."));
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

    public void invokeSaveFormFields() {
        saveFormFields();
    }

    public boolean saveFormFields() {

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
            index++;
        }

        if (!emptyTitle && !emptyOptions && !duplicateTitle) {
            selectedForm.setFormFields(newFormFields);
            selectedForm.setFormTemplateName(this.newFormName);
            formTemplateSessionBeanLocal.saveFormTemplate(selectedForm);

            formTemplates = formTemplateSessionBeanLocal.retrieveAllFormTemplates();
            System.out.println("saved form template: ");
            System.out.println(selectedForm.getFormTemplateId());

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully saved form template!", "Database has been updated."));
            return true;
        } else {
            return false;
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

    public void cloneCurrentForm(boolean save) {
        boolean success = true;
        if (save) {
            success = saveFormFields();
            if (!success) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Unable to Clone!", "We are unable to save your form."));
            }
        }

        if (success) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Clone", "Form template is cloned!"));
            formTemplateSessionBeanLocal.cloneFormTemplate(this.selectedForm.getFormTemplateId());
            // Reload
            formTemplates = formTemplateSessionBeanLocal.retrieveAllFormTemplates();

            if (formTemplates.size() > 0) {
                FormTemplate newFormTemplate = formTemplates.get(formTemplates.size() - 1);
                setSelectedForm(newFormTemplate);
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

    public boolean isNewFormIsPublic() {
        return newFormIsPublic;
    }

    public void setNewFormIsPublic(boolean newFormIsPublic) {
        this.newFormIsPublic = newFormIsPublic;
    }

    public void updateFormPrivacy() {
        formTemplateSessionBeanLocal.updateFormTemplatePrivacy(selectedForm.getFormTemplateId(), newFormIsPublic);
        selectedForm.setIsPublic(newFormIsPublic);
        formTemplates = formTemplateSessionBeanLocal.retrieveAllFormTemplates();

    }

    public void setSelectedForm(FormTemplate selectedForm) {
        this.selectedForm = formTemplateSessionBeanLocal.retrieveFormTemplate(selectedForm.getFormTemplateId());
        this.newFormName = selectedForm.getFormTemplateName();
        this.newFormIsPublic = selectedForm.isIsPublic();
        this.selectedFormFieldWrappers = new ArrayList<>();

        for (FormField ff : this.selectedForm.getFormFields()) {
            this.selectedFormFieldWrappers.add(new FormFieldWrapper(ff));
        }

        Collections.sort(selectedFormFieldWrappers);

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

    public String renderDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        return dateFormat.format(date);
    }

    public String renderPrivacy(boolean privacy) {
        if (privacy) {
            return "public";
        } else {
            return "private";
        }
    }

    public InputTypeEnum[] getInputTypes() {
        return InputTypeEnum.values();
    }
}
