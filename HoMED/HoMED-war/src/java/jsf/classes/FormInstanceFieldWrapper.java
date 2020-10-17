package jsf.classes;

import entity.FormInstanceField;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import util.enumeration.InputTypeEnum;

public class FormInstanceFieldWrapper {

    private static final SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

    private FormInstanceField formInstanceField;

    private Boolean isEditable;

    private Date dateTime;

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

    public Date getDateTime() {
        try {
            if (formInstanceField.getFormFieldMapping().getInputType() == InputTypeEnum.DATE) {
                dateTime = sdfDate.parse(formInstanceField.getFormInstanceFieldValues().get(0).getInputValue());
            } else if (formInstanceField.getFormFieldMapping().getInputType() == InputTypeEnum.TIME) {
                dateTime = sdfTime.parse(formInstanceField.getFormInstanceFieldValues().get(0).getInputValue());
            }
        } catch (ParseException ex) {
            return null;
        }
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        if (dateTime != null) {
            if (formInstanceField.getFormFieldMapping().getInputType() == InputTypeEnum.DATE) {
                formInstanceField.getFormInstanceFieldValues().get(0).setInputValue(sdfDate.format(dateTime));
            } else if (formInstanceField.getFormFieldMapping().getInputType() == InputTypeEnum.TIME) {
                formInstanceField.getFormInstanceFieldValues().get(0).setInputValue(sdfTime.format(dateTime));
            }
        } else {
            formInstanceField.getFormInstanceFieldValues().get(0).setInputValue("");
        }
        this.dateTime = dateTime;
    }

}
