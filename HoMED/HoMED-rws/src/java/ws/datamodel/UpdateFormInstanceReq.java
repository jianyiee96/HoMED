/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

import entity.FormInstance;

/**
 *
 * @author User
 */
public class UpdateFormInstanceReq {
    
    FormInstance formInstance;

    public UpdateFormInstanceReq() {
    }

    public UpdateFormInstanceReq(FormInstance formInstance) {
        this.formInstance = formInstance;
    }

    public FormInstance getFormInstance() {
        return formInstance;
    }

    public void setFormInstance(FormInstance formInstance) {
        this.formInstance = formInstance;
    }
    
}
