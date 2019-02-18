package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.mongodb.MongoException;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.OtherLiabilitiesOrAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear.CreditorsAfterOneYear;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear.PreviousPeriod;
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
public class CreditorsAfterOneYearValidatorTest {

    private static final String CREDITORS_AFTER_PATH = "$.creditors_after_one_year";
    private static final String CREDITORS_AFTER_CURRENT_PERIOD_PATH = CREDITORS_AFTER_PATH +
            ".current_period";
    private static final String CREDITORS_AFTER_PREVIOUS_PERIOD_PATH = CREDITORS_AFTER_PATH +
            ".previous_period";
    private static final String CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH =
            CREDITORS_AFTER_CURRENT_PERIOD_PATH + ".total";
    private static final String CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH =
            CREDITORS_AFTER_PREVIOUS_PERIOD_PATH + ".total";

    private static final String CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME = "currentBalanceSheetNotEqual";
    private static final String CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE =
            "value_not_equal_to_current_period_on_balance_sheet";
    private static final String PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME = "previousBalanceSheetNotEqual";
    private static final String PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE =
            "value_not_equal_to_previous_period_on_balance_sheet";
    private static final String MANDATORY_ELEMENT_MISSING_NAME = "mandatoryElementMissing";
    private static final String MANDATORY_ELEMENT_MISSING_VALUE =
            "mandatory_element_missing";
    private static final String UNEXPECTED_DATA_NAME = "unexpectedData";
    private static final String UNEXPECTED_DATA_VALUE = "unexpected.data";

    private static final String COMPANY_ACCOUNTS_ID = "123abcefg";

    private static final String INCORRECT_TOTAL_NAME = "incorrectTotal";
    private static final String INCORRECT_TOTAL_VALUE = "incorrect_total";

    private CreditorsAfterOneYear creditorsAfterOneYear;
    private Errors errors;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private CompanyService mockCompanyService;

    @Mock
    private ServiceException mockServiceException;

    @Mock
    private CurrentPeriodService mockCurrentPeriodService;

    @Mock
    private PreviousPeriodService mockPreviousPeriodService;

    @Mock
    private HttpServletRequest mockRequest;

    private CreditorsAfterOneYearValidator validator;

    @BeforeEach
    void setup() {
        creditorsAfterOneYear = new CreditorsAfterOneYear();
        errors = new Errors();
        validator = new CreditorsAfterOneYearValidator(mockCompanyService, mockCurrentPeriodService, mockPreviousPeriodService);
    }

