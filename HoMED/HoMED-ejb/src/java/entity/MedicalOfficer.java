package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import util.enumeration.EmployeeRoleEnum;
import util.enumeration.GenderEnum;

@Entity
public class MedicalOfficer extends MedicalStaff implements Serializable {

    public MedicalOfficer() {
    }

    public MedicalOfficer(String name, String password, String email, Address address, String phoneNumber, GenderEnum gender) {
        super(name, password, email, address, phoneNumber, gender);
        super.role = EmployeeRoleEnum.MEDICAL_OFFICER;
    }

    public MedicalOfficer(String name, String email, Address address, String phoneNumber, GenderEnum gender) {
        super(name, email, address, phoneNumber, gender);
        super.role = EmployeeRoleEnum.MEDICAL_OFFICER;
    }

    public MedicalOfficer(Employee e) {
        super(e.name, e.password, e.email, e.address, e.phoneNumber, e.gender);
        super.role = EmployeeRoleEnum.MEDICAL_OFFICER;
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
