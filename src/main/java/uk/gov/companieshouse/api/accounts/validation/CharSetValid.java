package uk.gov.companieshouse.api.accounts.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import uk.gov.companieshouse.charset.CharSet;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = CharSetValidatorImpl.class)
public @interface CharSetValid {

    String message() default "INVALID_VALUE";

    CharSet value() default CharSet.CHARECTER_SET_1;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}