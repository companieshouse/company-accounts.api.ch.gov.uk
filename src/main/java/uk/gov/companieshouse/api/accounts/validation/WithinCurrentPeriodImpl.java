package uk.gov.companieshouse.api.accounts.validation;

import java.time.LocalDate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.enumeration.AccountType;
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.parent.ParentResource;
import uk.gov.companieshouse.api.accounts.parent.ParentResourceFactory;

@Component
public class WithinCurrentPeriodImpl implements ConstraintValidator<WithinCurrentPeriod, LocalDate> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ParentResourceFactory<LinkType> parentResourceFactory;

    private AccountType accountType;

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null) {
            return true;
        }

        ParentResource<LinkType> parentResource = parentResourceFactory.getParentResource(accountType);

        LocalDate periodStart = parentResource.getPeriodStartOn(request);
        LocalDate periodEnd = parentResource.getPeriodEndOn(request);

        return !date.isBefore(periodStart) && !date.isAfter(periodEnd);
    }

    @Override
    public void initialize(WithinCurrentPeriod constraintAnnotation) {
        this.accountType = constraintAnnotation.accountType();
    }
}