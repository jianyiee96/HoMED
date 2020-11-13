/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package ejb.session.stateless;

import entity.ConditionStatus;
import entity.PreDefinedConditionStatus;
import entity.PreDefinedConditionStatusGroup;
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
    public List<ConditionStatus> retrieveConditionStatusByServiceman(Long servicemanId) {

        Query query = em.createQuery("SELECT c FROM ConditionStatus c WHERE c.serviceman.servicemanId = :inServicemanId");
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
        if (pdcs != null) {

            List<PreDefinedConditionStatusGroup> conditionGroups = retrieveAllPreDefinedConditionStatusGroup();

            for (PreDefinedConditionStatusGroup g : conditionGroups) {
                PreDefinedConditionStatus toRemove = null;
                for (PreDefinedConditionStatus c : g.getStatuses()) {
                    if (c.equals(pdcs)) {
                        toRemove = c;
                        break;
                    }
                }
                if(toRemove != null) {
                    g.getStatuses().remove(toRemove);
                }
            }

            em.remove(pdcs);
        }
    }

    @Override
    public List<PreDefinedConditionStatusGroup> retrieveAllPreDefinedConditionStatusGroup() {

        Query query = em.createQuery("SELECT p FROM PreDefinedConditionStatusGroup p");

        List<PreDefinedConditionStatusGroup> list = query.getResultList();

        return list;

    }

    @Override
    public void createPreDefinedConditionStatusGroup(String preDefinedConditionStatusGroupName) {

        PreDefinedConditionStatusGroup p = new PreDefinedConditionStatusGroup(preDefinedConditionStatusGroupName);
        em.persist(p);

    }

    @Override
    public void updatePreDefinedConditionStatusGroupList(Long preDefinedConditionStatusGroupId, List<PreDefinedConditionStatus> list) {

        PreDefinedConditionStatusGroup p = em.find(PreDefinedConditionStatusGroup.class, preDefinedConditionStatusGroupId);
        if (p != null) {
            for (PreDefinedConditionStatus item : list) {
                em.merge(item);
            }
            p.setStatuses(list);
        }
    }

    @Override
    public void removePreDefinedConditionStatusGroup(Long preDefinedConditionStatusGroupId) {

        PreDefinedConditionStatusGroup p = em.find(PreDefinedConditionStatusGroup.class, preDefinedConditionStatusGroupId);
        if (p != null) {
            em.remove(p);
        }

    }

}
