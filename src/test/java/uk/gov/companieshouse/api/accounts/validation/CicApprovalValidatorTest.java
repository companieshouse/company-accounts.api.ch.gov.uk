package uk.gov.companieshouse.api.accounts.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.rest.LastAccounts;
import uk.gov.companieshouse.api.accounts.model.rest.CicApproval;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CicApprovalValidatorTest {

    private Errors errors;

    private CicApprovalValidator validator;

    private CicApproval cicApproval;

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @BeforeEach
    void setup() {
        validator = new CicApprovalValidator();
        validator.dateInvalid = "date.invalid";
        cicApproval = new CicApproval();
        when(httpServletRequestMock.getAttribute(anyString())).thenReturn(createCompanyAccount());
    }

    @Test
    @DisplayName("Validate with a valid approval date ")
    void validateApprovalWithValidDate(){
        cicApproval.setDate(LocalDate.of(2018, Month.NOVEMBER, 2));
        errors = validator.validateCicReportApproval(cicApproval,httpServletRequestMock);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with approval date before period end on date")
    void validateApprovalDateBeforePeriodEnd(){
        cicApproval.setDate(LocalDate.of(2018, Month.OCTOBER, 2));
        errors = validator.validateCicReportApproval(cicApproval,httpServletRequestMock);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError("date.invalid", "$.cic_approval.date")));
    }

    @Test
    @DisplayName("Validate with approval date equal to period end on date")
    void validateApprovalDateSameAsPeriodEnd(){
        cicApproval.setDate(LocalDate.of(2018, Month.NOVEMBER, 1));
        errors = validator.validateCicReportApproval(cicApproval,httpServletRequestMock);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError("date.invalid", "$.cic_approval.date")));
    }
    private CompanyAccount createCompanyAccount(){
        CompanyAccount companyAccount = new CompanyAccount();
        LastAccounts accountingPeriod = new LastAccounts();
        accountingPeriod.setPeriodEndOn(LocalDate.of(2018, Month.NOVEMBER, 1));
        companyAccount.setNextAccounts(accountingPeriod);
        return  companyAccount;
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}

