/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.DayOfWeekEnum;

@Entity
public class MedicalCentre implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medicalCentreId;

    @Column(nullable = false)
    @NotNull(message = "Please provide a name for the medical centre")
    @Size(min = 8, message = "Medical centre name must be at least of length 8")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "Please provide a telephone number for the medical centre")
    @Size(min = 8, max = 8, message = "Telephone number must be of length 8")
    private String phone;

    @Embedded
    @Column(nullable = false)
    @NotNull
    private Address address;

    @OneToMany
    private List<OperatingHours> operatingHours;

    public MedicalCentre() {
        this.address = new Address();
        this.operatingHours = new ArrayList<>(8);

        operatingHours.add(new OperatingHours(DayOfWeekEnum.MONDAY, Boolean.FALSE, LocalTime.of(8, 30), LocalTime.of(17, 30)));
        operatingHours.add(new OperatingHours(DayOfWeekEnum.TUESDAY, Boolean.FALSE, LocalTime.of(8, 30), LocalTime.of(17, 30)));
        operatingHours.add(new OperatingHours(DayOfWeekEnum.WEDNESDAY, Boolean.FALSE, LocalTime.of(8, 30), LocalTime.of(17, 30)));
        operatingHours.add(new OperatingHours(DayOfWeekEnum.THURSDAY, Boolean.FALSE, LocalTime.of(8, 30), LocalTime.of(17, 30)));
        operatingHours.add(new OperatingHours(DayOfWeekEnum.FRIDAY, Boolean.FALSE, LocalTime.of(8, 30), LocalTime.of(17, 30)));
        operatingHours.add(new OperatingHours(DayOfWeekEnum.SATURDAY, Boolean.FALSE, LocalTime.of(8, 30), LocalTime.of(13, 30)));
        operatingHours.add(new OperatingHours(DayOfWeekEnum.SUNDAY, Boolean.TRUE, null, null));
        operatingHours.add(new OperatingHours(DayOfWeekEnum.HOLIDAY, Boolean.TRUE, null, null));
    }

    public MedicalCentre(String name, String phone, Address address, List<OperatingHours> operatingHours) {
        this();
        
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.operatingHours = operatingHours;
    }

    public Long getMedicalCentreId() {
        return medicalCentreId;
    }

    public void setMedicalCentreId(Long medicalCentreId) {
        this.medicalCentreId = medicalCentreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<OperatingHours> getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(List<OperatingHours> operatingHours) {
        this.operatingHours = operatingHours;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (medicalCentreId != null ? medicalCentreId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the medicalCentreId fields are not set
        if (!(object instanceof MedicalCentre)) {
            return false;
        }
        MedicalCentre other = (MedicalCentre) object;
        if ((this.medicalCentreId == null && other.medicalCentreId != null) || (this.medicalCentreId != null && !this.medicalCentreId.equals(other.medicalCentreId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.MedicalCentre[ id=" + medicalCentreId + " ]";
    }

}
