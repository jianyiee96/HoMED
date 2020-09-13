/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class FormTemplate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formTemplateId;

    @Column(nullable = false, length = 128)
    @NotNull(message = "Form Name must be between length 2 to 128")
    @Size(min = 2, max = 128, message = "Form Name must be between length 2 to 128")
    private String formTemplateName;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date dateCreated;
    
    @Column(nullable = false)
    @NotNull
    private Boolean isArchived;
    
    public FormTemplate() {
        this.dateCreated = new Date();
        this.isArchived = false;
    }

    public FormTemplate(String formTemplateName) {
        this();
        this.formTemplateName = formTemplateName;
    }

    public Long getFormTemplateId() {
        return formTemplateId;
    }

    public void setFormTemplateId(Long formTemplateId) {
        this.formTemplateId = formTemplateId;
    }

    public String getFormTemplateName() {
        return formTemplateName;
    }

    public void setFormTemplateName(String formTemplateName) {
        this.formTemplateName = formTemplateName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }
    
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (formTemplateId != null ? formTemplateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the formTemplateId fields are not set
        if (!(object instanceof FormTemplate)) {
            return false;
        }
        FormTemplate other = (FormTemplate) object;
        if ((this.formTemplateId == null && other.formTemplateId != null) || (this.formTemplateId != null && !this.formTemplateId.equals(other.formTemplateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FormTemplate[ id=" + formTemplateId + " ]";
    }
    
}
