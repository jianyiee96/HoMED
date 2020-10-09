/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

import java.util.Date;


public class QueryBookingSlotsReq {
    
    Long medicalCentreId;
    Date queryDate;

    public QueryBookingSlotsReq() {
    }

    public QueryBookingSlotsReq(Long medicalCentreId, Date queryDate) {
        this.medicalCentreId = medicalCentreId;
        this.queryDate = queryDate;
    }

    public Long getMedicalCentreId() {
        return medicalCentreId;
    }

    public void setMedicalCentreId(Long medicalCentreId) {
        this.medicalCentreId = medicalCentreId;
    }

    public Date getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(Date queryDate) {
        this.queryDate = queryDate;
    }
    
}
