package uk.gov.companieshouse.api.accounts.validation;

import java.time.LocalDate;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;

@Component
public class WithinCurrentPeriodImpl implements ConstraintValidator<WithinCurrentPeriod, LocalDate> {

    @Autowired
    private HttpServletRequest request;

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {

        if (date == null) {
            return true;
        }

        CompanyAccount companyAccount = (CompanyAccount) request.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());

        LocalDate periodStart = companyAccount.getNextAccounts().getPeriodStartOn();
        LocalDate periodEnd = companyAccount.getNextAccounts().getPeriodEndOn();

        return !date.isBefore(periodStart) && !date.isAfter(periodEnd);
    }

    @Override
    public void initialize(WithinCurrentPeriod constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}