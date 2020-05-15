package uk.gov.companieshouse.api.accounts.validation;

import java.time.LocalDate;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.CicApproval;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class CicApprovalValidator extends BaseValidator {

    @Autowired
    private CompanyService companyService;

    private static final String APPROVAL_PATH = "$.cic_approval";
    private static final String DATE_PATH = APPROVAL_PATH + ".date";

    public Errors validateCicReportApproval(CicApproval cicApproval, HttpServletRequest request) throws DataException {

        Errors errors = new Errors();

        Transaction transaction =
                (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            CompanyProfileApi companyProfile =
                    companyService.getCompanyProfile(transaction.getCompanyNumber());

            LocalDate periodEndDate = companyProfile.getAccounts().getNextAccounts().getPeriodEndOn();
            LocalDate approvalDate = cicApproval.getDate();

            if (approvalDate.isBefore(periodEndDate) || approvalDate.isEqual(periodEndDate)) {
                errors.addError(new Error(dateInvalid, DATE_PATH, LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType()));
            }

        } catch (ServiceException e) {
            throw new DataException(e);
        }

        return errors;
    }

}
