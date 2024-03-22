package uk.gov.companieshouse.api.accounts.validation;

import java.time.LocalDate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.exception.UncheckedDataException;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class WithinSetDaysOfPeriodEndImpl implements ConstraintValidator<WithinSetDaysOfPeriodEnd, LocalDate> {

    private int numOfDays;
    private boolean allowNulls;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CompanyService companyService;
    
    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (allowNulls && date == null) {
            return true;
        } else if (!allowNulls && date == null) {
        	return false;
        }

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            CompanyProfileApi companyProfile = companyService.getCompanyProfile(transaction.getCompanyNumber());

            LocalDate periodEnd = companyProfile.getAccounts().getNextAccounts().getPeriodEndOn();

            return date.isBefore(periodEnd.plusDays(numOfDays)) && date.isAfter(periodEnd.minusDays(numOfDays));
        } catch (ServiceException e) {
            throw new UncheckedDataException("Error fetching company profile", e);
        }
    }

    @Override
    public void initialize(WithinSetDaysOfPeriodEnd constraintAnnotation) {
    	this.numOfDays = constraintAnnotation.numOfDays() + 1; // add one to so that if a user were to submit a date on the constraint, it is treated as valid
    	this.allowNulls = constraintAnnotation.allowNulls();
    }
}
