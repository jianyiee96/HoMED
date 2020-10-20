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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date dateSubmitted;

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
    
    @ManyToOne(optional = true)
    private Booking booking;
    
    @ManyToOne(optional = true)
    private MedicalOfficer signedBy;

    public FormInstance() {
        this.dateCreated = new Date();
        this.formInstanceStatusEnum = FormInstanceStatusEnum.DRAFT;
        this.formInstanceFields = new ArrayList<>();
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(Date dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
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

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public MedicalOfficer getSignedBy() {
        return signedBy;
    }

    public void setSignedBy(MedicalOfficer signedBy) {
        this.signedBy = signedBy;
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
        return "Form Instance [ id: " + formInstanceId + " ]";
    }

}
