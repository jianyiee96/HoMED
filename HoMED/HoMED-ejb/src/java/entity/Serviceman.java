/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import util.enumeration.BloodTypeEnum;
import util.enumeration.GenderEnum;
import util.security.CryptographicHelper;

/**
 *
 * @author Keith Lim <https://github.com/keithlim>
 */
@Entity
public class Serviceman implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long servicemanId;

    @Column(nullable = false, length = 128)
    @NotNull(message = "Name must be provided")
    @Size(min = 2, max = 128, message = "Name must be between length 2 to 128")
    private String name;

    @Column(nullable = false, unique = true, length = 64)
    @NotNull(message = "Email must be provided")
    @Size(max = 64, message = "Proper formatted Email must be provided")
    @Email(message = "Proper formatted Email must be provided")
    private String email;

    @Column(nullable = false, unique = true, length = 8)
    @NotNull(message = "Phone Number must be provided")
    @Size(min = 8, max = 8, message = "Phone Number must be of length 8")
    @Pattern(regexp = "^[89]\\d{7}", message = "Proper formmated Phone Number must be provided")
    private String phoneNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull(message = "ROD must be provided")
    private Date rod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Gender must be provided")
    private GenderEnum gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Blood type must be provided")
    private BloodTypeEnum bloodType;

    @Column(columnDefinition = "CHAR(64) NOT NULL")
    @NotNull(message = "Password must be provided")
    @Size(min = 8, max = 64, message = "Password must be between length 8 to 64")
    private String password;
    
    @OneToMany
    private List<FormInstance> formInstances;
    
    @Embedded
    @Column(nullable = false)
    @NotNull(message = "Address must be provided")
    private Address address;

    @Column(nullable = false)
    @NotNull
    private Boolean isActivated;

    @Column(columnDefinition = "CHAR(32) NOT NULL")
    @NotNull
    private String salt;

    public Serviceman() {
        this.isActivated = false;
        this.salt = CryptographicHelper.getInstance().generateRandomString(32);
        
        this.address = new Address();
    }

    public Serviceman(String name, String email, String phoneNumber, Date ord, GenderEnum gender, BloodTypeEnum bloodType, Address address) {
        this();
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.rod = ord;
        this.gender = gender;
        this.bloodType = bloodType;
        this.address = address;
    }

    public Long getServicemanId() {
        return servicemanId;
    }

    public void setServicemanId(Long servicemanId) {
        this.servicemanId = servicemanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getRod() {
        return rod;
    }

    public void setRod(Date rod) {
        this.rod = rod;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public BloodTypeEnum getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodTypeEnum bloodType) {
        this.bloodType = bloodType;
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


    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Boolean getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(Boolean isActivated) {
        this.isActivated = isActivated;
    }

    public List<FormInstance> getFormInstances() {
        return formInstances;
    }

    public void setFormInstances(List<FormInstance> formInstances) {
        this.formInstances = formInstances;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (servicemanId != null ? servicemanId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the servicemanId fields are not set
        if (!(object instanceof Serviceman)) {
            return false;
        }
        Serviceman other = (Serviceman) object;
        if ((this.servicemanId == null && other.servicemanId != null) || (this.servicemanId != null && !this.servicemanId.equals(other.servicemanId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Serviceman[ id=" + servicemanId + " ]";
    }

}
