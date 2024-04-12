package uk.gov.companieshouse.api.accounts.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = WithinCurrentPeriodImpl.class)
public @interface WithinCurrentPeriod {

    AccountType accountType();
    String message() default "{date.outside.currentPeriod}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}