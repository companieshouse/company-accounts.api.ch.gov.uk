package uk.gov.companieshouse.api.accounts.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = ValidRegulatoryStandardsImpl.class)
public @interface ValidRegulatoryStandards {

        String message() default "{mustMatch.frs101.or.frs102}";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
}
