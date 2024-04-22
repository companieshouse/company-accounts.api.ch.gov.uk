package uk.gov.companieshouse.api.accounts.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = ValidRptTransactionTypeImpl.class)
public @interface ValidRptTransactionType {

    String message() default "{mustMatch.money.given.or.received.by.related.party}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