    @Test
    @DisplayName("Note validation and cross validation passes with valid note for first year filer")
    void testSuccessfulFirstYearNoteValidationAndCrossValidation() throws DataException {

        createValidNoteCurrentPeriod();

        mockValidBalanceSheetCurrentPeriod();

        errors = validator.validateCreditorsAfterOneYear(creditorsAfterOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

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

        errors = validator.validateCreditorsAfterOneYear(creditorsAfterOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Note validation passes with valid note for first year filer")
    void testSuccessfulFirstYearNoteValidation() throws DataException {

        createValidNoteCurrentPeriod();

        errors = validator.validateCreditorsAfterOneYear(creditorsAfterOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Note validation passes with valid note for multiple year filer")
    void testSuccessfulMultipleYearNoteValidation() throws ServiceException, DataException {

        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        errors = validator.validateCreditorsAfterOneYear(creditorsAfterOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Cross validation passes with valid note for first year filer")
    void testSuccessfulCrossValidationForFirstYearFiler() throws DataException {

        createValidNoteCurrentPeriod();

        mockValidBalanceSheetCurrentPeriod();

        errors = validator.crossValidate(creditorsAfterOneYear, mockRequest, COMPANY_ACCOUNTS_ID, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Cross validation passes with valid note for multiple year filer")
    void testSuccessfulCrossValidationForMultipleYearFiler() throws DataException {

        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        mockValidBalanceSheetCurrentPeriod();
        mockValidBalanceSheetPreviousPeriod();

        errors = validator.crossValidate(creditorsAfterOneYear, mockRequest, COMPANY_ACCOUNTS_ID, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Errors returned for empty note when balance sheet periods have values")
    void testErrorsReturnedWhenNoDataPresentButBalanceSheetPeriodValuesProvided() throws ServiceException, DataException {

        mockValidBalanceSheetCurrentPeriod();
        mockValidBalanceSheetPreviousPeriod();

        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_NAME,
                MANDATORY_ELEMENT_MISSING_VALUE);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        errors = validator.validateCreditorsAfterOneYear(creditorsAfterOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
                CREDITORS_AFTER_CURRENT_PERIOD_PATH)));
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
                CREDITORS_AFTER_PREVIOUS_PERIOD_PATH)));
    }

    @Test
    @DisplayName("Error returned for first year filer if previous period provided in note")
    void testUnexpectedDataErrorReturnedForFirstYearFiler() throws ServiceException, DataException {

        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        mockValidBalanceSheetCurrentPeriod();

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_NAME,
                UNEXPECTED_DATA_VALUE);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(false);

        errors = validator.validateCreditorsAfterOneYear(creditorsAfterOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA_VALUE,
                CREDITORS_AFTER_PREVIOUS_PERIOD_PATH)));
    }

    @Test
    @DisplayName("Errors returned when total fields missing")
    void testErrorsReturnedWhenMandatoryFieldsMissing() throws ServiceException,
            DataException {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setBankLoansAndOverdrafts(1L);
        creditorsAfterOneYear.setCurrentPeriod(currentPeriod);

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setOtherCreditors(5L);
        creditorsAfterOneYear.setPreviousPeriod(previousPeriod);

        mockValidBalanceSheetCurrentPeriod();
        mockValidBalanceSheetPreviousPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
                CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
                PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_NAME,
                MANDATORY_ELEMENT_MISSING_VALUE);

        errors = validator.validateCreditorsAfterOneYear(creditorsAfterOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertEquals(4, errors.getErrorCount());
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
                CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE,
                CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
                CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE,
                CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Errors returned when total values incorrect")
    void testErrorsReturnedWhenTotalValuesIncorrect() throws ServiceException,
            DataException {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setBankLoansAndOverdrafts(1L);
        currentPeriod.setTotal(2L);
        creditorsAfterOneYear.setCurrentPeriod(currentPeriod);

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setBankLoansAndOverdrafts(5L);
        previousPeriod.setTotal(50L);
        creditorsAfterOneYear.setPreviousPeriod(previousPeriod);

        mockValidBalanceSheetCurrentPeriod();
        mockValidBalanceSheetPreviousPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_NAME, INCORRECT_TOTAL_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
                CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
                PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateCreditorsAfterOneYear(creditorsAfterOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertEquals(4, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE,
                CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE,
                CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE,
                CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE,
                CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Errors returned when balance sheet period values empty but note periods not empty")
    void testErrorThrownWhenBalanceSheetPeriodValuesEmptyButNotPeriodsNotEmpty() throws ServiceException,
            DataException {

        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        mockBalanceSheetCurrentPeriodWithoutNoteValue();
        mockBalanceSheetPreviousPeriodWithoutNoteValue();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_NAME, INCORRECT_TOTAL_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
                CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
                PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_NAME,
                UNEXPECTED_DATA_VALUE);

        errors = validator.validateCreditorsAfterOneYear(creditorsAfterOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA_VALUE,
                CREDITORS_AFTER_CURRENT_PERIOD_PATH)));
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA_VALUE,
                CREDITORS_AFTER_PREVIOUS_PERIOD_PATH)));
    }

