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
import util.enumeration.EmployeeRoleEnum;
import util.enumeration.GenderEnum;

/**
 *
 * @author sunag
 */
@Entity
public class MedicalOfficer extends Employee implements Serializable {

    public MedicalOfficer() {
    }


    public MedicalOfficer(String name, String nric, String password, String email, String address, String phoneNumber, GenderEnum gender) {
        super(name, nric, password, email, address, phoneNumber, gender);
        this.role = EmployeeRoleEnum.MEDICAL_OFFICER;
    }

    public MedicalOfficer(String name, String nric, String email, String address, String phoneNumber, GenderEnum gender) {
        super(name, nric, email, address, phoneNumber, gender);
        this.role = EmployeeRoleEnum.MEDICAL_OFFICER;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MedicalOfficer)) {
            return false;
        }
        MedicalOfficer other = (MedicalOfficer) object;
        if ((super.getEmployeeId() == null && other.getEmployeeId() != null) || (super.getEmployeeId() != null && !super.getEmployeeId().equals(other.getEmployeeId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.MedicalOfficer[ id=" + super.getEmployeeId() + " ]";
    }

}
