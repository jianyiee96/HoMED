/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.ConditionStatus;
import entity.PreDefinedConditionStatus;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author User
 */
@Local
public interface ConditionStatusSessionBeanLocal {
    
    public List<ConditionStatus> retrieveActiveConditionStatusByServiceman(Long servicemanId);
    
    public List<PreDefinedConditionStatus> retrieveAllPreDefinedConditionStatus();
    
    public void addPreDefinedConditionStatus(String description);
    
    public void removePreDefinedConditionStatus(Long preDefinedConditionStatusId);
    
}
