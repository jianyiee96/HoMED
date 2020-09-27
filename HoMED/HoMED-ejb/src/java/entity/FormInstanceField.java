/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author sunag
 */
@Entity
public class FormInstanceField implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formInstanceFieldId;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FormInstanceFieldValue> formInstanceFieldValues;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private FormField formFieldMapping;

    public FormInstanceField() {
        this.formInstanceFieldValues = new ArrayList<>();
    }

    public FormInstanceField(FormField formField) {
        this();
        this.formFieldMapping = formField;
    }

    public FormField getFormFieldMapping() {
        return formFieldMapping;
    }

    public void setFormFieldMapping(FormField formFieldMapping) {
        this.formFieldMapping = formFieldMapping;
    }

    public Long getFormInstanceFieldId() {
        return formInstanceFieldId;
    }

    public void setFormInstanceFieldId(Long formInstanceFieldId) {
        this.formInstanceFieldId = formInstanceFieldId;
    }

    public List<FormInstanceFieldValue> getFormInstanceFieldValues() {
        return formInstanceFieldValues;
    }

    public void setFormInstanceFieldValues(List<FormInstanceFieldValue> formInstanceFieldValues) {
        this.formInstanceFieldValues = formInstanceFieldValues;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (formInstanceFieldId != null ? formInstanceFieldId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the formInstanceFieldId fields are not set
        if (!(object instanceof FormInstanceField)) {
            return false;
        }
        FormInstanceField other = (FormInstanceField) object;
        if ((this.formInstanceFieldId == null && other.formInstanceFieldId != null) || (this.formInstanceFieldId != null && !this.formInstanceFieldId.equals(other.formInstanceFieldId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FormInstanceValue[ id=" + formInstanceFieldId + " ]";
    }

}
