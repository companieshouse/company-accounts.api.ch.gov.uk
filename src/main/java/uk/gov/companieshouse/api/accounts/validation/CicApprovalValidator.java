package uk.gov.companieshouse.api.accounts.validation;

import java.time.LocalDate;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.rest.CicApproval;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

@Component
public class CicApprovalValidator extends BaseValidator {

    private static final String APPROVAL_PATH = "$.cic-approval";
    private static final String DATE_PATH = APPROVAL_PATH + ".date";

    public Errors validateCicReportApproval(CicApproval cicApproval, HttpServletRequest request) {

        Errors errors = new Errors();

        CompanyAccount companyAccount = (CompanyAccount) request
            .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());

        LocalDate periodEndDate = companyAccount.getNextAccounts().getPeriodEndOn();
        LocalDate approvalDate = cicApproval.getDate();

        if (approvalDate.isBefore(periodEndDate) || approvalDate.isEqual(periodEndDate)) {
            errors.addError(new Error(dateInvalid, DATE_PATH, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType()));
        }

        return errors;
    }

}
