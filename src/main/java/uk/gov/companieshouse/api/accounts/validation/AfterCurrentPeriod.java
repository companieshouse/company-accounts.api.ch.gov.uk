package uk.gov.companieshouse.api.accounts.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = AfterCurrentPeriodImpl.class)
public @interface AfterCurrentPeriod {

    AccountType accountType();
    String message() default "{date.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
