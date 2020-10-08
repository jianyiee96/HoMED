/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

public class ScheduleBookingReq {
    
    Long servicemanId;
    Long consultationPurposeId;
    Long medicalCentreId;
    Long bookingSlotId;

    public ScheduleBookingReq() {
    }

    public ScheduleBookingReq(Long servicemanId, Long consultationPurposeId, Long medicalCentreId, Long bookingSlotId) {
        this.servicemanId = servicemanId;
        this.consultationPurposeId = consultationPurposeId;
        this.medicalCentreId = medicalCentreId;
        this.bookingSlotId = bookingSlotId;
    }

    public Long getServicemanId() {
        return servicemanId;
    }

    public void setServicemanId(Long servicemanId) {
        this.servicemanId = servicemanId;
    }

    public Long getConsultationPurposeId() {
        return consultationPurposeId;
    }

    public void setConsultationPurposeId(Long consultationPurposeId) {
        this.consultationPurposeId = consultationPurposeId;
    }

    public Long getMedicalCentreId() {
        return medicalCentreId;
    }

    public void setMedicalCentreId(Long medicalCentreId) {
        this.medicalCentreId = medicalCentreId;
    }

    public Long getBookingSlotId() {
        return bookingSlotId;
    }

    public void setBookingSlotId(Long bookingSlotId) {
        this.bookingSlotId = bookingSlotId;
    }
    
}
