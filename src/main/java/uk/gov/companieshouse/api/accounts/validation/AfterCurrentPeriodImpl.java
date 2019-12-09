package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

@Component
public class AfterCurrentPeriodImpl implements ConstraintValidator<AfterCurrentPeriod, LocalDate> {

    @Autowired
    private HttpServletRequest request;

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }

        CompanyAccount companyAccount = (CompanyAccount) request.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
        LocalDate periodEnd = companyAccount.getNextAccounts().getPeriodEndOn();

        return date.isBefore(periodEnd) && date.isEqual(periodEnd);

    }

    @Override
    public void initialize(AfterCurrentPeriod constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