    @Test
    @DisplayName("Errors returned when no totals provided")
    void testErrorsReturnedWhenNoTotalsProvided() throws ServiceException,
            DataException {

        creditorsAfterOneYear.setCurrentPeriod(new CurrentPeriod());
        creditorsAfterOneYear.setPreviousPeriod(new PreviousPeriod());

        mockValidBalanceSheetCurrentPeriod();
        mockValidBalanceSheetPreviousPeriod();

        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_NAME, MANDATORY_ELEMENT_MISSING_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
                CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
                PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        errors = validator.validateCreditorsAfterOneYear(creditorsAfterOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertEquals(4, errors.getErrorCount());
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
                CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE,
                CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
                CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE,
                CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Data exception thrown when company service API call fails")
    void testDataExceptionThrown() throws ServiceException {

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenThrow(mockServiceException);

        assertThrows(DataException.class,
                () -> validator.validateCreditorsAfterOneYear(creditorsAfterOneYear,
                        mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest));
    }

    @Test
    @DisplayName("Data exception thrown when mongo current balancesheet call fails")
    void testDataExceptionThrownWhenCurrentBalanceSheetMongoCallFails() throws ServiceException,
            DataException {

        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
                COMPANY_ACCOUNTS_ID);
        when(mockCurrentPeriodService.findById(COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(new MongoException(""));

        assertThrows(DataException.class,
                () -> validator.validateCreditorsAfterOneYear(creditorsAfterOneYear,
                        mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest));
    }

    @Test
    @DisplayName("Data exception thrown when mongo previous balancesheet call fails")
    void testDataExceptionThrownWhenPreviousBalanceSheetMongoCallFails() throws ServiceException,
            DataException {

        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        mockValidBalanceSheetCurrentPeriod();

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
                COMPANY_ACCOUNTS_ID);
        when(mockPreviousPeriodService.findById(COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(new MongoException(""));

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        assertThrows(DataException.class,
                () -> validator.validateCreditorsAfterOneYear(creditorsAfterOneYear,
                        mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest));
    }

    private void createValidNoteCurrentPeriod() {
        CurrentPeriod creditorsCurrent = new CurrentPeriod();
        creditorsCurrent.setBankLoansAndOverdrafts(1L);
        creditorsCurrent.setFinanceLeasesAndHirePurchaseContracts(1L);
        creditorsCurrent.setOtherCreditors(1L);
        creditorsCurrent.setTotal(3L);

        creditorsAfterOneYear.setCurrentPeriod(creditorsCurrent);
    }

    private void createValidNotePreviousPeriod() {
        PreviousPeriod previousCreditors = new PreviousPeriod();
        previousCreditors.setBankLoansAndOverdrafts(2L);
        previousCreditors.setFinanceLeasesAndHirePurchaseContracts(2L);
        previousCreditors.setOtherCreditors(2L);
        previousCreditors.setTotal(6L);

        creditorsAfterOneYear.setPreviousPeriod(previousCreditors);
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }

    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> generateValidCurrentPeriodResponseObject(boolean includeNoteValue) {
        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> currentPeriodResponseObject =
                new ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod>(
                        ResponseStatus.FOUND);

        uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod currentPeriodTest =
                new uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod();

        BalanceSheet balanceSheet = new BalanceSheet();

        if (includeNoteValue) {
            OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
            otherLiabilitiesOrAssets.setCreditorsAfterOneYear(3L);
            balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);
        }

        currentPeriodTest.setBalanceSheet(balanceSheet);

        currentPeriodResponseObject.setData(currentPeriodTest);
        return currentPeriodResponseObject;
    }

    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> generateValidPreviousPeriodResponseObject(boolean includeNoteValue) {
        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> previousPeriodResponseObject =
                new ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod>(
                        ResponseStatus.FOUND);

        uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod previousPeriodTest =
                new uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod();

        BalanceSheet balanceSheet = new BalanceSheet();

        if (includeNoteValue) {
            OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
            otherLiabilitiesOrAssets.setCreditorsAfterOneYear(6L);
            balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);
        }

        previousPeriodTest.setBalanceSheet(balanceSheet);

        previousPeriodResponseObject.setData(previousPeriodTest);
        return previousPeriodResponseObject;
    }

    private void mockValidBalanceSheetCurrentPeriod() throws DataException {
        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
                COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject(true)).when(mockCurrentPeriodService).findById(
                COMPANY_ACCOUNTS_ID, mockRequest);
    }

    private void mockValidBalanceSheetPreviousPeriod() throws DataException {
        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
                COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject(true)).when(mockPreviousPeriodService).findById(
                COMPANY_ACCOUNTS_ID, mockRequest);
    }

    private void mockBalanceSheetCurrentPeriodWithoutNoteValue() throws DataException {
        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
                COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject(false)).when(mockCurrentPeriodService).findById(
                COMPANY_ACCOUNTS_ID, mockRequest);
    }

    private void mockBalanceSheetPreviousPeriodWithoutNoteValue() throws DataException {
        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
                COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject(false)).when(mockPreviousPeriodService).findById(
                COMPANY_ACCOUNTS_ID, mockRequest);
    }
}
