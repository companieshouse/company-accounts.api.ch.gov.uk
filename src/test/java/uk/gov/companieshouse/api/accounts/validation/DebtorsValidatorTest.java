package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.debtors.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.debtors.Debtors;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.debtors.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DebtorsValidatorTest {
    private static final String DEBTORS_PATH = "$.debtors";
    private static final String DEBTORS_PATH_CURRENT = DEBTORS_PATH + ".current_period";
    private static final String DEBTORS_PATH_PREVIOUS = DEBTORS_PATH + ".previous_period";
    private static final String CURRENT_TOTAL_PATH = DEBTORS_PATH_CURRENT + ".total";
    private static final String PREVIOUS_TOTAL_PATH = DEBTORS_PATH_PREVIOUS + ".total";
    private static final String INCORRECT_TOTAL_NAME = "incorrectTotal";
    private static final String INCORRECT_TOTAL_VALUE = "incorrect_total";
    private static final String COMPANY_ACCOUNTS_ID = "123abc";
    private static final String CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME = "currentBalanceSheetNotEqual";
    private static final String CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE =
            "value_not_equal_to_current_period_on_balance_sheet";
    private static final String PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME = "previousBalanceSheetNotEqual";
    private static final String PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE =
            "value_not_equal_to_previous_period_on_balance_sheet";
    private static final String MANDATORY_ELEMENT_MISSING_NAME = "mandatoryElementMissing";
    private static final String MANDATORY_ELEMENT_MISSING_VALUE = "mandatory_element_missing";
    private static final String UNEXPECTED_DATA_NAME = "unexpectedData";
    private static final String UNEXPECTED_DATA_VALUE = "unexpected.data";
    private static final String EMPTY_RESOURCE_NAME = "emptyResource";
    private static final String EMPTY_RESOURCE_VALUE = "empty_resource";

    private Debtors debtors;
    private Errors errors;

    @Mock
    private CompanyService mockCompanyService;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private ServiceException mockServiceException;

    @Mock
    private CurrentPeriodService mockCurrentPeriodService;

    @Mock
    private PreviousPeriodService mockPreviousPeriodService;

    private DebtorsValidator validator;

    @BeforeEach
    void setup() {
        debtors = new Debtors();
        errors = new Errors();
        validator = new DebtorsValidator(mockCompanyService, mockCurrentPeriodService, mockPreviousPeriodService);
    }

    @Test
    @DisplayName("Note validation and cross validation passes with valid note for first year filer")
    void testSuccessfulFirstYearNoteValidationAndCrossValidation() throws DataException {
        createValidNoteCurrentPeriod();

        mockValidBalanceSheetCurrentPeriod();

        errors = validator.validateSubmission(debtors, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Note validation and cross validation passes with valid note for multiple year filer")
    void testSuccessfulMultipleYearNoteValidationAndCrossValidation() throws ServiceException, DataException {
        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        mockValidBalanceSheetCurrentPeriod();
        mockValidBalanceSheetPreviousPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        errors = validator.validateSubmission(debtors, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Note validation passes with valid note for first year filer")
    void testSuccessfulFirstYearNoteValidation() throws DataException {
        createValidNoteCurrentPeriod();

        errors = validator.validateSubmission(debtors, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Note validation passes with valid note for multiple year filer")
    void testSuccessfulMultipleYearNoteValidation() throws ServiceException, DataException {
        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        errors = validator.validateSubmission(debtors, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Errors returned for empty note when balance sheet periods have values")
    void testErrorsReturnedWhenNoDataPresentButBalanceSheetPeriodValuesProvided() throws ServiceException, DataException {
        mockValidBalanceSheetCurrentPeriod();
        mockValidBalanceSheetPreviousPeriod();

        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
                CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
                PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        errors = validator.validateSubmission(debtors, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE, CURRENT_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE, PREVIOUS_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Error returned for first year filer if previous period provided in note")
    void testUnexpectedDataErrorReturnedForFirstYearFiler() throws ServiceException, DataException {
        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        mockValidBalanceSheetCurrentPeriod();

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_NAME, UNEXPECTED_DATA_VALUE);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(false);

        errors = validator.validateSubmission(debtors, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA_VALUE, DEBTORS_PATH_PREVIOUS)));
    }

    @Test
    @DisplayName("Validation fails when empty periods and empty note resource")
    void testValidationAgainstEmptyResource() throws DataException {
        Debtors debtors = new Debtors();

        ReflectionTestUtils.setField(validator, EMPTY_RESOURCE_NAME, EMPTY_RESOURCE_VALUE);

        errors = validator.validateSubmission(debtors, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.containsError(createError(EMPTY_RESOURCE_VALUE, DEBTORS_PATH)));
    }

    @Test
    @DisplayName("Errors returned when total fields missing")
    void testErrorsReturnedWhenMandatoryFieldsMissing() throws ServiceException, DataException {
        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setTradeDebtors(1L);
        debtors.setCurrentPeriod(currentPeriod);

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setTradeDebtors(5L);
        debtors.setPreviousPeriod(previousPeriod);

        mockValidBalanceSheetCurrentPeriod();
        mockValidBalanceSheetPreviousPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_NAME,
            MANDATORY_ELEMENT_MISSING_VALUE);

        errors = validator.validateSubmission(debtors, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE, CURRENT_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE, PREVIOUS_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Errors returned when total values incorrect")
    void testErrorsReturnedWhenTotalValuesIncorrect() throws ServiceException,
        DataException {
        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setTradeDebtors(1L);
        currentPeriod.setTotal(2L);
        debtors.setCurrentPeriod(currentPeriod);

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setPrepaymentsAndAccruedIncome(5L);
        previousPeriod.setTotal(50L);
        debtors.setPreviousPeriod(previousPeriod);

        mockValidBalanceSheetCurrentPeriod();
        mockValidBalanceSheetPreviousPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_NAME, INCORRECT_TOTAL_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateSubmission(debtors, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertEquals(4, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE, CURRENT_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE, CURRENT_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE, PREVIOUS_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE, PREVIOUS_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Errors returned when no totals provided")
    void testErrorsReturnedWhenNoTotalsProvided() throws ServiceException,
        DataException {
        debtors.setCurrentPeriod(new CurrentPeriod());
        debtors.setPreviousPeriod(new PreviousPeriod());

        mockValidBalanceSheetCurrentPeriod();
        mockValidBalanceSheetPreviousPeriod();

        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_NAME, MANDATORY_ELEMENT_MISSING_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
                CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
                PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        errors = validator.validateSubmission(debtors, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE, CURRENT_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE, PREVIOUS_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Data exception thrown when company service API call fails")
    void testDataExceptionThrown() throws ServiceException {
        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenThrow(mockServiceException);

        assertThrows(DataException.class, () -> validator.validateSubmission(debtors, mockTransaction,
                COMPANY_ACCOUNTS_ID, mockRequest));
    }

    @Test
    @DisplayName("Data exception thrown when current balancesheet call fails")
    void testDataExceptionThrownWhenCurrentBalanceSheetCallFails() throws DataException {
        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        when(mockCurrentPeriodService.find(COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(new DataException(""));

        assertThrows(DataException.class, () -> validator.validateSubmission(debtors, mockTransaction,
                COMPANY_ACCOUNTS_ID, mockRequest));
    }

    @Test
    @DisplayName("Data exception thrown when previous balancesheet call fails")
    void testDataExceptionThrownWhenPreviousBalanceSheetCallFails() throws DataException {
        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        mockValidBalanceSheetCurrentPeriod();

        when(mockPreviousPeriodService.find(COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(new DataException(""));

        assertThrows(DataException.class, () -> validator.validateSubmission(debtors, mockTransaction,
                COMPANY_ACCOUNTS_ID, mockRequest));
    }

    @Test
    @DisplayName("Get accounting note type")
    void getAccountingNoteType() {
        assertEquals(AccountingNoteType.SMALL_FULL_DEBTORS, validator.getAccountingNoteType());
    }

    private void createValidNoteCurrentPeriod() {
        CurrentPeriod currentDebtors = new CurrentPeriod();
        currentDebtors.setTradeDebtors(1L);
        currentDebtors.setPrepaymentsAndAccruedIncome(2L);
        currentDebtors.setGreaterThanOneYear(3L);
        currentDebtors.setOtherDebtors(4L);
        currentDebtors.setTotal(7L);
        currentDebtors.setDetails("details");

        debtors.setCurrentPeriod(currentDebtors);
    }

    private void createValidNotePreviousPeriod() {
        PreviousPeriod previousDebtors = new PreviousPeriod();
        previousDebtors.setTradeDebtors(1L);
        previousDebtors.setPrepaymentsAndAccruedIncome(2L);
        previousDebtors.setGreaterThanOneYear(3L);
        previousDebtors.setOtherDebtors(4L);
        previousDebtors.setTotal(7L);

        debtors.setPreviousPeriod(previousDebtors);
    }

    private void mockValidBalanceSheetCurrentPeriod() throws DataException {
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).find(
            COMPANY_ACCOUNTS_ID, mockRequest);
    }

    private void mockValidBalanceSheetPreviousPeriod() throws DataException {
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).find(
            COMPANY_ACCOUNTS_ID, mockRequest);
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType());
    }

    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> generateValidCurrentPeriodResponseObject() {
        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> currentPeriodResponseObject =
            new ResponseObject<>(ResponseStatus.FOUND);

        uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod currentPeriodTest =
            new uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod();

        BalanceSheet balanceSheet = new BalanceSheet();

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setDebtors(7L);
        balanceSheet.setCurrentAssets(currentAssets);

        currentPeriodTest.setBalanceSheet(balanceSheet);

        currentPeriodResponseObject.setData(currentPeriodTest);
        return currentPeriodResponseObject;
    }

    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> generateValidPreviousPeriodResponseObject() {
        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> previousPeriodResponseObject =
            new ResponseObject<>(ResponseStatus.FOUND);

        uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod previousPeriodTest =
            new uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod();

        BalanceSheet balanceSheet = new BalanceSheet();

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setDebtors(7L);
        balanceSheet.setCurrentAssets(currentAssets);

        previousPeriodTest.setBalanceSheet(balanceSheet);

        previousPeriodResponseObject.setData(previousPeriodTest);
        return previousPeriodResponseObject;
    }
}
