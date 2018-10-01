package uk.gov.companieshouse.api.accounts.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import uk.gov.companieshouse.charset.CharSet;

@Documented
@Retention(RUNTIME)
@Target({FIELD, ANNOTATION_TYPE, PARAMETER})
@Constraint(validatedBy = CharSetValidatorImpl.class)
public @interface CharSetValid {

    String message() default "Its all gone wrong";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}