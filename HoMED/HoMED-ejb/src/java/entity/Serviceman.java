/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
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
import util.enumeration.PesStatusEnum;
import util.enumeration.ServicemanRoleEnum;
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
    @Size(max = 64, message = "Email must be of length less than 64")
    @Email(message = "Proper formatted Email must be provided")
    private String email;

    @Column(nullable = false, length = 8)
    @NotNull(message = "Phone Number must be provided")
    @Size(min = 8, max = 8, message = "Phone Number must be of length 8")
    @Pattern(regexp = "^[689]\\d{7}", message = "Proper formatted Phone Number must be provided")
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

    @OneToMany
    private List<Booking> bookings;

    @OneToMany
    private List<Notification> notifications;

    @OneToMany
    private List<ConditionStatus> conditionStatuses;

    @Embedded
    @Column(nullable = false)
    @NotNull(message = "Address must be provided")
    private Address address;

    @Column(nullable = false)
    @NotNull
    private Boolean isActivated;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Role must be provided")
    protected ServicemanRoleEnum role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Pes status must be provided")
    protected PesStatusEnum pesStatus;

    @Column(columnDefinition = "CHAR(32) NOT NULL")
    @NotNull
    private String salt;

    @Column(columnDefinition = "CHAR(32)")
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    private Date tokenExp;

    @Column(unique = true)
    private String fcmToken;

    public Serviceman() {
        this.isActivated = false;
        this.salt = CryptographicHelper.getInstance().generateRandomString(32);
        this.address = new Address();
        this.formInstances = new ArrayList<>();
        this.bookings = new ArrayList<>();
        this.notifications = new ArrayList<>();
        this.conditionStatuses = new ArrayList<>();
    }

    // Cloning
    public Serviceman(Serviceman another) {
        this.name = another.name;
        this.email = another.email;
        this.phoneNumber = another.phoneNumber;
        this.rod = another.rod;
        this.gender = another.gender;
        this.bloodType = another.bloodType;
        this.password = another.password;
        this.formInstances = another.formInstances;
        this.address = another.address;
        this.isActivated = another.isActivated;
        this.role = another.role;
        this.salt = another.salt;
    }

    public Serviceman(String name, String password, String email, String phoneNumber, ServicemanRoleEnum role, PesStatusEnum pesStatus, Date ord, GenderEnum gender, BloodTypeEnum bloodType, Address address) {
        this();
        this.name = name;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.pesStatus = pesStatus;
        this.rod = ord;
        this.gender = gender;
        this.bloodType = bloodType;
        this.address = address;
        setPassword(password);
    }

    public Serviceman(String name, String email, String phoneNumber, ServicemanRoleEnum role, PesStatusEnum pesStatus, Date ord, GenderEnum gender, BloodTypeEnum bloodType, Address address) {
        this();
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.pesStatus = pesStatus;
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

    public void setHashPassword(String password) {
        this.password = password;
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

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<ConditionStatus> getConditionStatuses() {
        return conditionStatuses;
    }

    public void setConditionStatuses(List<ConditionStatus> conditionStatuses) {
        this.conditionStatuses = conditionStatuses;
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

    public ServicemanRoleEnum getRole() {
        return role;
    }

    public void setRole(ServicemanRoleEnum role) {
        this.role = role;
    }

    public PesStatusEnum getPesStatus() {
        return pesStatus;
    }

    public void setPesStatus(PesStatusEnum pesStatus) {
        this.pesStatus = pesStatus;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getTokenExp() {
        return tokenExp;
    }

    public void setTokenExp(Date tokenExp) {
        this.tokenExp = tokenExp;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @Override
    public String toString() {
        return "Serviceman [ id: " + servicemanId + " ]";
    }

}
