
package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.mongodb.MongoException;
import java.time.LocalDate;
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
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.Debtors;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.company.account.CompanyAccountApi;
import uk.gov.companieshouse.api.model.company.account.LastAccountsApi;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DebtorsValidatorTest {

    private static final String DEBTORS_PATH = "$.debtors";
    private static final String DEBTORS_PATH_PREVIOUS = DEBTORS_PATH + ".previous_period";
    private static final String CURRENT_TOTAL_PATH = DEBTORS_PATH + ".current_period.total";
    private static final String PREVIOUS_TOTAL_PATH = DEBTORS_PATH_PREVIOUS + ".total";
    private static final String PREVIOUS_TRADE_DEBTORS = DEBTORS_PATH_PREVIOUS + ".trade_debtors";
    private static final String PREVIOUS_PREPAYMENTS = DEBTORS_PATH_PREVIOUS + ".prepayments_and_accrued_income";
    private static final String PREVIOUS_OTHER_DEBTORS = DEBTORS_PATH_PREVIOUS + ".other_debtors";
    private static final String PREVIOUS_GREATER_THAN_ONE_YEAR = DEBTORS_PATH_PREVIOUS + ".greater_than_one_year";
    private static final String COMPANY_NUMBER = "12345";
    private static final String INVALID_NOTE_VALUE = "invalid_note";
    private static final String INVALID_NOTE_NAME = "invalidNote";
    private static final String INCORRECT_TOTAL_NAME = "incorrectTotal";
    private static final String INCORRECT_TOTAL_VALUE = "incorrect_total";
    private static final String INCONSISTENT_DATA_NAME = "inconsistentData";
    private static final String INCONSISTENT_DATA_VALUE = "inconsistent_data";
    private static final String COMPANY_ACCOUNTS_ID ="123abc";

    private static final long INVALID_TOTAL = 200L;
    public static final String CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME = "currentBalanceSheetNotEqual";
    public static final String CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE =
        "value_not_equal_to_current_period_on_balance_sheet";
    public static final String PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME = "previousBalanceSheetNotEqual";
    public static final String PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE =
        "value_not_equal_to_previous_period_on_balance_sheet";

    private Debtors debtors;
    private Errors errors;

    @Mock
    private CompanyService mockCompanyService;

    @Mock
    private uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod currentPeriod;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private ServiceException mockServiceException;

    @Mock
    private MongoException mockMongoException;

    @Mock
    private CurrentPeriodService mockCurrentPeriodService;

    @Mock
    private PreviousPeriodService mockPreviousPeriodService;

    @Mock
    private ResponseObject mockResponseObject;

    private DebtorsValidator validator;

    @BeforeEach
    void setup() {
        debtors = new Debtors();
        errors = new Errors();
        validator = new DebtorsValidator(mockCompanyService, mockCurrentPeriodService, mockPreviousPeriodService);
    }

    @Test
    @DisplayName("Tests the validation passes on valid single year debtors resource")
    void testSuccessfulSingleYearDebtorsNote() throws DataException {

        addValidCurrentDebtors();

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateNPreviousNullDebtorsBalanceSheetResponse()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID,
            mockRequest);

        errors = validator.validateDebtors(debtors, mockTransaction, COMPANY_ACCOUNTS_ID,mockRequest);

        assertFalse(errors.hasErrors());

    }

    @Test
    @DisplayName("Tests the validation passes on valid multiple year debtors resource")
    void testSuccessfulMultipleYearDebtorsNote() throws DataException, ServiceException {

        addValidCurrentDebtors();
        addValidPreviousDebtors();

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID,
            mockRequest);

        when(mockCompanyService.getCompanyProfile(mockTransaction.getCompanyNumber()))
            .thenReturn(createCompanyProfileMultipleYearFiler());

        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateDebtors(debtors, mockTransaction, COMPANY_ACCOUNTS_ID,mockRequest);

        assertFalse(errors.hasErrors());

    }

    @Test
    @DisplayName("Tests the validation fails on single year filer filing previous period")
    void tesInvalidMultipleYearDebtorsNote() throws DataException, ServiceException {

        addValidCurrentDebtors();
        addValidPreviousDebtors();

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID,
            mockRequest);

        when(mockCompanyService.getCompanyProfile(mockTransaction.getCompanyNumber()))
            .thenReturn(createCompanyProfileSingleYearFiler());

        ReflectionTestUtils.setField(validator, INCONSISTENT_DATA_NAME, INCONSISTENT_DATA_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateDebtors(debtors, mockTransaction, COMPANY_ACCOUNTS_ID,mockRequest);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(INCONSISTENT_DATA_VALUE, PREVIOUS_TRADE_DEBTORS)));
        assertTrue(errors.containsError(createError(INCONSISTENT_DATA_VALUE, PREVIOUS_PREPAYMENTS)));
        assertTrue(errors.containsError(createError(INCONSISTENT_DATA_VALUE, PREVIOUS_GREATER_THAN_ONE_YEAR)));
        assertTrue(errors.containsError(createError(INCONSISTENT_DATA_VALUE, PREVIOUS_OTHER_DEBTORS)));
        assertTrue(errors.containsError(createError(INCONSISTENT_DATA_VALUE, PREVIOUS_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Tests the validation fails on previous period incorrect total")
    void testIncorrectPreviousDebtorsTotal() throws DataException, ServiceException {

        addValidCurrentDebtors();

        PreviousPeriod previousDebtors = new PreviousPeriod();
        previousDebtors.setTradeDebtors(2L);
        previousDebtors.setTotal(INVALID_TOTAL);

        debtors.setPreviousPeriod(previousDebtors);

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID,
            mockRequest);

        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(mockCompanyService.getCompanyProfile(COMPANY_NUMBER))
            .thenReturn(createCompanyProfileMultipleYearFiler());

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_NAME, INCORRECT_TOTAL_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        when(mockCompanyService.getCompanyProfile(mockTransaction.getCompanyNumber()))
            .thenReturn(createCompanyProfileMultipleYearFiler());

        errors = validator.validateDebtors(debtors, mockTransaction, COMPANY_ACCOUNTS_ID,mockRequest);

        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE, PREVIOUS_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Tests the validation fails on previous period missing total")
    void testMissingPreviousDebtorsTotal() throws DataException, ServiceException {

        addValidCurrentDebtors();

        PreviousPeriod previousDebtors = new PreviousPeriod();
        previousDebtors.setTradeDebtors(2L);
        debtors.setPreviousPeriod(previousDebtors);

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID,
            mockRequest);

        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(mockCompanyService.getCompanyProfile(COMPANY_NUMBER))
            .thenReturn(createCompanyProfileMultipleYearFiler());

        ReflectionTestUtils.setField(validator, INVALID_NOTE_NAME, INVALID_NOTE_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);
        errors = validator.validateDebtors(debtors, mockTransaction, COMPANY_ACCOUNTS_ID,mockRequest);

        assertTrue(errors.hasErrors());

        assertTrue(errors.containsError(createError(INVALID_NOTE_VALUE, PREVIOUS_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Tests current period incorrect total throws error")
    void testIncorrectCurrentTotal() throws DataException {

        CurrentPeriod currentDebtors = new CurrentPeriod();
        currentDebtors.setTradeDebtors(1L);
        currentDebtors.setPrepaymentsAndAccruedIncome(2L);
        currentDebtors.setGreaterThanOneYear(3L);
        currentDebtors.setOtherDebtors(4L);
        currentDebtors.setTotal(INVALID_TOTAL);

        debtors.setCurrentPeriod(currentDebtors);
        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_NAME, INCORRECT_TOTAL_VALUE);

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateNPreviousNullDebtorsBalanceSheetResponse()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID,
            mockRequest);

        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        errors = validator.validateDebtors(debtors, mockTransaction, COMPANY_ACCOUNTS_ID,mockRequest);

        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE, CURRENT_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Tests current period missing total throws error")
    void testMissingCurrentTotal() throws DataException {

        CurrentPeriod currentDebtors = new CurrentPeriod();
        currentDebtors.setTradeDebtors(1L);

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateNPreviousNullDebtorsBalanceSheetResponse()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID,
            mockRequest);

        debtors.setCurrentPeriod(currentDebtors);
        ReflectionTestUtils.setField(validator, INVALID_NOTE_NAME, INVALID_NOTE_VALUE);

        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateDebtors(debtors, mockTransaction, COMPANY_ACCOUNTS_ID,mockRequest);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(INVALID_NOTE_VALUE, CURRENT_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Tests data exception thrown when company profile api call fails")
    void testDataExceptionThrown() throws DataException, ServiceException {

        addValidCurrentDebtors();
        addValidPreviousDebtors();

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID,
            mockRequest);

        when(mockCompanyService.getCompanyProfile(null)).thenThrow(mockServiceException);

        assertThrows(DataException.class,
            () -> validator.validateDebtors(debtors, mockTransaction, COMPANY_ACCOUNTS_ID,
                mockRequest));
    }

    @Test
    @DisplayName("Tests data exception thrown when current period api call fails")
    void testDataExceptionThrownWhenRetrievingCurrentPeriod() throws DataException {

        addValidCurrentDebtors();;

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        when(mockCurrentPeriodService.findById(COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(mockMongoException);

        assertThrows(DataException.class,
            () -> validator.validateDebtors(debtors, mockTransaction, COMPANY_ACCOUNTS_ID,
                mockRequest));

        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);

        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);
    }

    @Test
    @DisplayName("Assert different balance sheet and note value throws error")
    void testMismatchedDebtorsValues() throws DataException, ServiceException {

        addValidCurrentDebtors();
        addValidPreviousDebtors();

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateDifferentCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateDifferentPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID,
            mockRequest);

        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(mockCompanyService.getCompanyProfile(COMPANY_NUMBER))
            .thenReturn(createCompanyProfileMultipleYearFiler());

        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);

        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateDebtors(debtors, mockTransaction, COMPANY_ACCOUNTS_ID,mockRequest);

        assertTrue(errors.containsError(createError(
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE, CURRENT_TOTAL_PATH)));

        assertTrue(errors.containsError(createError(
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE, PREVIOUS_TOTAL_PATH)));

    }

    @Test
    @DisplayName("Assert empty balance sheet and populated note value throws error")
    void testEmptyBalanceSheetMismatchedDebtorsValues() throws DataException, ServiceException {

        addValidCurrentDebtors();
        addValidPreviousDebtors();

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateNullDebtorsBalanceSheetResponse()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateNPreviousNullDebtorsBalanceSheetResponse()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID,
            mockRequest);

        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(mockCompanyService.getCompanyProfile(COMPANY_NUMBER))
            .thenReturn(createCompanyProfileMultipleYearFiler());

        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);

        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateDebtors(debtors, mockTransaction, COMPANY_ACCOUNTS_ID,mockRequest);

        assertTrue(errors.containsError(createError(
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE, CURRENT_TOTAL_PATH)));

        assertTrue(errors.containsError(createError(
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE, PREVIOUS_TOTAL_PATH)));

    }

    @Test
    @DisplayName("Assert successful cross validation")
    void testValidDebtorsCrossValidation() throws DataException, ServiceException {

        addValidCurrentDebtors();
        addValidPreviousDebtors();

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID,
            mockRequest);

        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(mockCompanyService.getCompanyProfile(COMPANY_NUMBER))
            .thenReturn(createCompanyProfileMultipleYearFiler());

        errors = validator.validateDebtors(debtors, mockTransaction, COMPANY_ACCOUNTS_ID,mockRequest);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Assert empty note and populated balance sheet value throws error")
    void testEmptyNoteMismatchedDebtorsValues() throws DataException, ServiceException {

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID,
            mockRequest);

        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);

        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateDebtors(debtors, mockTransaction, COMPANY_ACCOUNTS_ID,mockRequest);

        assertTrue(errors.containsError(createError(
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE, CURRENT_TOTAL_PATH)));

        assertTrue(errors.containsError(createError(
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE, PREVIOUS_TOTAL_PATH)));

    }

    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> generateValidCurrentPeriodResponseObject() {
        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> currentPeriodResponseObject =
            new ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod>(
                ResponseStatus.FOUND);


        uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod currentPeriodTest =
            new uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod();
        CurrentAssets currentAssets = new CurrentAssets();

        currentAssets.setDebtors(10L);
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
        currentAssets.setDebtors(20L);
        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCurrentAssets(currentAssets);
        previousPeriodTest.setBalanceSheet(balanceSheet);

        previousPeriodResponseObject.setData(previousPeriodTest);
        return previousPeriodResponseObject;
    }

    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> generateDifferentCurrentPeriodResponseObject() {
        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> currentPeriodResponseObject =
            new ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod>(
                ResponseStatus.FOUND);


        uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod currentPeriodTest =
            new uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod();
        CurrentAssets currentAssets = new CurrentAssets();

        currentAssets.setDebtors(1L);
        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCurrentAssets(currentAssets);
        currentPeriodTest.setBalanceSheet(balanceSheet);

        currentPeriodResponseObject.setData(currentPeriodTest);
        return currentPeriodResponseObject;
    }

    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> generateNullDebtorsBalanceSheetResponse() {
        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> currentPeriodResponseObject =
            new ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod>(
                ResponseStatus.FOUND);

        uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod currentPeriodTest =
            new uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod();
        CurrentAssets currentAssets = new CurrentAssets();

        BalanceSheet balanceSheet = new BalanceSheet();
        currentPeriodTest.setBalanceSheet(balanceSheet);

        currentPeriodResponseObject.setData(currentPeriodTest);
        return currentPeriodResponseObject;
    }

    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> generateNPreviousNullDebtorsBalanceSheetResponse() {
        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> previousPeriodResponseObject =
            new ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod>(
                ResponseStatus.FOUND);

        uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod previousPeriod =
            new uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod();
        CurrentAssets currentAssets = new CurrentAssets();

        BalanceSheet balanceSheet = new BalanceSheet();
        previousPeriod.setBalanceSheet(balanceSheet);

        previousPeriodResponseObject.setData(previousPeriod);
        return previousPeriodResponseObject;
    }

    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> generateDifferentPreviousPeriodResponseObject() {
        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> previousPeriodResponseObject =
            new ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod>(
                ResponseStatus.FOUND);

        uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod previousPeriodTest =
            new uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod();
        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setDebtors(2L);
        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCurrentAssets(currentAssets);
        previousPeriodTest.setBalanceSheet(balanceSheet);

        previousPeriodResponseObject.setData(previousPeriodTest);
        return previousPeriodResponseObject;
    }

    private void addValidCurrentDebtors() {

        CurrentPeriod currentDebtors = new CurrentPeriod();
        currentDebtors.setTradeDebtors(1L);
        currentDebtors.setPrepaymentsAndAccruedIncome(2L);
        currentDebtors.setGreaterThanOneYear(3L);
        currentDebtors.setOtherDebtors(4L);
        currentDebtors.setTotal(10L);
        currentDebtors.setDetails("details");

        debtors.setCurrentPeriod(currentDebtors);
    }

    private CompanyProfileApi createCompanyProfileMultipleYearFiler() {

        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        CompanyAccountApi companyAccountApi = new CompanyAccountApi();

        LastAccountsApi lastAccountsApi = new LastAccountsApi();
        lastAccountsApi.setType("lastaccounts");
        lastAccountsApi.setPeriodStartOn(LocalDate.now());

        companyAccountApi.setLastAccounts(lastAccountsApi);
        companyProfileApi.setAccounts(companyAccountApi);

        return companyProfileApi;
    }

    private CompanyProfileApi createCompanyProfileSingleYearFiler() {

        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        return companyProfileApi;
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
            ErrorType.VALIDATION.getType());
    }

    private void addValidPreviousDebtors() {
        PreviousPeriod previousDebtors = new PreviousPeriod();
        previousDebtors.setTradeDebtors(2L);
        previousDebtors.setPrepaymentsAndAccruedIncome(4L);
        previousDebtors.setGreaterThanOneYear(6L);
        previousDebtors.setOtherDebtors(8L);
        previousDebtors.setTotal(20L);

        debtors.setPreviousPeriod(previousDebtors);
    }
}
