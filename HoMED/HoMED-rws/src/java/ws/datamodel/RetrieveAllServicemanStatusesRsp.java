/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

import ws.wrapper.ConditionStatusWrapper;
import java.util.List;

/**
 *
 * @author rycan
 */
public class RetrieveAllServicemanStatusesRsp {
    
    private List<ConditionStatusWrapper> conditionStatuses;

    public RetrieveAllServicemanStatusesRsp() {
    }

    public RetrieveAllServicemanStatusesRsp(List<ConditionStatusWrapper> conditionStatuses) {
        this.conditionStatuses = conditionStatuses;
    }

    public List<ConditionStatusWrapper> getConditionStatuses() {
        return conditionStatuses;
    }

    public void setConditionStatuses(List<ConditionStatusWrapper> conditionStatuses) {
        this.conditionStatuses = conditionStatuses;
    }
    
}
