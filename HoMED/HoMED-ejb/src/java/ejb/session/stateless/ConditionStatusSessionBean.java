/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.ConditionStatus;
import entity.PreDefinedConditionStatus;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author User
 */
@Stateless
public class ConditionStatusSessionBean implements ConditionStatusSessionBeanLocal {

    @PersistenceContext(unitName = "HoMED-ejbPU")
    private EntityManager em;

    @Override
    public List<ConditionStatus> retrieveActiveConditionStatusByServiceman(Long servicemanId) {

        Query query = em.createQuery("SELECT c FROM ConditionStatus c WHERE c.serviceman.servicemanId = :inServicemanId AND c.isActive = TRUE");
        query.setParameter("inServicemanId", servicemanId);
        return query.getResultList();

    }
    
    @Override
    public List<PreDefinedConditionStatus> retrieveAllPreDefinedConditionStatus() {
        
        Query query = em.createQuery("SELECT p FROM PreDefinedConditionStatus p");
        
        List<PreDefinedConditionStatus> list = query.getResultList();
        
        return list;
        
    }

    @Override
    public void addPreDefinedConditionStatus(String description) {
        PreDefinedConditionStatus pdcs = new PreDefinedConditionStatus(description);
        em.persist(pdcs);

    }

    @Override
    public void removePreDefinedConditionStatus(Long preDefinedConditionStatusId) {
        PreDefinedConditionStatus pdcs = em.find(PreDefinedConditionStatus.class, preDefinedConditionStatusId);
        if(pdcs != null) {
            em.remove(pdcs);
        }
    }
    

}
