/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-war
 */
package jsf.classes;

import entity.FormTemplate;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author User
 */
@FacesConverter(value = "formTemplateConverter", forClass = FormTemplate.class)
public class FormTemplateConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {

        Long id = Long.parseLong(string.substring(string.lastIndexOf("[") + 1, string.length() - 1));
        List<FormTemplate> formTemplates = (List<FormTemplate>) fc.getExternalContext().getSessionMap().get("ConsultationPurposeUtilityManagedBean.allFormTemplates");

        for (FormTemplate ft : formTemplates) {
            if (ft.getFormTemplateId().equals(id)) {
                return ft;
            }
        }

        return null;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        return o.toString();
    }

}
