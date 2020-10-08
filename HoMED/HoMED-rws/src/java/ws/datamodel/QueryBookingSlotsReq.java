/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

import java.util.Date;


public class QueryBookingSlotsReq {
    
    Long medicalCenterId;
    Date queryDate;

    public QueryBookingSlotsReq() {
    }

    public QueryBookingSlotsReq(Long medicalCenterId, Date queryDate) {
        this.medicalCenterId = medicalCenterId;
        this.queryDate = queryDate;
    }

    public Long getMedicalCenterId() {
        return medicalCenterId;
    }

    public void setMedicalCenterId(Long medicalCenterId) {
        this.medicalCenterId = medicalCenterId;
    }

    public Date getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(Date queryDate) {
        this.queryDate = queryDate;
    }
    
}
