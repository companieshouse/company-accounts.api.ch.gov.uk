package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PreviousPeriodValidatorTest {

    @Mock
    private CompanyService companyService;

    @Mock
    private BalanceSheetValidator balanceSheetValidator;

    private PreviousPeriodValidator validator;

    @Mock
    private PreviousPeriod previousPeriod;

    @Mock
    private BalanceSheet balanceSheet;

    @Mock
    private Transaction transaction;

    private static final String PERIOD_PATH = "$.previous_period";

    private static final String UNEXPECTED_DATA_KEY = "unexpectedData";
    private static final String UNEXPECTED_DATA = "unexpected.data";

    @BeforeEach
    private void setUp() {
    	validator = new PreviousPeriodValidator(companyService, balanceSheetValidator);
    }
    
    @Test
    @DisplayName("Validate previous period - multi year filer")
    void validatePreviousPeriodForMultiYearFiler() throws DataException, ServiceException {

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        when(previousPeriod.getBalanceSheet()).thenReturn(balanceSheet);

        Errors errors = validator.validatePreviousPeriod(previousPeriod, transaction);

        assertNotNull(errors);
        verify(balanceSheetValidator).validateBalanceSheet(eq(balanceSheet), eq(transaction), eq(PERIOD_PATH), any(Errors.class));
    }

    @Test
    @DisplayName("Validate previous period - single year filer")
    void validatePreviousPeriodForSingleYearFiler() throws DataException, ServiceException {

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(false);

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_KEY, UNEXPECTED_DATA);

        Errors errors = validator.validatePreviousPeriod(previousPeriod, transaction);

        assertNotNull(errors);
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA, PERIOD_PATH)));

        verify(balanceSheetValidator, never())
                .validateBalanceSheet(any(BalanceSheet.class), eq(transaction), eq(PERIOD_PATH), any(Errors.class));
    }

    @Test
    @DisplayName("Validate previous period - service exception thrown")
    void validatePreviousPeriodServiceException() throws ServiceException {

        when(companyService.isMultipleYearFiler(transaction)).thenThrow(ServiceException.class);

        assertThrows(DataException.class, () -> validator.validatePreviousPeriod(previousPeriod, transaction));
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}