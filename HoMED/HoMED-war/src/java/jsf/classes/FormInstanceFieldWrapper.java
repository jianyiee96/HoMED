package jsf.classes;

import entity.FormInstanceField;

public class FormInstanceFieldWrapper {

    private FormInstanceField formInstanceField;

    private Boolean isEditable;

    private String errorMessage;

    public FormInstanceFieldWrapper(FormInstanceField formInstanceField) {
        this.formInstanceField = formInstanceField;
        this.isEditable = !formInstanceField.getFormFieldMapping().getIsServicemanEditable();
    }

    public FormInstanceField getFormInstanceField() {
        return formInstanceField;
    }

    public void setFormInstanceField(FormInstanceField formInstanceField) {
        this.formInstanceField = formInstanceField;
    }

    public Boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Boolean isEditable) {
        this.isEditable = isEditable;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
