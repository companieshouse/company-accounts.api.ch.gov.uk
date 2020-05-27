package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.CicApproval;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.parent.ParentResource;
import uk.gov.companieshouse.api.accounts.parent.ParentResourceFactory;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.utility.AccountTypeFactory;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.company.account.CompanyAccountApi;
import uk.gov.companieshouse.api.model.company.account.NextAccountsApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CicApprovalValidatorTest {

    private static final String COMPANY_NUMBER = "12345678";

	private Errors errors;

    private CicApprovalValidator validator;

    private CicApproval cicApproval;

    @Mock
    private HttpServletRequest httpServletRequestMock;
    
    @Mock
    private Transaction transaction;

    @Mock
    private CompanyAccount companyAccount;
    
    @Mock
    private CompanyService companyService;

    @Mock
    private AccountTypeFactory accountTypeFactory;

    @Mock
    private ParentResourceFactory parentResourceFactory;

    @Mock
    private ParentResource parentResource;

    private static final AccountType ACCOUNT_TYPE = AccountType.SMALL_FULL;

    private static final String ACCOUNT_TYPE_LINK = "accountTypeLink";

    private static final String NON_ACCOUNT_TYPE_LINK = "nonAccountTypeLink";

    @BeforeEach
    void setup() throws ServiceException {
        validator = new CicApprovalValidator(companyService, accountTypeFactory, parentResourceFactory);
        validator.dateInvalid = "date.invalid";
        cicApproval = new CicApproval();
        transaction.setCompanyNumber(COMPANY_NUMBER);
        doReturn(transaction).when(httpServletRequestMock).getAttribute(AttributeName.TRANSACTION.getValue());
        doReturn(companyAccount).when(httpServletRequestMock).getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
        when(companyService.getCompanyProfile(transaction.getCompanyNumber())).thenReturn(createCompanyProfile());
    }

    @Test
    @DisplayName("Validate with a valid approval date - no associated accounts")
    void validateApprovalWithValidDateNoAssociatedAccounts() throws DataException {
        when(companyAccount.getLinks()).thenReturn(getCompanyAccountLinks(false));
        cicApproval.setDate(LocalDate.of(2018, Month.NOVEMBER, 2));
        errors = validator.validateCicReportApproval(cicApproval,httpServletRequestMock);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with approval date before period end on date - no associated accounts")
    void validateApprovalDateBeforePeriodEndNoAssociatedAccounts() throws DataException {
        when(companyAccount.getLinks()).thenReturn(getCompanyAccountLinks(false));
        cicApproval.setDate(LocalDate.of(2018, Month.OCTOBER, 2));
        errors = validator.validateCicReportApproval(cicApproval,httpServletRequestMock);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError("date.invalid", "$.cic_approval.date")));
    }

    @Test
    @DisplayName("Validate with approval date equal to period end on date - no associated accounts")
    void validateApprovalDateSameAsPeriodEndNoAssociatedAccounts() throws DataException {
        when(companyAccount.getLinks()).thenReturn(getCompanyAccountLinks(false));
        cicApproval.setDate(LocalDate.of(2018, Month.NOVEMBER, 1));
        errors = validator.validateCicReportApproval(cicApproval,httpServletRequestMock);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError("date.invalid", "$.cic_approval.date")));
    }

    @Test
    @DisplayName("Validate with a valid approval date - has associated accounts")
    void validateApprovalWithValidDateHasAssociatedAccounts() throws DataException {
        when(companyAccount.getLinks()).thenReturn(getCompanyAccountLinks(true));
        doReturn(ACCOUNT_TYPE).when(accountTypeFactory).getAccountTypeForCompanyAccountLinkType(ACCOUNT_TYPE_LINK);
        when(parentResourceFactory.getParentResource(ACCOUNT_TYPE)).thenReturn(parentResource);
        when(parentResource.getPeriodEndOn(httpServletRequestMock)).thenReturn(LocalDate.of(2018, Month.OCTOBER, 30));
        cicApproval.setDate(LocalDate.of(2018, Month.OCTOBER, 31));
        errors = validator.validateCicReportApproval(cicApproval,httpServletRequestMock);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with approval date before period end on date - has associated accounts")
    void validateApprovalDateBeforePeriodEndHasAssociatedAccounts() throws DataException {
        when(companyAccount.getLinks()).thenReturn(getCompanyAccountLinks(true));
        doReturn(ACCOUNT_TYPE).when(accountTypeFactory).getAccountTypeForCompanyAccountLinkType(ACCOUNT_TYPE_LINK);
        when(parentResourceFactory.getParentResource(ACCOUNT_TYPE)).thenReturn(parentResource);
        when(parentResource.getPeriodEndOn(httpServletRequestMock)).thenReturn(LocalDate.of(2018, Month.NOVEMBER, 3));
        cicApproval.setDate(LocalDate.of(2018, Month.NOVEMBER, 2));
        errors = validator.validateCicReportApproval(cicApproval,httpServletRequestMock);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError("date.invalid", "$.cic_approval.date")));
    }

    @Test
    @DisplayName("Validate with approval date equal to period end on date - has associated accounts")
    void validateApprovalDateSameAsPeriodEndHasAssociatedAccounts() throws DataException {
        when(companyAccount.getLinks()).thenReturn(getCompanyAccountLinks(true));
        doReturn(ACCOUNT_TYPE).when(accountTypeFactory).getAccountTypeForCompanyAccountLinkType(ACCOUNT_TYPE_LINK);
        when(parentResourceFactory.getParentResource(ACCOUNT_TYPE)).thenReturn(parentResource);
        when(parentResource.getPeriodEndOn(httpServletRequestMock)).thenReturn(LocalDate.of(2018, Month.NOVEMBER, 2 ));
        cicApproval.setDate(LocalDate.of(2018, Month.NOVEMBER, 2));
        errors = validator.validateCicReportApproval(cicApproval,httpServletRequestMock);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError("date.invalid", "$.cic_approval.date")));
    }
    
    private CompanyProfileApi createCompanyProfile(){
    	CompanyProfileApi companyProfileApi = new CompanyProfileApi();
    	CompanyAccountApi companyAccountApi = new CompanyAccountApi();
        NextAccountsApi accountingPeriod = new NextAccountsApi();
        accountingPeriod.setPeriodEndOn(LocalDate.of(2018, Month.NOVEMBER, 1));
        companyAccountApi.setNextAccounts(accountingPeriod);
        companyProfileApi.setAccounts(companyAccountApi);
        return companyProfileApi;
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }

    private Map<String, String> getCompanyAccountLinks(boolean hasAssociatedAccounts) {

        Map<String, String> companyAccountsLinks = new HashMap<>();
        companyAccountsLinks.put(NON_ACCOUNT_TYPE_LINK, NON_ACCOUNT_TYPE_LINK);
        if (hasAssociatedAccounts) {
            companyAccountsLinks.put(ACCOUNT_TYPE_LINK, ACCOUNT_TYPE_LINK);
        }

        return companyAccountsLinks;
    }
}

