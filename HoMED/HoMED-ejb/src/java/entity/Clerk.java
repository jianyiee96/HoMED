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
public class Clerk extends Employee implements Serializable {

    
    public Clerk(){
    }
    
    public Clerk(String name, String nric, String password) {
        super(name, nric, password);
        this.role = EmployeeRoleEnum.CLERK; 
    }



    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Clerk)) {
            return false;
        }
        Clerk other = (Clerk) object;
        if ((super.getEmployeeId() == null && other.getEmployeeId() != null) || (super.getEmployeeId() != null && !super.getEmployeeId().equals(other.getEmployeeId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Clerk[ id=" + super.getEmployeeId() + " ]";
    }
    
}