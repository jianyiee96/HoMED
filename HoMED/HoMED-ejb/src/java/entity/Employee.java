package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import util.enumeration.EmployeeRoleEnum;
import util.enumeration.GenderEnum;
import util.security.CryptographicHelper;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Employee implements Serializable {
    // whenever new attribute is added, remember to update the updateEmployee in employeeSessionBean

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long employeeId;

    @Column(nullable = false, length = 128)
    @NotNull(message = "Name must be provided")
    @Size(min = 2, max = 128, message = "Name must be between length 2 to 128")
    protected String name;

    @Column(nullable = false, unique = true, length = 64)
    @NotNull(message = "Email must be provided")
    @Size(max = 64, message = "Proper formatted Email must be provided")
    @Email(message = "Proper formatted Email must be provided")
    protected String email;

    @Column(nullable = false, unique = true, length = 8)
    @NotNull(message = "Phone Number must be provided")
    @Size(min = 8, max = 8, message = "Phone Number must be of length 8")
    @Pattern(regexp = "^[689]\\d{7}", message = "Proper formmated Phone Number must be provided")
    protected String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Gender must be provided")
    protected GenderEnum gender;

    @Column(columnDefinition = "CHAR(64) NOT NULL")
    @NotNull(message = "Password must be between length 8 to 64")
    @Size(min = 8, max = 64, message = "Password must be between length 8 to 64")
    protected String password;

    @Embedded
    @Column(nullable = false)
    @NotNull(message = "Address must be provided")
    protected Address address;

    @Column(nullable = false)
    @NotNull
    protected Boolean isActivated;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Role must be provided")
    protected EmployeeRoleEnum role;

    @Column(columnDefinition = "CHAR(32) NOT NULL")
    @NotNull
    protected String salt;

    // whenever new attribute is added, remember to update the updateEmployee in employeeSessionBean
    public Employee() {
        this.isActivated = false;
        this.salt = CryptographicHelper.getInstance().generateRandomString(32);

        this.address = new Address();
    }

    public Employee(String name, String password, String email, Address address, String phoneNumber, GenderEnum genderEnum) {
        this();
        this.name = name;
        this.password = password;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.gender = genderEnum;
        setPassword(password);
    }

    // Constructor to be used for OTP accounts
    public Employee(String name, String email, Address address, String phoneNumber, GenderEnum genderEnum) {
        this();
        this.name = name;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.gender = genderEnum;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public EmployeeRoleEnum getRole() {
        return role;
    }

    public void setRole(EmployeeRoleEnum role) {
        this.role = role;
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
