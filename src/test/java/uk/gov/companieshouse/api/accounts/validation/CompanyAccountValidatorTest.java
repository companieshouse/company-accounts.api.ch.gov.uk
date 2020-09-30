package uk.gov.companieshouse.api.accounts.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.rest.NextAccounts;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Director;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.company.account.CompanyAccountApi;
import uk.gov.companieshouse.api.model.company.account.NextAccountsApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CompanyAccountValidatorTest {

    private static final String ERROR_PATH = "$.company_account";

    private Errors errors;

    @Mock
    private Transaction transaction;

    @Mock
    private BaseValidator baseValidator;

    @Mock
    private HttpServletRequest request;

    @Mock
    private CompanyAccount companyAccount;

    @Mock
    private CompanyService companyService;

    @Mock
    private CompanyProfileApi companyProfileApi;

    @Mock
    private CompanyAccountApi accounts;

    @Mock
    private NextAccountsApi nextAccounts;

    private CompanyAccountValidator validator;

    @BeforeEach
    void setup() {

        validator = new CompanyAccountValidator(companyService);
        validator.dateOutsideRange = "date.outside.range";
    }

    @Test
    @DisplayName("Validate with a valid date")
    void validateCompanyAccountWithValidDate() throws ServiceException, DataException {

        when(companyService.getCompanyProfile(transaction.getCompanyNumber())).thenReturn(companyProfileApi);

        when(companyProfileApi.getAccounts()).thenReturn(accounts);
        when(accounts.getNextAccounts()).thenReturn(nextAccounts);
        when(nextAccounts.getPeriodStartOn()).thenReturn(LocalDate.now());

        errors = validator.validateCompanyAccount(transaction);
        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate with an invalid date")
    void validateCompanyAccountWithInvalidDate() throws ServiceException, DataException {

        when(companyService.getCompanyProfile(transaction.getCompanyNumber())).thenReturn(companyProfileApi);

        when(companyProfileApi.getAccounts()).thenReturn(accounts);
        when(accounts.getNextAccounts()).thenReturn(nextAccounts);
        when(nextAccounts.getPeriodStartOn()).thenReturn(LocalDate.now().plusDays(8));

        errors = validator.validateCompanyAccount(transaction);
        assertTrue(errors.hasErrors());

        Error error = createError(validator.dateOutsideRange, ERROR_PATH);

        assertTrue(errors.containsError(error));
    }

    @Test
    @DisplayName("Validator throws service exception")
    void validateCompanyAccountThrowsServiceException() throws ServiceException, DataException {

        when(companyService.getCompanyProfile(transaction.getCompanyNumber())).thenThrow(ServiceException.class);

        assertThrows(DataException.class,
                () -> validator.validateCompanyAccount(transaction));
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}
