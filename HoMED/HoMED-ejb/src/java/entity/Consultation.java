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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import util.enumeration.ConsultationStatusEnum;

@Entity
public class Consultation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long consultationId;

    @OneToOne(optional = false)
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column
    private ConsultationStatusEnum consultationStatusEnum;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date joinQueueDateTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date startDateTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date endDateTime;

    @Column(nullable = true, length = 8000)
    private String remarks;

    @Column(nullable = true, length = 8000)
    private String remarksForServiceman;

    @ManyToOne(optional = true)
    private MedicalOfficer medicalOfficer;
    
    
    public Consultation() {
        this.consultationStatusEnum = ConsultationStatusEnum.WAITING;
        this.joinQueueDateTime = new Date();
    }

    public Long getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(Long consultationId) {
        this.consultationId = consultationId;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public ConsultationStatusEnum getConsultationStatusEnum() {
        return consultationStatusEnum;
    }

    public void setConsultationStatusEnum(ConsultationStatusEnum consultationStatusEnum) {
        this.consultationStatusEnum = consultationStatusEnum;
    }

    public Date getJoinQueueDateTime() {
        return joinQueueDateTime;
    }

    public void setJoinQueueDateTime(Date joinQueueDateTime) {
        this.joinQueueDateTime = joinQueueDateTime;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRemarksForServiceman() {
        return remarksForServiceman;
    }

    public void setRemarksForServiceman(String remarksForServiceman) {
        this.remarksForServiceman = remarksForServiceman;
    }

    public MedicalOfficer getMedicalOfficer() {
        return medicalOfficer;
    }

    public void setMedicalOfficer(MedicalOfficer medicalOfficer) {
        this.medicalOfficer = medicalOfficer;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (consultationId != null ? consultationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the consultationId fields are not set
        if (!(object instanceof Consultation)) {
            return false;
        }
        Consultation other = (Consultation) object;
        if ((this.consultationId == null && other.consultationId != null) || (this.consultationId != null && !this.consultationId.equals(other.consultationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Consultation [ id: " + consultationId + " ]";
    }

}
