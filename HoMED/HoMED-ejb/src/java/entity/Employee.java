/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.EmployeeRoleEnum;
import util.security.CryptographicHelper;

/**
 *
 * @author User
 */
@Entity
public class Employee implements Serializable {
    // whenever new attribute is added, remember to update the updateEmployee in employeeSessionBean

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long employeeId;

    @Column(nullable = false, unique = true, length = 9)
    @NotNull(message = "NRIC must be of length 9")
    @Size(min = 9, max = 9, message = "NRIC must be of length 9")
    protected String nric;

    @Column(columnDefinition = "CHAR(64) NOT NULL")
    @NotNull(message = "Password must be between length 8 to 64")
    @Size(min = 8, max = 64, message = "Password must be between length 8 to 64")
    protected String password;

    @Column(nullable = false, unique = true, length = 64)
    @NotNull(message = "Proper formatted email with length no more than 64 must be provided")
    @Size(max = 64, message = "Proper formatted email with length no more than 64 must be provided")
    @Email(message = "Proper formatted email with length no more than 64 must be provided")
    private String email;

    @Column(nullable = false, length = 128)
    @NotNull(message = "Name must be between length 2 to 128")
    @Size(min = 2, max = 128, message = "First Name must be between length 2 to 128")
    protected String name;

    @Column(nullable = false)
    @NotNull
    private Boolean isActivated;

    @Column(nullable = false, length = 128)
    @NotNull(message = "Address must be between length 2 to 128")
    @Size(min = 2, max = 128, message = "Address must be between length 2 to 128")
    protected String address;

    @Column(nullable = false, unique = true)
    @NotNull(message = "phone number must be length of 8")
    @Min(60000000)
    @Max(99999999)
    protected int phoneNumber;

    @Column(columnDefinition = "CHAR(32) NOT NULL")
    @NotNull
    protected String salt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Role must be provided")
    protected EmployeeRoleEnum role;

    // whenever new attribute is added, remember to update the updateEmployee in employeeSessionBean
    public Employee() {
        this.isActivated = false;
        this.salt = CryptographicHelper.getInstance().generateRandomString(32);
    }

    public Employee(String name, String nric, String password, String email, String address, int phoneNumber) {
        this();
        this.name = name;
        this.nric = nric;
        this.password = password;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        setPassword(password);
    }

    // Constructor to be used for OTP accounts
    public Employee(String name, String nric, String email, String address, int phoneNumber) {
        this();
        this.name = name;
        this.nric = nric;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Integer phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public EmployeeRoleEnum getRole() {
        return role;
    }

    public void setRole(EmployeeRoleEnum role) {
        this.role = role;
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

    public Boolean getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(Boolean isActivated) {
        this.isActivated = isActivated;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
