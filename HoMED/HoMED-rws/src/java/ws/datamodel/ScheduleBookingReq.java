/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

public class ScheduleBookingReq {
    
    Long servicemanId;
    Long consultationPurposeId;
    Long bookingSlotId;
    String bookingComment;
    Boolean isForReview;

    public ScheduleBookingReq() {
    }

    public ScheduleBookingReq(Long servicemanId, Long consultationPurposeId, Long bookingSlotId, String bookingComment, Boolean isForReview) {
        this.servicemanId = servicemanId;
        this.consultationPurposeId = consultationPurposeId;
        this.bookingSlotId = bookingSlotId;
        this.bookingComment = bookingComment;
        this.isForReview = isForReview;
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

    public Long getBookingSlotId() {
        return bookingSlotId;
    }

    public void setBookingSlotId(Long bookingSlotId) {
        this.bookingSlotId = bookingSlotId;
    }

    public String getBookingComment() {
        return bookingComment;
    }

    public void setBookingComment(String bookingComment) {
        this.bookingComment = bookingComment;
    }

    public Boolean getIsForReview() {
        return isForReview;
    }

    public void setIsForReview(Boolean isForReview) {
        this.isForReview = isForReview;
    }

}
