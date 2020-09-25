/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author sunag
 */
@Entity
public class FormInstanceValue implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formInstanceValueId;
    
    @Column
    private String inputValue;
    
    @OneToOne(optional = false)
    private FormField formFieldMapping;
    
    public FormInstanceValue(){
        this.inputValue = "";
    }
    
    public FormInstanceValue(FormField formField) {
        this();
        this.formFieldMapping = formField;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }

    public FormField getFormFieldMapping() {
        return formFieldMapping;
    }

    public void setFormFieldMapping(FormField formFieldMapping) {
        this.formFieldMapping = formFieldMapping;
    }
    
    public Long getFormInstanceValueId() {
        return formInstanceValueId;
    }

    public void setFormInstanceValueId(Long formInstanceValueId) {
        this.formInstanceValueId = formInstanceValueId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (formInstanceValueId != null ? formInstanceValueId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the formInstanceValueId fields are not set
        if (!(object instanceof FormInstanceValue)) {
            return false;
        }
        FormInstanceValue other = (FormInstanceValue) object;
        if ((this.formInstanceValueId == null && other.formInstanceValueId != null) || (this.formInstanceValueId != null && !this.formInstanceValueId.equals(other.formInstanceValueId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FormInstanceValue[ id=" + formInstanceValueId + " ]";
    }
    
}
