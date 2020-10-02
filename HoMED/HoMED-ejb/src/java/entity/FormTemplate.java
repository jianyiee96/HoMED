/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.FormTemplateStatusEnum;

@Entity
public class FormTemplate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formTemplateId;

    @Column(nullable = false, length = 128)
    @NotNull(message = "Form Name must be between length 1 to 128")
    @Size(min = 1, max = 128, message = "Form Name must be between length 1 to 128")
    private String formTemplateName;
    
    @Column(nullable = true, length = 8000)
    private String declaration;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date dateCreated;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date datePublished;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private FormTemplateStatusEnum formTemplateStatus;
    
    @Column(nullable = false)
    @NotNull
    private Boolean isPublic;
    
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FormField> formFields;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "formTemplates")
    private List<ConsultationPurpose> consultationPurposes;
    
    //Cannot delete FormTemplate when there is FormInstance
    @OneToMany
    private List<FormInstance> formInstances;
    
    public FormTemplate() {
        this.dateCreated = new Date();
        this.formFields = new ArrayList<>();
        this.consultationPurposes = new ArrayList<>();
        this.formTemplateStatus = FormTemplateStatusEnum.DRAFT;
        this.isPublic = false;
        this.formInstances = new ArrayList<>();
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

    public String getDeclaration() {
        return declaration;
    }

    public void setDeclaration(String declaration) {
        this.declaration = declaration;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }
    
    public FormTemplateStatusEnum getFormTemplateStatus() {
        return formTemplateStatus;
    }

    public void setFormTemplateStatus(FormTemplateStatusEnum formTemplateStatus) {
        this.formTemplateStatus = formTemplateStatus;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public List<FormField> getFormFields() {
        return formFields;
    }

    public void setFormFields(List<FormField> formFields) {
        this.formFields = formFields;
    }

    public List<ConsultationPurpose> getConsultationPurposes() {
        return consultationPurposes;
    }

    public void setConsultationPurposes(List<ConsultationPurpose> consultationPurposes) {
        this.consultationPurposes = consultationPurposes;
    }

    public List<FormInstance> getFormInstances() {
        return formInstances;
    }

    public void setFormInstances(List<FormInstance> formInstances) {
        this.formInstances = formInstances;
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
        return formTemplateName+ " [" + formTemplateId + "]";
    }
    
}
