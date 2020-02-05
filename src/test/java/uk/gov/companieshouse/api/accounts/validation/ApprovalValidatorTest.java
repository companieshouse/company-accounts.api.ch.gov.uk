package uk.gov.companieshouse.api.accounts.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.AccountingPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Director;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApprovalValidatorTest {

    private static final String NAME = "directorName";
    private static final String COMPANY_ACCOUNTS = "companyAccountsId";
    private static final String INVALID_VALUE = "invalidValue";
    private static final String OTHER_NAME = "otherName";

    private Errors errors;

    private Approval approval ;

    @Mock
    private DirectorService directorService;

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @InjectMocks
    private ApprovalValidator validator;

    @BeforeEach
    void setup() {

        validator.dateInvalid = "date.invalid";
        approval = new Approval();
       // when(httpServletRequestMock.getAttribute("transaction")).thenReturn(transaction);
        when(httpServletRequestMock.getAttribute(anyString())).thenReturn(createCompanyAccount());
    }

    @Test
    @DisplayName("Validate with a valid approval date ")
    void validateApprovalWithValidDate() throws DataException {

        approval.setDate(LocalDate.of(2018, Month.NOVEMBER, 2));
        errors = validator.validateApproval(approval,transaction, COMPANY_ACCOUNTS, httpServletRequestMock);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with approval date before period end on date")
    void validateApprovalDateBeforePeriodEnd() throws DataException {

        approval.setDate(LocalDate.of(2018, Month.OCTOBER, 2));
        errors = validator.validateApproval(approval,transaction, COMPANY_ACCOUNTS, httpServletRequestMock);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError("date.invalid", "$.approval.date")));
    }

    @Test
    @DisplayName("Validate with approval date equal to period end on date")
    void validateApprovalDateSameAsPeriodEnd() throws DataException {

        approval.setDate(LocalDate.of(2018, Month.NOVEMBER, 1));
        errors = validator.validateApproval(approval,transaction, COMPANY_ACCOUNTS, httpServletRequestMock);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError("date.invalid", "$.approval.date")));
    }

    @Test
    @DisplayName("Validate with approval name same as directors report directors")
    void validateApprovalNameSameAsDirectorsReport() throws DataException {

        approval.setDate(LocalDate.of(2018, Month.DECEMBER, 1));
        approval.setName(OTHER_NAME);

        ReflectionTestUtils.setField(validator, INVALID_VALUE, INVALID_VALUE);

        when(directorService.findAll(transaction, COMPANY_ACCOUNTS, httpServletRequestMock)).thenReturn(new ResponseObject<>(ResponseStatus.FOUND, createDirectors()));

        errors = validator.validateApproval(approval,transaction, COMPANY_ACCOUNTS, httpServletRequestMock);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INVALID_VALUE, "$.approval.name")));
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

    private Director[] createDirectors() {

        Director[] directors = new Director[1];
        directors[0] = new Director();
        directors[0].setName(NAME);

        return directors;
    }
}
