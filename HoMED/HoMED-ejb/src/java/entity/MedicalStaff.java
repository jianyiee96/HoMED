/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import util.enumeration.GenderEnum;
import util.security.CryptographicHelper;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class MedicalStaff extends Employee implements Serializable {

    @ManyToOne(optional = true)
    @JoinColumn(nullable = true)
    private MedicalCentre medicalCentre;

    public MedicalStaff() {
    }

    public MedicalStaff(String name, String password, String email, Address address, String phoneNumber, GenderEnum gender) {
        super(name, password, email, address, phoneNumber, gender);
    }

    public MedicalStaff(String name, String email, Address address, String phoneNumber, GenderEnum gender) {
        super(name, email, address, phoneNumber, gender);
    }

    public MedicalCentre getMedicalCentre() {
        return medicalCentre;
    }

    public void setMedicalCentre(MedicalCentre medicalCentre) {
        if (this.medicalCentre != null) {
            this.medicalCentre.getMedicalStaffList().remove(this);
        }

        this.medicalCentre = medicalCentre;

        if (this.medicalCentre != null) {
            if (!this.medicalCentre.getMedicalStaffList().contains(this)) {
                this.medicalCentre.getMedicalStaffList().add(this);
            }
        }
    }

    public void setMedicalCentreToNull() {
        this.medicalCentre = null;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MedicalStaff)) {
            return false;
        }
        MedicalStaff other = (MedicalStaff) object;
        if ((super.getEmployeeId() == null && other.getEmployeeId() != null) || (super.getEmployeeId() != null && !super.getEmployeeId().equals(other.getEmployeeId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Medical Staff [ id: " + super.getEmployeeId() + " ]";
    }
}
