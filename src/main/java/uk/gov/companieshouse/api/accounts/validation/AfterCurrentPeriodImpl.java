package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import uk.gov.companieshouse.api.accounts.parent.ParentResourceFactory;

@Component
public class AfterCurrentPeriodImpl implements ConstraintValidator<AfterCurrentPeriod, LocalDate> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ParentResourceFactory parentResourceFactory;

    private AccountType accountType;

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }

        LocalDate periodEnd = parentResourceFactory.getParentResource(accountType).getPeriodEndOn(request);

        return date.isAfter(periodEnd);

    }

    @Override
    public void initialize(AfterCurrentPeriod constraintAnnotation) {
        this.accountType = constraintAnnotation.accountType();
    }
}
