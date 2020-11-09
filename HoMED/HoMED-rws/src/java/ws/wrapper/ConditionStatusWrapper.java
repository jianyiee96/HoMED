/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.wrapper;

import entity.ConditionStatus;
import java.util.Date;

/**
 *
 * @author User
 */
public class ConditionStatusWrapper {
    
    public ConditionStatus conditionStatus;
    
    public Long medicalBoardCaseId;
    
    public Date conditionStartDate;

    public ConditionStatusWrapper() {
    }

    public ConditionStatusWrapper(ConditionStatus conditionStatus, Long medicalBoardCaseId, Date conditionStartDate) {
        this.conditionStatus = conditionStatus;
        this.medicalBoardCaseId = medicalBoardCaseId;
        this.conditionStartDate = conditionStartDate;
    }

    public ConditionStatus getConditionStatus() {
        return conditionStatus;
    }

    public void setConditionStatus(ConditionStatus conditionStatus) {
        this.conditionStatus = conditionStatus;
    }

    public Long getMedicalBoardCaseId() {
        return medicalBoardCaseId;
    }

    public void setMedicalBoardCaseId(Long medicalBoardCaseId) {
        this.medicalBoardCaseId = medicalBoardCaseId;
    }

    public Date getConditionStartDate() {
        return conditionStartDate;
    }

    public void setConditionStartDate(Date conditionStartDate) {
        this.conditionStartDate = conditionStartDate;
    }
    
}
