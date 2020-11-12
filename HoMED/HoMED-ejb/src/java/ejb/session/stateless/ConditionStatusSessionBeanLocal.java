/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.ConditionStatus;
import entity.PreDefinedConditionStatus;
import entity.PreDefinedConditionStatusGroup;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author User
 */
@Local
public interface ConditionStatusSessionBeanLocal {

    public List<ConditionStatus> retrieveActiveConditionStatusByServiceman(Long servicemanId);

    public List<ConditionStatus> retrieveConditionStatusByServiceman(Long servicemanId);

    public List<PreDefinedConditionStatus> retrieveAllPreDefinedConditionStatus();

    public void addPreDefinedConditionStatus(String description);

    public void removePreDefinedConditionStatus(Long preDefinedConditionStatusId);

    public List<PreDefinedConditionStatusGroup> retrieveAllPreDefinedConditionStatusGroup();

    public void createPreDefinedConditionStatusGroup(String preDefinedConditionStatusGroupName);

    public void updatePreDefinedConditionStatusGroupList(Long preDefinedConditionStatusGroupId, List<PreDefinedConditionStatus> list);

    public void removePreDefinedConditionStatusGroup(Long preDefinedConditionStatusGroupId);

}
