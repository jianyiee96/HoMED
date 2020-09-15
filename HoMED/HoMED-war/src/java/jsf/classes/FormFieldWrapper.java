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

/**
 *
 * @author User
 */
public class FormFieldWrapper {

    public List<String> formFieldOptions = new ArrayList<>();
    public FormField formField;
    public String formFieldCode;

    public FormFieldWrapper(FormField formField) {
        this.formFieldCode = CryptographicHelper.getInstance().generateRandomString(32);
        this.formField = formField;

        for (FormFieldOption ffo : formField.getFormFieldOptions()) {
            formFieldOptions.add(ffo.getFormFieldOptionValue());
        }
    }

    public List<String> getFormFieldOptions() {
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
        
        if(i == InputTypeEnum.CHECK_BOX
                || i == InputTypeEnum.RADIO_BUTTON
                || i == InputTypeEnum.MULTI_DROPDOWN
                || i == InputTypeEnum.SINGLE_DROPDOWN){
            return true;
        }
        return false;
    }
    
    public boolean getIsHeader() {
        
        InputTypeEnum i = this.formField.getInputType();
        if(i == InputTypeEnum.HEADER){
            return true;
        }
        return false;
    }
    

}
