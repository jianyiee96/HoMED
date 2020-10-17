package jsf.classes;

import entity.FormInstanceField;
import entity.FormInstanceFieldValue;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
        prepareLoad();
        this.isEditable = !formInstanceField.getFormFieldMapping().getIsServicemanEditable();
    }

    public void prepareLoad() {
        if (this.formInstanceField.getFormInstanceFieldValues().isEmpty()) {
            this.formInstanceField.getFormInstanceFieldValues().add(new FormInstanceFieldValue());
        }
    }

    public void prepareSubmission(Boolean checkError) {

        this.formInstanceField.setFormInstanceFieldValues(this.formInstanceField.getFormInstanceFieldValues().stream()
                .filter(fifv -> fifv.getInputValue() != null && !fifv.getInputValue().equals(""))
                .collect(Collectors.toList()));

        if (checkError) {
            if (this.getFormInstanceField().getFormFieldMapping().getIsRequired()) {
                if (this.getFormInstanceField().getFormInstanceFieldValues().isEmpty()) {
                    this.setErrorMessage("Question " + this.getFormInstanceField().getFormFieldMapping().getPosition() + " is required");
                } else {
                    this.setErrorMessage(null);
                }
            }
        }
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
