package jsf.converter;

import entity.FormInstanceFieldValue;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("formInstanceFieldValueConverter")
public class FormInstanceFieldValueConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
        return new FormInstanceFieldValue(value);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        return value.toString();
    }
}
