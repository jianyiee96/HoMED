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

/**
 *
 * @author User
 */
@Entity
public class FormInstanceFieldValue implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formInstanceFieldValueId;

    @Column
    private String inputValue;

    public FormInstanceFieldValue() {
        inputValue = "";
    }

    public FormInstanceFieldValue(String inputValue) {
        this.inputValue = inputValue;
    }

    public Long getFormInstanceFieldValueId() {
        return formInstanceFieldValueId;
    }

    public void setFormInstanceFieldValueId(Long formInstanceFieldValueId) {
        this.formInstanceFieldValueId = formInstanceFieldValueId;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (formInstanceFieldValueId != null ? formInstanceFieldValueId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the formInstanceValueInputId fields are not set
        if (!(object instanceof FormInstanceFieldValue)) {
            return false;
        }
        FormInstanceFieldValue other = (FormInstanceFieldValue) object;
        if ((this.formInstanceFieldValueId == null && other.formInstanceFieldValueId != null) || (this.formInstanceFieldValueId != null && !this.formInstanceFieldValueId.equals(other.formInstanceFieldValueId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FormInstanceValueInput[ id=" + formInstanceFieldValueId + " ]";
    }
    
}
