/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.wrapper;

import entity.MedicalBoardCase;
import java.util.Date;
import java.util.List;

/**
 *
 * @author User
 */
public class MedicalBoardCaseWrapper {
    
    public MedicalBoardCase medicalBoardCase;
    
    public Date scheduledStartDate;
    
    public Date scheduledEndDate;
    
    public String chairman;
    
    public List<ConditionStatusWrapper> conditionStatuses;

    public MedicalBoardCaseWrapper() {
    }

    public MedicalBoardCaseWrapper(MedicalBoardCase medicalBoardCase, Date scheduledStartDate, Date scheduledEndDate, String chairman, List<ConditionStatusWrapper> conditionStatuses) {
        this.medicalBoardCase = medicalBoardCase;
        this.scheduledStartDate = scheduledStartDate;
        this.scheduledEndDate = scheduledEndDate;
        this.chairman = chairman;
        this.conditionStatuses = conditionStatuses;
    }

    public MedicalBoardCase getMedicalBoardCase() {
        return medicalBoardCase;
    }

    public void setMedicalBoardCase(MedicalBoardCase medicalBoardCase) {
        this.medicalBoardCase = medicalBoardCase;
    }

    public Date getScheduledStartDate() {
        return scheduledStartDate;
    }

    public void setScheduledStartDate(Date scheduledStartDate) {
        this.scheduledStartDate = scheduledStartDate;
    }

    public Date getScheduledEndDate() {
        return scheduledEndDate;
    }

    public void setScheduledEndDate(Date scheduledEndDate) {
        this.scheduledEndDate = scheduledEndDate;
    }

    public String getChairman() {
        return chairman;
    }

    public void setChairman(String chairman) {
        this.chairman = chairman;
    }

    public List<ConditionStatusWrapper> getConditionStatuses() {
        return conditionStatuses;
    }

    public void setConditionStatuses(List<ConditionStatusWrapper> conditionStatuses) {
        this.conditionStatuses = conditionStatuses;
    }
    
}
