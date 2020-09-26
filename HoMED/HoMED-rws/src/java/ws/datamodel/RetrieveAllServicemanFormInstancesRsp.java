/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

import entity.FormInstance;
import java.util.List;

/**
 *
 * @author User
 */
public class RetrieveAllServicemanFormInstancesRsp {
    
    List<FormInstance> formInstances;

    public RetrieveAllServicemanFormInstancesRsp() {
    }
    
    public RetrieveAllServicemanFormInstancesRsp(List<FormInstance> formInstances) {
        this.formInstances = formInstances;
    }
    
    public List<FormInstance> getFormInstances() {
        return formInstances;
    }

    public void setFormInstances(List<FormInstance> formInstances) {
        this.formInstances = formInstances;
    }
    
}
