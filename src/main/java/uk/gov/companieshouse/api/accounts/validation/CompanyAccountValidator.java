package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.exception.UncheckedDataException;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Director;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class CompanyAccountValidator extends BaseValidator {

    private static final String ERROR_PATH = "$.company_account";
    private static final int NUMBER_OF_DAYS = 8; // 7 days including the current date.

    @Autowired
    public CompanyAccountValidator(CompanyService companyService) {
        super(companyService);
    }

    public Errors validateCompanyAccount(Transaction transaction) throws DataException {

        Errors errors = new Errors();

        try {
            CompanyProfileApi companyProfile = super.getCompanyService()
                    .getCompanyProfile(transaction.getCompanyNumber());

            LocalDate periodStart = companyProfile.getAccounts().getNextAccounts().getPeriodStartOn();

            boolean result = LocalDate.now().isAfter(periodStart.minusDays(NUMBER_OF_DAYS));
            if (!result) {
                errors.addError(new Error(dateOutsideRange, ERROR_PATH, LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType()));
            }

        } catch (ServiceException e) {

            throw new DataException("Error fetching company profile", e);
        }

        return errors;
    }
}