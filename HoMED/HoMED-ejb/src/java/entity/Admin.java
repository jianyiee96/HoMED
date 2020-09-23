/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import util.enumeration.EmployeeRoleEnum;
import util.enumeration.GenderEnum;

/**
 *
 * @author sunag
 */
@Entity
public class Admin extends Employee implements Serializable {

    public Admin() {
    }

    public Admin(String name, String password, String email, Address address, String phoneNumber, GenderEnum gender) {
        super(name, password, email, address, phoneNumber, gender);
        this.role = EmployeeRoleEnum.ADMIN;
    }

    public Admin(String name, String email, Address address, String phoneNumber, GenderEnum gender) {
        super(name, email, address, phoneNumber, gender);
        this.role = EmployeeRoleEnum.ADMIN;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Admin)) {
            return false;
        }
        Admin other = (Admin) object;
        if ((super.getEmployeeId() == null && other.getEmployeeId() != null) || (super.getEmployeeId() != null && !super.getEmployeeId().equals(other.getEmployeeId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Admin[ id=" + super.getEmployeeId() + " ]";
    }

}
