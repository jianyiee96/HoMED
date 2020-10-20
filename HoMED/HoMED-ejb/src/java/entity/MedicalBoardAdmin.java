package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import util.enumeration.EmployeeRoleEnum;
import util.enumeration.GenderEnum;

@Entity
public class MedicalBoardAdmin extends MedicalStaff implements Serializable {

    public MedicalBoardAdmin() {
    }

    public MedicalBoardAdmin(String name, String password, String email, Address address, String phoneNumber, GenderEnum gender) {
        super(name, password, email, address, phoneNumber, gender);
        super.role = EmployeeRoleEnum.MB_ADMIN;
    }

    public MedicalBoardAdmin(String name, String email, Address address, String phoneNumber, GenderEnum gender) {
        super(name, email, address, phoneNumber, gender);
        super.role = EmployeeRoleEnum.MB_ADMIN;
    }

    public MedicalBoardAdmin(Employee e) {
        super(e.name, e.password, e.email, e.address, e.phoneNumber, e.gender);
        super.role = EmployeeRoleEnum.MB_ADMIN;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MedicalBoardAdmin)) {
            return false;
        }
        MedicalBoardAdmin other = (MedicalBoardAdmin) object;
        if ((super.getEmployeeId() == null && other.getEmployeeId() != null) || (super.getEmployeeId() != null && !super.getEmployeeId().equals(other.getEmployeeId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Medical Board Admin [ id: " + super.getEmployeeId() + " ]";
    }

}
