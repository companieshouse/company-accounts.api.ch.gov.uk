package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

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
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.CreditorsWithinOneYear;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.PreviousPeriod;
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
public class CreditorsWithinOneYearValidatorTests {

    private static final String CREDITORS_WITHIN_PATH = "$.creditors_within_one_year";
    private static final String CREDITORS_WITHIN_CURRENT_PERIOD_PATH = CREDITORS_WITHIN_PATH +
            ".current_period";
    private static final String CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH = CREDITORS_WITHIN_PATH +
            ".previous_period";
    private static final String CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH =
            CREDITORS_WITHIN_CURRENT_PERIOD_PATH + ".total";
    private static final String CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH =
            CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH + ".total";

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

    private CreditorsWithinOneYear creditorsWithinOneYear;
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

    private CreditorsWithinOneYearValidator validator;

    @BeforeEach
    void setup() {
        creditorsWithinOneYear = new CreditorsWithinOneYear();
        errors = new Errors();
        validator = new CreditorsWithinOneYearValidator(mockCompanyService, mockCurrentPeriodService, mockPreviousPeriodService);
    }

    @Test
    @DisplayName("Note validation and cross validation passes with valid note for first year filer")
    void testSuccessfulFirstYearNoteValidationAndCrossValidation() throws DataException {

        createValidNoteCurrentPeriod();

        mockValidBalanceSheetCurrentPeriod();

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

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

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Note validation passes with valid note for first year filer")
    void testSuccessfulFirstYearNoteValidation() throws DataException {

        createValidNoteCurrentPeriod();

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Note validation passes with valid note for multiple year filer")
    void testSuccessfulMultipleYearNoteValidation() throws ServiceException, DataException {

        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Cross validation passes with valid note for first year filer")
    void testSuccessfulCrossValidationForFirstYearFiler() throws DataException {

        createValidNoteCurrentPeriod();

        mockValidBalanceSheetCurrentPeriod();

        errors = validator.crossValidate(creditorsWithinOneYear, mockRequest, COMPANY_ACCOUNTS_ID, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Cross validation passes with valid note for multiple year filer")
    void testSuccessfulCrossValidationForMultipleYearFiler() throws DataException {

        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        mockValidBalanceSheetCurrentPeriod();
        mockValidBalanceSheetPreviousPeriod();

        errors = validator.crossValidate(creditorsWithinOneYear, mockRequest, COMPANY_ACCOUNTS_ID, errors);

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

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
                CREDITORS_WITHIN_CURRENT_PERIOD_PATH)));
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
                CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH)));
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

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA_VALUE,
                CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH)));
    }

    @Test
    @DisplayName("Errors returned when total fields missing")
    void testErrorsReturnedWhenMandatoryFieldsMissing() throws ServiceException,
            DataException {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setTradeCreditors(1L);
        creditorsWithinOneYear.setCurrentPeriod(currentPeriod);

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setOtherCreditors(5L);
        creditorsWithinOneYear.setPreviousPeriod(previousPeriod);

        mockValidBalanceSheetCurrentPeriod();
        mockValidBalanceSheetPreviousPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
                CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
                PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_NAME,
                MANDATORY_ELEMENT_MISSING_VALUE);

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertEquals(4, errors.getErrorCount());
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
                CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE,
                CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
                CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE,
                CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Errors returned when total values incorrect")
    void testErrorsReturnedWhenTotalValuesIncorrect() throws ServiceException,
            DataException {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setTradeCreditors(1L);
        currentPeriod.setTotal(2L);
        creditorsWithinOneYear.setCurrentPeriod(currentPeriod);

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setTaxationAndSocialSecurity(5L);
        previousPeriod.setTotal(50L);
        creditorsWithinOneYear.setPreviousPeriod(previousPeriod);

        mockValidBalanceSheetCurrentPeriod();
        mockValidBalanceSheetPreviousPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_NAME, INCORRECT_TOTAL_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
                CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
                PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertEquals(4, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE,
                CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE,
                CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE,
                CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE,
                CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH)));
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

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA_VALUE,
                CREDITORS_WITHIN_CURRENT_PERIOD_PATH)));
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA_VALUE,
                CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH)));
    }

    @Test
    @DisplayName("Errors returned when no totals provided")
    void testErrorsReturnedWhenNoTotalsProvided() throws ServiceException,
            DataException {

        creditorsWithinOneYear.setCurrentPeriod(new CurrentPeriod());
        creditorsWithinOneYear.setPreviousPeriod(new PreviousPeriod());

        mockValidBalanceSheetCurrentPeriod();
        mockValidBalanceSheetPreviousPeriod();

        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_NAME, MANDATORY_ELEMENT_MISSING_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
                CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
                PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertEquals(4, errors.getErrorCount());
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
                CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE,
                CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
                CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE,
                CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Data exception thrown when company service API call fails")
    void testDataExceptionThrown() throws ServiceException {

        Errors errors = new Errors();

        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenThrow(mockServiceException);

        assertThrows(DataException.class,
                () -> validator.validateCreditorsWithinOneYear(creditorsWithinOneYear,
                        mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest));
    }

    @Test
    @DisplayName("Data exception thrown when current balancesheet call fails")
    void testDataExceptionThrownWhenCurrentBalanceSheetCallFails() throws ServiceException,
            DataException {

        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        when(mockCurrentPeriodService.find(COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(new DataException(""));

        assertThrows(DataException.class,
                () -> validator.validateCreditorsWithinOneYear(creditorsWithinOneYear,
                        mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest));
    }

    @Test
    @DisplayName("Data exception thrown when previous balancesheet call fails")
    void testDataExceptionThrownWhenPreviousBalanceSheetCallFails() throws ServiceException,
            DataException {

        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        mockValidBalanceSheetCurrentPeriod();

        when(mockPreviousPeriodService.find(COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(new DataException(""));

        assertThrows(DataException.class,
                () -> validator.validateCreditorsWithinOneYear(creditorsWithinOneYear,
                        mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest));
    }

    private void createValidNoteCurrentPeriod() {
        CurrentPeriod creditorsCurrent = new CurrentPeriod();
        creditorsCurrent.setAccrualsAndDeferredIncome(1L);
        creditorsCurrent.setBankLoansAndOverdrafts(1L);
        creditorsCurrent.setFinanceLeasesAndHirePurchaseContracts(1L);
        creditorsCurrent.setOtherCreditors(1L);
        creditorsCurrent.setTaxationAndSocialSecurity(1L);
        creditorsCurrent.setTradeCreditors(1L);
        creditorsCurrent.setTotal(6L);

        creditorsWithinOneYear.setCurrentPeriod(creditorsCurrent);
    }

    private void createValidNotePreviousPeriod() {
        PreviousPeriod previousCreditors = new PreviousPeriod();
        previousCreditors.setAccrualsAndDeferredIncome(2L);
        previousCreditors.setBankLoansAndOverdrafts(2L);
        previousCreditors.setFinanceLeasesAndHirePurchaseContracts(2L);
        previousCreditors.setOtherCreditors(2L);
        previousCreditors.setTaxationAndSocialSecurity(2L);
        previousCreditors.setTradeCreditors(2L);
        previousCreditors.setTotal(12L);

        creditorsWithinOneYear.setPreviousPeriod(previousCreditors);
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
            otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(6L);
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
            otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(12L);
            balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);
        }

        previousPeriodTest.setBalanceSheet(balanceSheet);

        previousPeriodResponseObject.setData(previousPeriodTest);
        return previousPeriodResponseObject;
    }

    private void mockValidBalanceSheetCurrentPeriod() throws DataException {
        doReturn(generateValidCurrentPeriodResponseObject(true)).when(mockCurrentPeriodService).find(
                COMPANY_ACCOUNTS_ID, mockRequest);
    }

    private void mockValidBalanceSheetPreviousPeriod() throws DataException {
        doReturn(generateValidPreviousPeriodResponseObject(true)).when(mockPreviousPeriodService).find(
                COMPANY_ACCOUNTS_ID, mockRequest);
    }

    private void mockBalanceSheetCurrentPeriodWithoutNoteValue() throws DataException {
        doReturn(generateValidCurrentPeriodResponseObject(false)).when(mockCurrentPeriodService).find(
                COMPANY_ACCOUNTS_ID, mockRequest);
    }

    private void mockBalanceSheetPreviousPeriodWithoutNoteValue() throws DataException {
        doReturn(generateValidPreviousPeriodResponseObject(false)).when(mockPreviousPeriodService).find(
                COMPANY_ACCOUNTS_ID, mockRequest);
    }
}
