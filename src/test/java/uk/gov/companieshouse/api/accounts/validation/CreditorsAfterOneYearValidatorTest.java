package uk.gov.companieshouse.api.accounts.validation;

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
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreditorsAfterOneYearValidatorTest {

    private static final String CREDITORS_AFTER_PATH = "$.creditors_after_one_year";

    private static final String CREDITORS_AFTER_CURRENT_PERIOD_PATH =
        CREDITORS_AFTER_PATH + ".current_period";

    private static final String CREDITORS_AFTER_PREVIOUS_PERIOD_PATH =
        CREDITORS_AFTER_PATH + ".previous_period";

    private static final String CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH =
        CREDITORS_AFTER_CURRENT_PERIOD_PATH + ".total";

    private static final String CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH =
        CREDITORS_AFTER_PREVIOUS_PERIOD_PATH + ".total";

    private static final String CREDITORS_AFTER_CURRENT_PERIOD_DETAILS_PATH =
        CREDITORS_AFTER_CURRENT_PERIOD_PATH + ".details";

    private static final String CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME = "currentBalanceSheetNotEqual";
    private static final String CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE =
        "value_not_equal_to_current_period_on_balance_sheet";

    private static final String PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME = "previousBalanceSheetNotEqual";
    private static final String PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE =
        "value_not_equal_to_previous_period_on_balance_sheet";

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountId";

    private static final String INVALID_NOTE_NAME = "invalidNote";
    private static final String INVALID_NOTE_VALUE = "invalid_note";

    private static final String INCORRECT_TOTAL_NAME = "incorrectTotal";
    private static final String INCORRECT_TOTAL_VALUE = "incorrect_total";

    private static final String INCONSISTENT_DATA_NAME = "inconsistentData";
    private static final String INCONSISTENT_DATA_VALUE = "inconsistent_data";

    private CreditorsAfterOneYear creditorsAfterOneYear;

    private Errors errors;

    private CreditorsAfterOneYearValidator validator;

    @Mock
    private CompanyService mockCompanyService;

    @Mock
    private CurrentPeriodService mockCurrentPeriodService;

    @Mock
    private PreviousPeriodService mockPreviousPeriodService;
    
    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private ServiceException mockServiceException;

    @BeforeEach
    void setUp() {
        creditorsAfterOneYear = new CreditorsAfterOneYear();
        errors = new Errors();
        validator = new CreditorsAfterOneYearValidator(
            mockCompanyService, mockCurrentPeriodService, mockPreviousPeriodService);
    }

    @Test
    @DisplayName("Validation passes on a valid single year creditors after resource")
    void testSuccessfulSingleYearCreditorsAfterNote() throws DataException {
        
        createValidCurrentPeriodCreditorsAfter();

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        errors = validator.validateCreditorsAfterOneYear(creditorsAfterOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validation passes on a valid multi year creditors after resource")
    void testSuccessfullMultiYearCreditorsAfterNote() throws DataException, ServiceException {

        createValidCurrentPeriodCreditorsAfter();
        createValidPreviousPeriodCreditorsAfter();

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        errors = validator.validateCreditorsAfterOneYear(creditorsAfterOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Error thrown when total field missing")
    void testErrorThrownWhenMandatoryFieldsMissing() throws ServiceException, DataException {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setOtherCreditors(1L);
        creditorsAfterOneYear.setCurrentPeriod(currentPeriod);

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setOtherCreditors(5L);
        creditorsAfterOneYear.setPreviousPeriod(previousPeriod);

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INVALID_NOTE_NAME, INVALID_NOTE_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateCreditorsAfterOneYear(creditorsAfterOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(INVALID_NOTE_VALUE, CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(INVALID_NOTE_VALUE, CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Error thrown when total value incorrect")
    void testErrorThrownWhenTotalIsIncorrect() throws ServiceException, DataException {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setOtherCreditors(1L);
        currentPeriod.setTotal(2L);
        creditorsAfterOneYear.setCurrentPeriod(currentPeriod);

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setOtherCreditors(5L);
        previousPeriod.setTotal(50L);
        creditorsAfterOneYear.setPreviousPeriod(previousPeriod);

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_NAME, INCORRECT_TOTAL_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateCreditorsAfterOneYear(creditorsAfterOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE, CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE, CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Error thrown when current period balance sheet creditors after is not provided but the note has a value")
    void testErrorThrownWhenBalanceSheetValueNotProvidedButNoteValueIsProvided() throws ServiceException, DataException {

        createValidCurrentPeriodCreditorsAfter();
        createValidPreviousPeriodCreditorsAfter();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_NAME, INCORRECT_TOTAL_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateCreditorsAfterOneYear(creditorsAfterOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE,
            CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE,
            CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Error thrown when single year filer creates previous period creditors after")
    void testErrorThrownWhenPreviousPeriodFiledOnSingleYearCompany() throws ServiceException, DataException {

        createValidCurrentPeriodCreditorsAfter();
        createValidPreviousPeriodCreditorsAfter();

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(false);

        ReflectionTestUtils.setField(validator, INCONSISTENT_DATA_NAME, INCONSISTENT_DATA_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateCreditorsAfterOneYear(creditorsAfterOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(INCONSISTENT_DATA_VALUE,
            CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Error thrown when no total and no additional information provided")
    void testErrorThrownWhenNoTotalAndNoDetailsProvided() throws DataException {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        creditorsAfterOneYear.setCurrentPeriod(currentPeriod);

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        ReflectionTestUtils.setField(validator, INVALID_NOTE_NAME, INVALID_NOTE_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateCreditorsAfterOneYear(creditorsAfterOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(INVALID_NOTE_VALUE,
            CREDITORS_AFTER_CURRENT_PERIOD_DETAILS_PATH)));
    }

    @Test
    @DisplayName("Data exception thrown when company service api call fails")
    void testDataExceptionThrown() throws ServiceException, DataException {

        createValidCurrentPeriodCreditorsAfter();
        createValidPreviousPeriodCreditorsAfter();

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenThrow(mockServiceException);

        assertThrows(DataException.class,
            () -> validator.validateCreditorsAfterOneYear(creditorsAfterOneYear,
                mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest));
    }

    @Test
    @DisplayName("Data exception thrown when mongo current balancesheet call fails")
    void testDataExceptionThrownWhenCurrentBalanceSheetMongoCallFails() throws DataException {

        createValidCurrentPeriodCreditorsAfter();
        createValidPreviousPeriodCreditorsAfter();

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(COMPANY_ACCOUNTS_ID);
        when(mockCurrentPeriodService.findById(COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(new DataException(""));

        assertThrows(DataException.class,
            () -> validator.validateCreditorsAfterOneYear(creditorsAfterOneYear,
                mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest));
    }

    @Test
    @DisplayName("Data exception thrown when mongo previous balancesheet call fails")
    void testDataExceptionThrownWhenPreviousBalanceSheetMongoCallFails() throws DataException {

        createValidCurrentPeriodCreditorsAfter();
        createValidPreviousPeriodCreditorsAfter();

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(COMPANY_ACCOUNTS_ID);
        when(mockPreviousPeriodService.findById(COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(new DataException(""));

        assertThrows(DataException.class,
            () -> validator.validateCreditorsAfterOneYear(creditorsAfterOneYear,
                mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest));
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
            ErrorType.VALIDATION.getType());
    }

    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> generateValidPreviousPeriodResponseObject() {

        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> previousPeriodResponseObject =
            new ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod>(ResponseStatus.FOUND);

        uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod previousPeriodTest =
            new uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod();

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(6L);
        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);
        previousPeriodTest.setBalanceSheet(balanceSheet);

        previousPeriodResponseObject.setData(previousPeriodTest);

        return previousPeriodResponseObject;
    }

    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> generateValidCurrentPeriodResponseObject() {

        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> currentPeriodResponseObject =
            new ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod>(ResponseStatus.FOUND);

        uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod currentPeriodTest =
            new uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod();

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(30L);
        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);
        currentPeriodTest.setBalanceSheet(balanceSheet);

        currentPeriodResponseObject.setData(currentPeriodTest);
        return currentPeriodResponseObject;

    }

    private void createValidCurrentPeriodCreditorsAfter() {

        CurrentPeriod creditorsAfterCurrent = new CurrentPeriod();
        creditorsAfterCurrent.setBankLoansAndOverdrafts(5L);
        creditorsAfterCurrent.setFinanceLeasesAndHirePurchaseContracts(10L);
        creditorsAfterCurrent.setOtherCreditors(15L);
        creditorsAfterCurrent.setTotal(30L);
        creditorsAfterCurrent.setDetails("test details data");
        
        creditorsAfterOneYear.setCurrentPeriod(creditorsAfterCurrent);
    }

    private void createValidPreviousPeriodCreditorsAfter() {

        PreviousPeriod creditorsAfterPrevious = new PreviousPeriod();
        creditorsAfterPrevious.setBankLoansAndOverdrafts(1L);
        creditorsAfterPrevious.setFinanceLeasesAndHirePurchaseContracts(2L);
        creditorsAfterPrevious.setOtherCreditors(3L);
        creditorsAfterPrevious.setTotal(6L);

        creditorsAfterOneYear.setPreviousPeriod(creditorsAfterPrevious);
    }
}
