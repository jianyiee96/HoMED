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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import jsf.classes.FormFieldWrapper;
import util.enumeration.InputTypeEnum;
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

    public FormUtilityManagedBean() {
        inputTypes = Arrays.asList(InputTypeEnum.class.getEnumConstants());
    }

    @PostConstruct
    public void postConstruct() {

        formTemplates = formTemplateSessionBeanLocal.retrieveAllFormTemplates();
        selectedFormFieldWrappers = new ArrayList<>();
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

    // Invoked then selected radio button
    public void setSelectedForm(FormTemplate selectedForm) {
        System.out.println("Selected: " + selectedForm.getFormTemplateName());
        this.selectedForm = selectedForm;
        this.selectedFormFieldWrappers = new ArrayList<>();

        for (FormField ff : selectedForm.getFormFields()) {
            this.selectedFormFieldWrappers.add(new FormFieldWrapper(ff));
        }
    }

    public void addCurrentFormFieldWrapper() {
        FormField newff = new FormField();
        FormFieldWrapper newffw = new FormFieldWrapper(newff);
        this.selectedFormFieldWrappers.add(newffw);
    }

    public void removeFormFieldWrapper(ActionEvent event) {

        String fieldToDelete = (String) event.getComponent().getAttributes().get("formfieldWrapperToRemove");
        System.out.println("Removing: " + fieldToDelete);
        List<FormFieldWrapper> remainingFormFieldWrappers = new ArrayList<>();

        for (FormFieldWrapper ffw : selectedFormFieldWrappers) {
            if (!ffw.getFormFieldCode().equals(fieldToDelete)) {
                System.out.println("Copying over: ");
                System.out.println(ffw.formField.getTitle());
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

}
