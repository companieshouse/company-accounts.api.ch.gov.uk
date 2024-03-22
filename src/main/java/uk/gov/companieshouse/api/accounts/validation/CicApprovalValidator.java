package uk.gov.companieshouse.api.accounts.validation;

import java.time.LocalDate;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.CicApproval;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.parent.ParentResourceFactory;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.utility.AccountTypeFactory;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class CicApprovalValidator extends BaseValidator {

	private final AccountTypeFactory accountTypeFactory;

    private final ParentResourceFactory parentResourceFactory;

    private static final String APPROVAL_PATH = "$.cic_approval";
    private static final String DATE_PATH = APPROVAL_PATH + ".date";

    @Autowired
    public CicApprovalValidator(CompanyService companyService,
                                AccountTypeFactory accountTypeFactory,
                                ParentResourceFactory parentResourceFactory) {
        super(companyService);
        this.accountTypeFactory = accountTypeFactory;
        this.parentResourceFactory = parentResourceFactory;
    }
    
    public Errors validateCicReportApproval(CicApproval cicApproval,
                                            String companyAccountsId,
                                            HttpServletRequest request) throws DataException {
        Errors errors = new Errors();

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        CompanyAccount companyAccount = (CompanyAccount) request.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());

        try {
            CompanyProfileApi companyProfile = getCompanyService().getCompanyProfile(transaction.getCompanyNumber());

            // Take period end date from the company profile
            LocalDate periodEndDate = companyProfile.getAccounts().getNextAccounts().getPeriodEndOn();

            AccountType accountType = null;

            // Stream over the company accounts links to check if an associated account type exists
            for (String companyAccountLinkType : companyAccount.getLinks().keySet()) {
                accountType = accountTypeFactory.getAccountTypeForCompanyAccountLinkType(companyAccountLinkType);
                if (accountType != null) {
                    break;
                }
            }

            // If an account type does exist, derive the period end date from the account type in case it has been edited via the ARD functionality
            if (accountType != null) {
                periodEndDate = parentResourceFactory.getParentResource(accountType)
                        .getPeriodEndOn(companyAccountsId, request);
            }

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
