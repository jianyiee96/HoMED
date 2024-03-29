/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.classes;

import entity.FormField;
import entity.FormFieldOption;
import java.util.ArrayList;
import java.util.List;
import util.enumeration.InputTypeEnum;
import util.security.CryptographicHelper;

public class FormFieldWrapper implements Comparable<FormFieldWrapper> {

    private List<String> formFieldOptions = new ArrayList<>();
    private FormField formField;
    
    private boolean declarationField;
    private boolean declarationFieldEnabled;
    private String declarationText;
    
    private String formFieldCode;

    // Called when creating a declaration field
    public FormFieldWrapper(boolean declarationField){
        this.declarationField = declarationField;
    }
    
    public FormFieldWrapper(FormField formField) {
        this(false);
        this.formFieldCode = CryptographicHelper.getInstance().generateRandomString(32);
        this.formField = formField;

        if (getHasInputOption()) {
            for (FormFieldOption ffo : formField.getFormFieldOptions()) {
                formFieldOptions.add(ffo.getFormFieldOptionValue());
            }
        }
    }

    public List<String> getFormFieldOptions() {
        if (formFieldOptions == null) {
            return new ArrayList<String>();
        }

        return formFieldOptions;
    }

    public void setFormFieldOptions(List<String> formFieldOptions) {
        this.formFieldOptions = formFieldOptions;
    }

    public FormField getFormField() {
        return formField;
    }

    public void setFormField(FormField formField) {
        this.formField = formField;
    }

    public String getFormFieldCode() {
        return formFieldCode;
    }

    public void setFormFieldCode(String formFieldCode) {
        this.formFieldCode = formFieldCode;
    }

    public boolean getHasInputOption() {

        InputTypeEnum i = this.formField.getInputType();

        if (i == InputTypeEnum.CHECK_BOX
                || i == InputTypeEnum.RADIO_BUTTON
                || i == InputTypeEnum.MULTI_DROPDOWN
                || i == InputTypeEnum.SINGLE_DROPDOWN) {
            return true;
        }
        return false;
    }

    public boolean getIsHeader() {

        InputTypeEnum i = this.formField.getInputType();
        if (i == InputTypeEnum.HEADER) {
            return true;
        }
        return false;
    }

    public boolean isDeclarationField() {
        return declarationField;
    }

    public void setDeclarationField(boolean declarationField) {
        this.declarationField = declarationField;
    }

    public String getDeclarationText() {
        return declarationText;
    }

    public void setDeclarationText(String declarationText) {
        this.declarationText = declarationText;
    }

    public boolean isDeclarationFieldEnabled() {
        return declarationFieldEnabled;
    }

    public void setDeclarationFieldEnabled(boolean declarationFieldEnabled) {
        this.declarationFieldEnabled = declarationFieldEnabled;
    }
    
    @Override
    public int compareTo(FormFieldWrapper another) {
        try {
            return(this.formField.getPosition() - another.formField.getPosition());
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
