/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 *
 * @author sunag
 */
@Documented
@Constraint(validatedBy = OpeningHoursValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OpeningHours {

    String message() default "Opening hour has to be befoe closing hour!";
//    String message() default "{org.hibernate.validator.referenceguide.chapter06.CheckCase." +
//            "message}";
    
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
