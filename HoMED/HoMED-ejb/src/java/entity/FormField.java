/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import util.enumeration.InputTypeEnum;

/**
 *
 * @author User
 */
@Entity
public class FormField implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formFieldId;

    @Column(nullable = true) //Validation to be done when saving a form
    private String question;

    @Column(nullable = false)
    @NotNull(message = "Position must not be null")
    private Integer position;

    @Enumerated(EnumType.STRING)
    @Column
    private InputTypeEnum inputType;

    @Column(nullable = false)
    @NotNull(message = "isRequired must not be null")
    private Boolean isRequired;

    @Column(nullable = false)
    @NotNull(message = "isServicemanEditable must not be null")
    private Boolean isServicemanEditable;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FormFieldOption> formFieldOptions;

    public FormField() {
        this.isRequired = false;
        this.isServicemanEditable = false;
        this.formFieldOptions = new ArrayList<>();
        this.position = 1;
    }

    public FormField(String question, Integer position, InputTypeEnum inputType, Boolean isRequired, Boolean isServicemanEditable, List<FormFieldOption> formFieldOptions) {
        this.question = question;
        this.position = position;
        this.inputType = inputType;
        this.isRequired = isRequired;
        this.isServicemanEditable = isServicemanEditable;
        if (formFieldOptions == null) {
            this.formFieldOptions = new ArrayList<>();
        } else {
            this.formFieldOptions = formFieldOptions;
        }
    }

    public Long getFormFieldId() {
        return formFieldId;
    }

    public void setFormFieldId(Long formFieldId) {
        this.formFieldId = formFieldId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public InputTypeEnum getInputType() {
        return inputType;
    }

    public void setInputType(InputTypeEnum inputType) {
        this.inputType = inputType;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Boolean getIsServicemanEditable() {
        return isServicemanEditable;
    }

    public void setIsServicemanEditable(Boolean isServicemanEditable) {
        this.isServicemanEditable = isServicemanEditable;
    }

    public List<FormFieldOption> getFormFieldOptions() {
        return formFieldOptions;
    }

    public void setFormFieldOptions(List<FormFieldOption> formFieldOptions) {
        this.formFieldOptions = formFieldOptions;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (formFieldId != null ? formFieldId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the formFieldId fields are not set
        if (!(object instanceof FormField)) {
            return false;
        }
        FormField other = (FormField) object;
        if ((this.formFieldId == null && other.formFieldId != null) || (this.formFieldId != null && !this.formFieldId.equals(other.formFieldId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Form Field [ id: " + formFieldId + " ]";
    }

}
