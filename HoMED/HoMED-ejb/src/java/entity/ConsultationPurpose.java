/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author User
 */
@Entity
public class ConsultationPurpose implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long consultationPurposeId;

    @Column(nullable = false, unique = true, length = 128)
    @NotNull(message = "Consultation Purpose Name must be between length 1 to 128")
    @Size(min = 1, max = 128, message = "Consultation Purpose Name must be between length 1 to 128")
    private String consultationPurposeName;

    @ManyToMany
    private List<FormTemplate> formTemplates;
    
    @OneToMany
    private List<Booking> bookings;
    
    @Column(nullable = false)
    @NotNull
    private Boolean isActive;
    
    public ConsultationPurpose() {
        isActive = Boolean.TRUE;
        formTemplates = new ArrayList<>();
        bookings = new ArrayList<>();
    }
    
    public ConsultationPurpose(String consultationPurposeName) {
        this();
        this.consultationPurposeName = consultationPurposeName;
    }
    
    public Long getConsultationPurposeId() {
        return consultationPurposeId;
    }

    public void setConsultationPurposeId(Long consultationPurposeId) {
        this.consultationPurposeId = consultationPurposeId;
    }
    
    public String getConsultationPurposeName() {
        return consultationPurposeName;
    }

    public void setConsultationPurposeName(String consultationPurposeName) {
        this.consultationPurposeName = consultationPurposeName;
    }

    public List<FormTemplate> getFormTemplates() {
        return formTemplates;
    }

    public void setFormTemplates(List<FormTemplate> formTemplates) {
        this.formTemplates = formTemplates;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (consultationPurposeId != null ? consultationPurposeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the consultationPurposeId fields are not set
        if (!(object instanceof ConsultationPurpose)) {
            return false;
        }
        ConsultationPurpose other = (ConsultationPurpose) object;
        if ((this.consultationPurposeId == null && other.consultationPurposeId != null) || (this.consultationPurposeId != null && !this.consultationPurposeId.equals(other.consultationPurposeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Consultation Purpose [ id: " + consultationPurposeId + " ]";
    }
    
}
