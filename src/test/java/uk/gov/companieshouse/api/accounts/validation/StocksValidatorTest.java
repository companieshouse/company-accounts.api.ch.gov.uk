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
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.stocks.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.stocks.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.stocks.Stocks;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StocksValidatorTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final Long NOTE_CURRENT_PERIOD_PAYMENTS_ON_ACCOUNT = 1L;
    private static final Long NOTE_CURRENT_PERIOD_STOCKS = 1L;
    private static final Long NOTE_CURRENT_PERIOD_TOTAL = 2L;
    private static final Long NOTE_PREVIOUS_PERIOD_PAYMENTS_ON_ACCOUNT = 10L;
    private static final Long NOTE_PREVIOUS_PERIOD_STOCKS = 10L;
    private static final Long NOTE_PREVIOUS_PERIOD_TOTAL = 20L;
    private static final Long BALANCE_SHEET_CURRENT_PERIOD_STOCKS = 2L;
    private static final Long BALANCE_SHEET_PREVIOUS_PERIOD_STOCKS = 20L;

    private static final String INVALID_NOTE_VALUE = "invalid_note";
    private static final String INVALID_NOTE_NAME = "invalidNote";
    private static final String INCORRECT_TOTAL_NAME = "incorrectTotal";
    private static final String INCORRECT_TOTAL_VALUE = "incorrect_total";
    private static final String INCONSISTENT_DATA_NAME = "inconsistentData";
    private static final String INCONSISTENT_DATA_VALUE = "inconsistent_data";

    private static final String CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME = "currentBalanceSheetNotEqual";
    private static final String CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE =
            "value_not_equal_to_current_period_on_balance_sheet";
    private static final String PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME = "previousBalanceSheetNotEqual";
    private static final String PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE =
            "value_not_equal_to_previous_period_on_balance_sheet";

    private static final String STOCKS_PATH = "$.stocks";
    private static final String CURRENT_PERIOD_PATH = STOCKS_PATH + ".current_period";
    private static final String PREVIOUS_PERIOD_PATH = STOCKS_PATH + ".previous_period";
    private static final String CURRENT_PERIOD_TOTAL_PATH = CURRENT_PERIOD_PATH + ".total";
    private static final String PREVIOUS_PERIOD_TOTAL_PATH = PREVIOUS_PERIOD_PATH + ".total";
    private static final String PREVIOUS_PERIOD_PAYMENTS_ON_ACCOUNT_PATH = PREVIOUS_PERIOD_PATH + ".payments_on_account";
    private static final String PREVIOUS_PERIOD_STOCKS_PATH = PREVIOUS_PERIOD_PATH + ".stocks";

    private Stocks stocks;
    private Errors errors;

    @Mock
    private CompanyService mockCompanyService;

    @Mock
    private CurrentPeriodService mockCurrentPeriodService;

    @Mock
    private PreviousPeriodService mockPreviousPeriodService;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private ServiceException mockServiceException;

    @InjectMocks
    private StocksValidator validator;

    @BeforeEach
    void setup() {
        stocks = new Stocks();
        errors = new Errors();
    }

    @Test
    @DisplayName("Validation passes with valid stocks resource for single year filer")
    void testSuccessfulSingleYearStocksNote() throws DataException {

        createValidCurrentPeriodStocks();

        mockCurrentPeriodServiceValidCurrentPeriod();

        errors = validator.validateStocks(stocks, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validation passes with valid stocks resource for multiple year filer")
    void testSuccessfulMultipleYearStocksNote() throws ServiceException, DataException {

        createValidCurrentPeriodStocks();
        createValidPreviousPeriodStocks();

        mockCurrentPeriodServiceValidCurrentPeriod();
        mockPreviousPeriodServiceValidPreviousPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        errors = validator.validateStocks(stocks, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Cross validation passes with valid stocks resource for multiple year filer")
    void testSuccessfulCrossValidation() throws DataException, ServiceException {

        createValidCurrentPeriodStocks();
        createValidPreviousPeriodStocks();

        mockCurrentPeriodServiceValidCurrentPeriod();
        mockPreviousPeriodServiceValidPreviousPeriod();

        errors = validator.crossValidate(errors, mockRequest, COMPANY_ACCOUNTS_ID, stocks);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("No validation errors returned when no data to validate")
    void testNoValidationErrorsWhenNoDataPresent() throws ServiceException, DataException {

        errors = validator.validateStocks(stocks, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Errors returned when total fields missing")
    void testErrorsReturnedWhenTotalFieldsMissing() throws ServiceException,
            DataException {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setPaymentsOnAccount(1L);
        stocks.setCurrentPeriod(currentPeriod);

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setPaymentsOnAccount(10L);
        stocks.setPreviousPeriod(previousPeriod);

        mockCurrentPeriodServiceValidCurrentPeriod();
        mockPreviousPeriodServiceValidPreviousPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INVALID_NOTE_NAME, INVALID_NOTE_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
                CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
                PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateStocks(stocks, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertEquals(4, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INVALID_NOTE_VALUE,
                CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(INVALID_NOTE_VALUE,
                PREVIOUS_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE,
                CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE,
                PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Errors returned when total values incorrect")
    void testErrorsReturnedWhenTotalValuesIncorrect() throws ServiceException,
            DataException {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setPaymentsOnAccount(1L);
        currentPeriod.setTotal(2L);
        stocks.setCurrentPeriod(currentPeriod);

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setPaymentsOnAccount(10L);
        previousPeriod.setTotal(20L);
        stocks.setPreviousPeriod(previousPeriod);

        mockCurrentPeriodServiceValidCurrentPeriod();
        mockPreviousPeriodServiceValidPreviousPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_NAME, INCORRECT_TOTAL_VALUE);

        errors = validator.validateStocks(stocks, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE,
                CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE,
                PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Errors returned when total values do not match balance sheet values")
    void testErrorsReturnedWhenNoteTotalsNotEqualToBalanceSheetPeriodTotals() throws ServiceException,
            DataException {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setPaymentsOnAccount(100L);
        currentPeriod.setTotal(100L);
        stocks.setCurrentPeriod(currentPeriod);

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setPaymentsOnAccount(200L);
        previousPeriod.setTotal(200L);
        stocks.setPreviousPeriod(previousPeriod);

        mockCurrentPeriodServiceValidCurrentPeriod();
        mockPreviousPeriodServiceValidPreviousPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
                CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
                PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateStocks(stocks, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE,
                CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE,
                PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Errors returned when balance sheet periods have no stocks value but the note has values")
    void testErrorsReturnedWhenBalanceSheetPeriodsNullButNoteHasValues() throws ServiceException,
            DataException {

        createValidCurrentPeriodStocks();
        createValidPreviousPeriodStocks();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
                CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
                PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateStocks(stocks, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE,
                CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE,
                PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Errors returned when single year filer creates stocks resource with previous period")
    void testErrorsReturnedForFirstYearFilerWhenPreviousPeriodIncluded() throws ServiceException,
            DataException {

        createValidCurrentPeriodStocks();
        createValidPreviousPeriodStocks();

        mockCurrentPeriodServiceValidCurrentPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(false);

        ReflectionTestUtils.setField(validator, INCONSISTENT_DATA_NAME, INCONSISTENT_DATA_VALUE);

        errors = validator.validateStocks(stocks, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertEquals(3, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCONSISTENT_DATA_VALUE,
                PREVIOUS_PERIOD_PAYMENTS_ON_ACCOUNT_PATH)));
        assertTrue(errors.containsError(createError(INCONSISTENT_DATA_VALUE,
                PREVIOUS_PERIOD_STOCKS_PATH)));
        assertTrue(errors.containsError(createError(INCONSISTENT_DATA_VALUE,
                PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Data exception thrown when company service API call fails")
    void testDataExceptionThrown() throws ServiceException,
            DataException {

        createValidCurrentPeriodStocks();
        createValidPreviousPeriodStocks();

        mockCurrentPeriodServiceValidCurrentPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenThrow(mockServiceException);

        assertThrows(DataException.class,
                () -> validator.validateStocks(stocks,
                        mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest));
    }

    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> generateValidCurrentPeriodResponseObject() {

        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> currentPeriodResponseObject =
                new ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod>(
                        ResponseStatus.FOUND);
        uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod currentPeriodTest =
                new uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod();

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(BALANCE_SHEET_CURRENT_PERIOD_STOCKS);
        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCurrentAssets(currentAssets);
        currentPeriodTest.setBalanceSheet(balanceSheet);

        currentPeriodResponseObject.setData(currentPeriodTest);
        return currentPeriodResponseObject;
    }

    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> generateValidPreviousPeriodResponseObject() {

        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> previousPeriodResponseObject =
                new ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod>(
                        ResponseStatus.FOUND);

        uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod previousPeriodTest =
                new uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod();

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(BALANCE_SHEET_PREVIOUS_PERIOD_STOCKS);
        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCurrentAssets(currentAssets);
        previousPeriodTest.setBalanceSheet(balanceSheet);

        previousPeriodResponseObject.setData(previousPeriodTest);
        return previousPeriodResponseObject;
    }

    private void mockCurrentPeriodServiceValidCurrentPeriod() throws DataException {
        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
                COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
                COMPANY_ACCOUNTS_ID, mockRequest);
    }

    private void mockPreviousPeriodServiceValidPreviousPeriod() throws DataException {
        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
                COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
                COMPANY_ACCOUNTS_ID, mockRequest);
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }

    private void createValidCurrentPeriodStocks() {
        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setPaymentsOnAccount(NOTE_CURRENT_PERIOD_PAYMENTS_ON_ACCOUNT);
        currentPeriod.setStocks(NOTE_CURRENT_PERIOD_STOCKS);
        currentPeriod.setTotal(NOTE_CURRENT_PERIOD_TOTAL);
        stocks.setCurrentPeriod(currentPeriod);
    }

    private void createValidPreviousPeriodStocks() {
        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setPaymentsOnAccount(NOTE_PREVIOUS_PERIOD_PAYMENTS_ON_ACCOUNT);
        previousPeriod.setStocks(NOTE_PREVIOUS_PERIOD_STOCKS);
        previousPeriod.setTotal(NOTE_PREVIOUS_PERIOD_TOTAL);
        stocks.setPreviousPeriod(previousPeriod);
    }
}
