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
@Constraint(validatedBy = WithinSetDaysOfPeriodEndImpl.class)
public @interface WithinSetDaysOfPeriodEnd {

    int numOfDays();
    boolean allowNulls() default true;
    String message() default "{date.outsideRange.periodEnd}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
