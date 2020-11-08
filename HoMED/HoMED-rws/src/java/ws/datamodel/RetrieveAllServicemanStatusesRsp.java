/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

import entity.ConditionStatus;
import java.util.List;

/**
 *
 * @author rycan
 */
public class RetrieveAllServicemanStatusesRsp {
    
    private List<ConditionStatus> conditionStatuses;

    public RetrieveAllServicemanStatusesRsp() {
    }

    public RetrieveAllServicemanStatusesRsp(List<ConditionStatus> conditionStatuses) {
        this.conditionStatuses = conditionStatuses;
    }

    public List<ConditionStatus> getConditionStatuses() {
        return conditionStatuses;
    }

    public void setConditionStatuses(List<ConditionStatus> conditionStatuses) {
        this.conditionStatuses = conditionStatuses;
    }
    
}
