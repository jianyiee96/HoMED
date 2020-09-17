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

/**
 *
 * @author sunag
 */
@Entity
public class Admin extends Employee implements Serializable {

    public Admin() {
    }

    public Admin(String name, String nric, String password, String email, String address, int phoneNumber) {
        super(name, nric, password, email, address, phoneNumber);
        this.role = EmployeeRoleEnum.ADMIN;
    }

    public Admin(String name, String nric, String email, String address, int phoneNumber) {
        super(name, nric, email, address, phoneNumber);
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
