package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import util.enumeration.EmployeeRoleEnum;
import util.enumeration.GenderEnum;

@Entity
public class MedicalOfficer extends MedicalStaff implements Serializable {

    private Boolean isChairman;

    @OneToMany(mappedBy = "medicalOfficer")
    private List<Consultation> completedConsultations;

    @OneToMany(mappedBy = "signedBy")
    private List<FormInstance> signedFormInstances;

    @OneToOne(optional = true)
    private Consultation currentConsultation;

    @OneToMany(mappedBy = "chairman")
    private List<MedicalBoardSlot> medicalBoardSlotsAsChairman;

    @OneToMany(mappedBy = "medicalOfficerOne")
    private List<MedicalBoardSlot> medicalBoardSlotsAsMedicalOfficerOne;

    @OneToMany(mappedBy = "medicalOfficerTwo")
    private List<MedicalBoardSlot> medicalBoardSlotsAsMedicalOfficerTwo;

    public MedicalOfficer() {
        this.isChairman = Boolean.FALSE;
        
        this.completedConsultations = new ArrayList<>();
        this.signedFormInstances = new ArrayList<>();
    }

    public MedicalOfficer(String name, String password, String email, Address address, String phoneNumber, GenderEnum gender) {
        super(name, password, email, address, phoneNumber, gender);
        super.role = EmployeeRoleEnum.MEDICAL_OFFICER;

        this.completedConsultations = new ArrayList<>();
        this.signedFormInstances = new ArrayList<>();
    }

    public MedicalOfficer(String name, String email, Address address, String phoneNumber, GenderEnum gender) {
        super(name, email, address, phoneNumber, gender);
        super.role = EmployeeRoleEnum.MEDICAL_OFFICER;

        this.completedConsultations = new ArrayList<>();
        this.signedFormInstances = new ArrayList<>();
    }

    public MedicalOfficer(Employee e) {
        super(e.name, e.password, e.email, e.address, e.phoneNumber, e.gender);
        super.role = EmployeeRoleEnum.MEDICAL_OFFICER;

        this.completedConsultations = new ArrayList<>();
        this.signedFormInstances = new ArrayList<>();
    }

    public Boolean getIsChairman() {
        return isChairman;
    }

    public void setIsChairman(Boolean isChairman) {
        this.isChairman = isChairman;
    }

    public List<Consultation> getCompletedConsultations() {
        return completedConsultations;
    }

    public void setCompletedConsultations(List<Consultation> completedConsultations) {
        this.completedConsultations = completedConsultations;
    }

    public Consultation getCurrentConsultation() {
        return currentConsultation;
    }

    public void setCurrentConsultation(Consultation currentConsultation) {
        this.currentConsultation = currentConsultation;
    }

    public List<FormInstance> getSignedFormInstances() {
        return signedFormInstances;
    }

    public void setSignedFormInstances(List<FormInstance> signedFormInstances) {
        this.signedFormInstances = signedFormInstances;
    }

    public List<MedicalBoardSlot> getMedicalBoardSlotsAsChairman() {
        return medicalBoardSlotsAsChairman;
    }

    public void setMedicalBoardSlotsAsChairman(List<MedicalBoardSlot> medicalBoardSlotsAsChairman) {
        this.medicalBoardSlotsAsChairman = medicalBoardSlotsAsChairman;
    }

    public List<MedicalBoardSlot> getMedicalBoardSlotsAsMedicalOfficerOne() {
        return medicalBoardSlotsAsMedicalOfficerOne;
    }

    public void setMedicalBoardSlotsAsMedicalOfficerOne(List<MedicalBoardSlot> medicalBoardSlotsAsMedicalOfficerOne) {
        this.medicalBoardSlotsAsMedicalOfficerOne = medicalBoardSlotsAsMedicalOfficerOne;
    }

    public List<MedicalBoardSlot> getMedicalBoardSlotsAsMedicalOfficerTwo() {
        return medicalBoardSlotsAsMedicalOfficerTwo;
    }

    public void setMedicalBoardSlotsAsMedicalOfficerTwo(List<MedicalBoardSlot> medicalBoardSlotsAsMedicalOfficerTwo) {
        this.medicalBoardSlotsAsMedicalOfficerTwo = medicalBoardSlotsAsMedicalOfficerTwo;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MedicalOfficer)) {
            return false;
        }
        MedicalOfficer other = (MedicalOfficer) object;
        if ((super.getEmployeeId() == null && other.getEmployeeId() != null) || (super.getEmployeeId() != null && !super.getEmployeeId().equals(other.getEmployeeId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Medical Officer [ id: " + super.getEmployeeId() + " ]";
    }
}
