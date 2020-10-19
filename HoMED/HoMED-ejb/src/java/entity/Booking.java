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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import util.enumeration.BookingStatusEnum;

@Entity
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    private String bookingComment;

    private String cancellationComment;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Serviceman serviceman;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private ConsultationPurpose consultationPurpose;

    @OneToOne(optional = true)
    private Consultation consultation;

    @OneToOne(optional = false)
    private BookingSlot bookingSlot;

    @OneToMany
    private List<FormInstance> formInstances;

    @Enumerated(EnumType.STRING)
    @Column
    private BookingStatusEnum bookingStatusEnum;

    public Booking() {
        this.formInstances = new ArrayList<>();
        this.bookingStatusEnum = BookingStatusEnum.UPCOMING;
    }

    public Booking(Serviceman serviceman, ConsultationPurpose consultationPurpose, BookingSlot bookingSlot, String bookingComment) {
        this();
        this.serviceman = serviceman;
        this.consultationPurpose = consultationPurpose;
        this.bookingSlot = bookingSlot;
        this.bookingComment = bookingComment;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getBookingComment() {
        return bookingComment;
    }

    public void setBookingComment(String bookingComment) {
        this.bookingComment = bookingComment;
    }

    public String getCancellationComment() {
        return cancellationComment;
    }

    public void setCancellationComment(String cancellationComment) {
        this.cancellationComment = cancellationComment;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Serviceman getServiceman() {
        return serviceman;
    }

    public void setServiceman(Serviceman serviceman) {
        this.serviceman = serviceman;
    }

    public ConsultationPurpose getConsultationPurpose() {
        return consultationPurpose;
    }

    public void setConsultationPurpose(ConsultationPurpose consultationPurpose) {
        this.consultationPurpose = consultationPurpose;
    }

    public BookingSlot getBookingSlot() {
        return bookingSlot;
    }

    public void setBookingSlot(BookingSlot bookingSlot) {
        this.bookingSlot = bookingSlot;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public List<FormInstance> getFormInstances() {
        return formInstances;
    }

    public void setFormInstances(List<FormInstance> formInstances) {
        this.formInstances = formInstances;
    }

    public BookingStatusEnum getBookingStatusEnum() {
        return bookingStatusEnum;
    }

    public void setBookingStatusEnum(BookingStatusEnum bookingStatusEnum) {
        this.bookingStatusEnum = bookingStatusEnum;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bookingId != null ? bookingId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the bookingId fields are not set
        if (!(object instanceof Booking)) {
            return false;
        }
        Booking other = (Booking) object;
        if ((this.bookingId == null && other.bookingId != null) || (this.bookingId != null && !this.bookingId.equals(other.bookingId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Booking[ id=" + bookingId + " ]";
    }

}
