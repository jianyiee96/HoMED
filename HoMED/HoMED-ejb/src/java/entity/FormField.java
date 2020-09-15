/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.InputTypeEnum;
import util.security.CryptographicHelper;

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

    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String title;
    
    @Column(nullable = false)
    @NotNull
    @Min(1)
    private int position;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private InputTypeEnum inputType;
    
    @Column(nullable = false)
    @NotNull
    private boolean isRequired;
    
    @Column(nullable = false)
    @NotNull
    private boolean isServicemanEditable;
    
    @OneToMany
    private List<FormFieldOption> formFieldOptions;
    

    public FormField() {
        this.formFieldOptions = new ArrayList<>();
    }
    
    public Long getFormFieldId() {
        return formFieldId;
    }

    public void setFormFieldId(Long formFieldId) {
        this.formFieldId = formFieldId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public InputTypeEnum getInputType() {
        return inputType;
    }

    public void setInputType(InputTypeEnum inputType) {
        this.inputType = inputType;
    }

    public boolean isIsRequired() {
        return isRequired;
    }

    public void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public boolean isIsServicemanEditable() {
        return isServicemanEditable;
    }

    public void setIsServicemanEditable(boolean isServicemanEditable) {
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
        return "entity.FormField[ id=" + formFieldId + " ]";
    }
    
}
