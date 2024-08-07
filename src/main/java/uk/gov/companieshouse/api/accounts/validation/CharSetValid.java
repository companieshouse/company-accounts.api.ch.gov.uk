package uk.gov.companieshouse.api.accounts.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uk.gov.companieshouse.charset.CharSet;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = CharSetValidatorImpl.class)
public @interface CharSetValid {

    String message() default "{invalid.characters.entered}";
    CharSet value() default CharSet.CHARACTER_SET_1;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}