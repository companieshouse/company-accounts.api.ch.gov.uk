package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.company.account.CompanyAccountApi;
import uk.gov.companieshouse.api.model.company.account.NextAccountsApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CompanyAccountValidatorTest {
    private static final String ERROR_PATH = "$.company_account";

    private Errors errors;

    @Mock
    private Transaction transaction;

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

        Error error = createError(validator.dateOutsideRange);

        assertTrue(errors.containsError(error));
    }

    @Test
    @DisplayName("Validator throws service exception")
    void validateCompanyAccountThrowsServiceException() throws ServiceException {
        when(companyService.getCompanyProfile(transaction.getCompanyNumber())).thenThrow(ServiceException.class);

        assertThrows(DataException.class, () -> validator.validateCompanyAccount(transaction));
    }

    private Error createError(String error) {
        return new Error(error, CompanyAccountValidatorTest.ERROR_PATH, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}
