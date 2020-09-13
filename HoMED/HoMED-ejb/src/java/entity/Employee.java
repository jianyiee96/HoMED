/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.security.CryptographicHelper;

/**
 *
 * @author User
 */
@Entity
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @Column(nullable = false, unique = true, length = 9)
    @NotNull(message = "NRIC must be of length 9")
    @Size(min = 9, max = 9, message = "NRIC must be of length 9")
    private String nric;
    
    @Column(columnDefinition = "CHAR(64) NOT NULL")
    @NotNull(message = "Password must be between length 8 to 64")
    @Size(min = 8, max = 64, message = "Password must be between length 8 to 64")
    private String password;
    
    @Column(nullable = false, length = 128)
    @NotNull(message = "Name must be between length 2 to 128")
    @Size(min = 2, max = 128, message = "First Name must be between length 2 to 128")
    private String name;
    
    @Column(columnDefinition = "CHAR(32) NOT NULL")
    @NotNull
    private String salt;
    
    public Employee() {
        this.salt = CryptographicHelper.getInstance().generateRandomString(32);
    }
    
    public Employee(String name, String nric, String password) {
        this();
        this.name = name;
        this.nric = nric;
        this.password = password;
        setPassword(password);
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null) {
            this.password = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + this.salt));
        } else {
            this.password = null;
        }
    }
    
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long id) {
        this.employeeId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (employeeId != null ? employeeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the employeeId fields are not set
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee other = (Employee) object;
        if ((this.employeeId == null && other.employeeId != null) || (this.employeeId != null && !this.employeeId.equals(other.employeeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Employee[ id=" + employeeId + " ]";
    }
    
}
