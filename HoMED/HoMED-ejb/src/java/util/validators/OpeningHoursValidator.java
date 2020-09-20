/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.validators;

import entity.OperatingHours;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author sunag
 */
public class OpeningHoursValidator implements ConstraintValidator<OpeningHours, OperatingHours> {
    
    @Override
    public void initialize(OpeningHours constraintAnnotation) {
        
    }
    
    @Override
    public boolean isValid(OperatingHours hours, ConstraintValidatorContext context) {
        if (hours.getOpeningHours() == null && hours.getClosingHours() == null) {
            return true;
        } else if (hours.getOpeningHours().compareTo(hours.getClosingHours()) < 0) {
            return true;
        }
    
        return false;
    }
}
