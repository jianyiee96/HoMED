/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

    @Column(nullable = false)
    @NotNull(message = "Please provide address for the medical centre")
    @Size(min = 8, message = "Address must be at least of length 8")
    private String address;

    @Transient
    @NotNull(message = "Please provide street name for the address")
    @Size(min = 6, message = "Street name must be at least of length 6")
    private String streetName = "";

    @Transient
    private String unitNumber = "";

    @Transient
    private String buildingName = "";

    @Transient
    private String country = "";

    @Transient
    @NotNull(message = "Please provide valid postal code")
    @Size(min = 6, max = 6, message = "Please provide valid postal code")
    private String postal = "";

    @Transient
    private String delimitedAddress;

    @OneToMany
    private List<OperatingHours> operatingHours;

    public MedicalCentre() {
        this.operatingHours = new ArrayList<>();
    }

    public MedicalCentre(String name, String phone, String address, List<OperatingHours> operatingHours) {
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

    public void foo() {
        String[] delimitedAddressArray = address.split("!!!@@!!!");

        streetName = delimitedAddressArray[0];
        unitNumber = delimitedAddressArray[1];
        buildingName = delimitedAddressArray[2];
        country = delimitedAddressArray[3];
        postal = delimitedAddressArray[4];

        delimitedAddress = streetName;

        if (!unitNumber.equals("")) {
            delimitedAddress += ", " + unitNumber;
        }

        if (!buildingName.equals("")) {
            delimitedAddress += ", " + buildingName;
        }

        if (!country.equals("")) {
            delimitedAddress += ", " + country;
        }

        if (!postal.equals("")) {
            delimitedAddress += " " + postal;
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public String getDelimitedAddress() {
        return delimitedAddress;
    }

    public void setDelimitedAddress(String delimitedAddress) {
        this.delimitedAddress = delimitedAddress;
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
