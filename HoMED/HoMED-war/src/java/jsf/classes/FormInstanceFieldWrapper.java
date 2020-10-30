package jsf.classes;

import entity.FormInstanceField;
import entity.FormInstanceFieldValue;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import util.enumeration.FormFieldAccessEnum;
import util.enumeration.InputTypeEnum;

public class FormInstanceFieldWrapper {

    private static final SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
    final SimpleDateFormat JSON_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private FormInstanceField formInstanceField;

    private Boolean isEditable;

    private FormFieldAccessEnum accessEnum;

    private Date dateTime;

    private String errorMessage;

    public FormInstanceFieldWrapper(FormInstanceField formInstanceField) {
        this.formInstanceField = formInstanceField;
        prepareLoad();
        this.accessEnum = formInstanceField.getFormFieldMapping().getFormFieldAccess();
        this.isEditable = formInstanceField.getFormFieldMapping().getFormFieldAccess() == FormFieldAccessEnum.MO;
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

    public FormFieldAccessEnum getAccessEnum() {
        return accessEnum;
    }

    public void setAccessEnum(FormFieldAccessEnum accessEnum) {
        this.accessEnum = accessEnum;
    }

    public Boolean getIsEditable() {
        return this.isEditable;
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

    public boolean renderAccessLabel() {
        return this.accessEnum == FormFieldAccessEnum.MO;
    }

    public boolean renderLock() {
        return this.accessEnum == FormFieldAccessEnum.SERVICEMAN_MO;
    }

    public Date getDateTime() {
        try {
            if (formInstanceField.getFormInstanceFieldValues().isEmpty()) {
                return null;
            }
            if (formInstanceField.getFormFieldMapping().getInputType() == InputTypeEnum.DATE) {
                dateTime = JSON_DATE_FORMATTER.parse(formInstanceField.getFormInstanceFieldValues().get(0).getInputValue());
            } else if (formInstanceField.getFormFieldMapping().getInputType() == InputTypeEnum.TIME) {
                dateTime = JSON_DATE_FORMATTER.parse(formInstanceField.getFormInstanceFieldValues().get(0).getInputValue());
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
