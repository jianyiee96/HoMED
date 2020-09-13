/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author User
 */
@Entity
public class FormFieldOption implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formFieldOptionId;

    public Long getFormFieldOptionId() {
        return formFieldOptionId;
    }

    public void setFormFieldOptionId(Long formFieldOptionId) {
        this.formFieldOptionId = formFieldOptionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (formFieldOptionId != null ? formFieldOptionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the formFieldOptionId fields are not set
        if (!(object instanceof FormFieldOption)) {
            return false;
        }
        FormFieldOption other = (FormFieldOption) object;
        if ((this.formFieldOptionId == null && other.formFieldOptionId != null) || (this.formFieldOptionId != null && !this.formFieldOptionId.equals(other.formFieldOptionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FormFieldOption[ id=" + formFieldOptionId + " ]";
    }
    
}
