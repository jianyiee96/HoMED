package jsf.validator;

import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("phoneNumberValidator")
public class PhoneNumberValidator implements Validator {

    private Pattern pattern;

    private static final String PATTERN
            = "^[89]\\d{7}$";

    public PhoneNumberValidator() {
        pattern = Pattern.compile(PATTERN);
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String strValue = value.toString();

        if (value == null || strValue.equals("")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error",
                    "Phone number is required"));
        }

        if (!pattern.matcher(strValue).matches()) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error",
                    strValue + "is not a valid phone number. Enter your 8 digit phone number"));
        }
    }
}
