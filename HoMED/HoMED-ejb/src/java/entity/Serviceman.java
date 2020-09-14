/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
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
    @NotNull(message = "Name must be between length 2 to 128")
    @Size(min = 2, max = 128, message = "First Name must be between length 2 to 128")
    private String name;

    @Column(nullable = false, unique = true, length = 9)
    @NotNull(message = "NRIC must be of length 9")
    @Size(min = 9, max = 9, message = "NRIC must be of length 9")
    private String nric;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
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
    @NotNull(message = "Password must be between length 8 to 64")
    @Size(min = 8, max = 64, message = "Password must be between length 8 to 64")
    private String password;
    
    @Column(nullable = false, unique = true, length = 64)
    @NotNull(message = "Proper formatted email with length no more than 64 must be provided")
    @Size(max = 64, message = "Proper formatted email with length no more than 64 must be provided")
    @Email(message = "Proper formatted email with length no more than 64 must be provided")
    private String email;

    @Column(nullable = false)
    @NotNull(message = "Address must be at least of length 8")
    @Size(min = 8, message = "Address must be at least of length 8")
    private String address;
    
    @Column(nullable = false)
    @NotNull
    private Boolean isActivated;

    @Column(columnDefinition = "CHAR(32) NOT NULL")
    @NotNull
    private String salt;

    public Serviceman() {
        this.isActivated = false;
        this.password = CryptographicHelper.getInstance().generateRandomString(8);
        this.salt = CryptographicHelper.getInstance().generateRandomString(32);
    }

    public Serviceman(String name, String nric, Date ord, GenderEnum gender, BloodTypeEnum bloodType, String email, String address) {
        this();
        this.name = name;
        this.nric = nric;
        this.rod = ord;
        this.gender = gender;
        this.bloodType = bloodType;
        System.out.println("Serviceman " + nric + " has OTP: " + password);
        setPassword(password);
        this.email = email;
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

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
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
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
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
