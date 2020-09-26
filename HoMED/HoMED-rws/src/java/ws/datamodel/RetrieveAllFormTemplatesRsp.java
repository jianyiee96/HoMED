/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-rws
 */
package ws.datamodel;

import entity.FormTemplate;
import java.util.List;

/**
 *
 * @author User
 */
public class RetrieveAllFormTemplatesRsp {

    List<FormTemplate> formTemplates;

    public RetrieveAllFormTemplatesRsp() {
    }

    public RetrieveAllFormTemplatesRsp(List<FormTemplate> formTemplates) {
        this.formTemplates = formTemplates;
    }

    public List<FormTemplate> getFormTemplates() {
        return formTemplates;
    }

    public void setFormTemplates(List<FormTemplate> formTemplates) {
        this.formTemplates = formTemplates;
    }

}
