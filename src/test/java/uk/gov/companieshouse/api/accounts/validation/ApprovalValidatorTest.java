package uk.gov.companieshouse.api.accounts.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.rest.AccountingPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;
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
public class ApprovalValidatorTest {

    private Errors errors;

    private ApprovalValidator validator;

    private Approval approval ;

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @BeforeEach
    void setup() {

        validator = new ApprovalValidator();
        validator.dateInvalid = "date.invalid";
        approval = new Approval();
        when(httpServletRequestMock.getAttribute(anyString())).thenReturn(createCompanyAccount());
    }

    @Test
    @DisplayName("Validate with a valid approval date ")
    void validateApprovalWithValidDate(){
        approval.setDate(LocalDate.of(2018, Month.NOVEMBER, 2));
        errors = validator.validateApproval(approval,httpServletRequestMock);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with approval date before period end on date")
    void validateApprovalDateBeforePeriodEnd(){
        approval.setDate(LocalDate.of(2018, Month.OCTOBER, 2));
        errors = validator.validateApproval(approval,httpServletRequestMock);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError("date.invalid", "$.approval.date")));
    }

    @Test
    @DisplayName("Validate with approval date equal to period end on date")
    void validateApprovalDateSameAsPeriodEnd(){
        approval.setDate(LocalDate.of(2018, Month.NOVEMBER, 1));
        errors = validator.validateApproval(approval,httpServletRequestMock);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError("date.invalid", "$.approval.date")));
    }

    private CompanyAccount createCompanyAccount(){
        CompanyAccount companyAccount = new CompanyAccount();
        AccountingPeriod accountingPeriod = new AccountingPeriod();
        accountingPeriod.setPeriodEndOn(LocalDate.of(2018, Month.NOVEMBER, 1));
        companyAccount.setNextAccounts(accountingPeriod);
        return  companyAccount;
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}
