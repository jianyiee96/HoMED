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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import util.enumeration.FormInstanceStatusEnum;

/**
 *
 * @author sunag
 */
@Entity
public class FormInstance implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formInstanceId;
        
    @Enumerated(EnumType.STRING)
    @Column
    private FormInstanceStatusEnum formInstanceStatusEnum;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private FormTemplate formTemplateMapping;
    
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FormInstanceField> formInstanceFields;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Serviceman serviceman;
    
    public FormInstance() {
        this.formInstanceStatusEnum = FormInstanceStatusEnum.DRAFT;
        this.formInstanceFields = new ArrayList<>();
    }
    
    public FormInstanceStatusEnum getFormInstanceStatusEnum() {
        return formInstanceStatusEnum;
    }

    public void setFormInstanceStatusEnum(FormInstanceStatusEnum formInstanceStatusEnum) {
        this.formInstanceStatusEnum = formInstanceStatusEnum;
    }

    public FormTemplate getFormTemplateMapping() {
        return formTemplateMapping;
    }

    public void setFormTemplateMapping(FormTemplate formTemplateMapping) {
        this.formTemplateMapping = formTemplateMapping;
    }

    public List<FormInstanceField> getFormInstanceFields() {
        return formInstanceFields;
    }

    public void setFormInstanceFields(List<FormInstanceField> formInstanceFields) {
        this.formInstanceFields = formInstanceFields;
    }

    public Serviceman getServiceman() {
        return serviceman;
    }

    public void setServiceman(Serviceman serviceman) {
        this.serviceman = serviceman;
    }

    public Long getFormInstanceId() {
        return formInstanceId;
    }

    public void setFormInstanceId(Long formInstanceId) {
        this.formInstanceId = formInstanceId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (formInstanceId != null ? formInstanceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the formInstanceId fields are not set
        if (!(object instanceof FormInstance)) {
            return false;
        }
        FormInstance other = (FormInstance) object;
        if ((this.formInstanceId == null && other.formInstanceId != null) || (this.formInstanceId != null && !this.formInstanceId.equals(other.formInstanceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FromInstance[ id=" + formInstanceId + " ]";
    }
    
}
