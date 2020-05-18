package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.CicApproval;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
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
    private CompanyService companyService;

    @BeforeEach
    void setup() throws ServiceException {
        validator = new CicApprovalValidator(companyService);
        validator.dateInvalid = "date.invalid";
        cicApproval = new CicApproval();
        transaction.setCompanyNumber(COMPANY_NUMBER);
        when(httpServletRequestMock.getAttribute(anyString())).thenReturn(transaction);
        when(companyService.getCompanyProfile(transaction.getCompanyNumber())).thenReturn(createCompanyProfile());

    }

    @Test
    @DisplayName("Validate with a valid approval date ")
    void validateApprovalWithValidDate() throws DataException{
        cicApproval.setDate(LocalDate.of(2018, Month.NOVEMBER, 2));
        errors = validator.validateCicReportApproval(cicApproval,httpServletRequestMock);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with approval date before period end on date")
    void validateApprovalDateBeforePeriodEnd() throws DataException{
        cicApproval.setDate(LocalDate.of(2018, Month.OCTOBER, 2));
        errors = validator.validateCicReportApproval(cicApproval,httpServletRequestMock);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError("date.invalid", "$.cic_approval.date")));
    }

    @Test
    @DisplayName("Validate with approval date equal to period end on date")
    void validateApprovalDateSameAsPeriodEnd() throws DataException{
        cicApproval.setDate(LocalDate.of(2018, Month.NOVEMBER, 1));
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
}

