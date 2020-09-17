/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package entity;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Embeddable
public class Address implements Serializable {

    @NotNull(message = "Please provide street name for the address")
    @Size(min = 6, message = "Street name must be at least of length 6")
    private String streetName;
    private String unitNumber;
    private String buildingName;
    private String country;
    @NotNull(message = "Please provide valid postal code")
    @Size(min = 6, max = 6, message = "Please provide valid postal code")
    private String postal;

    public Address() {
    }

    public Address(String streetName, String unitNumber, String buildingName, String country, String postal) {
        this.streetName = streetName;
        this.unitNumber = unitNumber;
        this.buildingName = buildingName;
        this.country = country;
        this.postal = postal;
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

    @Override
    public String toString() {
        String str = streetName;

        if (unitNumber != null && !unitNumber.trim().equals("")) {
            str += ", " + unitNumber;
        }

        if (buildingName != null && !buildingName.trim().equals("")) {
            str += ", " + buildingName;
        }

        if (country != null && !country.trim().equals("")) {
            str += ", " + country;
        }

        if (postal != null && !postal.trim().equals("")) {
            str += " " + postal;
        }
        
        return str;
    }

}
