package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Director;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsApproval;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Secretary;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorService;
import uk.gov.companieshouse.api.accounts.service.impl.SecretaryService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

public class DirectorsApprovalValidator extends BaseValidator{

    private static final String APPROVAL_PATH = "$.approval";
    private static final String DATE_PATH = APPROVAL_PATH + ".date";
    private static final String APPROVAL_NAME = APPROVAL_PATH + ".name";

    @Autowired
    private SecretaryService secretaryService;

    @Autowired
    private DirectorService directorService;

    public Errors validateApproval(DirectorsApproval directorsApproval, HttpServletRequest request) throws DataException {

        Errors errors = new Errors();

        CompanyAccount companyAccount = (CompanyAccount) request
                .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        LocalDate periodEndDate = companyAccount.getNextAccounts().getPeriodEndOn();
        LocalDate approvalDate = directorsApproval.getDate();

        String companyAccountId = (String) request.getAttribute("companyAccountId");
        ResponseObject<Secretary> secretaryResponseObject = secretaryService.find(companyAccountId, request);

        String secretary = Optional.of(secretaryResponseObject)
                .map(ResponseObject::getData)
                .map(Secretary::getName)
                .orElse(null);

        ResponseObject<Director> directorsReportResponseObject = directorService.findAll(transaction, companyAccountId, request);

        Director[] directors = Optional.of(directorsReportResponseObject)
                .map(ResponseObject::getDataForMultipleResources)
                .orElse(null);

        if(secretary != null || directors != null) {
            if(!secretary.equals(directorsApproval.getName()) &&
                    !Arrays.stream(directors).anyMatch(directorsApproval.getName()::equals)) {
                errors.addError(new Error(valueRequired, APPROVAL_NAME, LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType()));
            }
        }

        if (approvalDate.isBefore(periodEndDate) || approvalDate.isEqual(periodEndDate)) {
            errors.addError(new Error(dateInvalid, DATE_PATH, LocationType.JSON_PATH.getValue(),
                    ErrorType.VALIDATION.getType()));
        }

        return errors;
    }
}

