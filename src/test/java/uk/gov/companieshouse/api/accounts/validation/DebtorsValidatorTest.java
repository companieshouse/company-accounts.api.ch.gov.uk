package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.api.ApiClientService;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.Debtors;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
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
    private static final String TEST_URL = "http://test-url";
    private static final String COMPANY_NUMBER = "12345";
    private static final String INVALID_NOTE_VALUE = "invalid_note";
    private static final String INVALID_NOTE_NAME = "invalidNote";
    private static final String INCORRECT_TOTAL_NAME = "incorrectTotal";
    private static final String INCORRECT_TOTAL_VALUE = "incorrect_total";
    private static final long INVALID_TOTAL = 200L;

    private Debtors debtors;
    private Errors errors;

    @Mock
    private CompanyService mockCompanyService;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private ApiClientService mockApiClientService;

    private DebtorsValidator validator;

    @BeforeEach
    void setup() {
        debtors = new Debtors();
        errors = new Errors();
        validator = new DebtorsValidator(mockCompanyService);
    }

    @Test
    @DisplayName("Tests the validation passes on valid single year debtors resource")
    void testSuccessfulSingleYearDebtorsNote() throws DataException {

        addValidCurrentDebtors();

        errors = validator.validateDebtors(debtors, mockTransaction);

        assertFalse(errors.hasErrors());

    }

    @Test
    @DisplayName("Tests the validation passes on valid multiple year debtors resource")
    void testSuccessfulMultipleYearDebtorsNote() throws DataException, ServiceException {

        addValidCurrentDebtors();

        PreviousPeriod previousDebtors = new PreviousPeriod();
        previousDebtors.setTradeDebtors(2L);
        previousDebtors.setPrepaymentsAndAccruedIncome(4L);
        previousDebtors.setGreaterThanOneYear(6L);
        previousDebtors.setOtherDebtors(8L);
        previousDebtors.setTotal(20L);

        debtors.setPreviousPeriod(previousDebtors);
        when(mockCompanyService.getCompanyProfile(mockTransaction.getCompanyNumber())).thenReturn(createCompanyProfileMultipleYearFiler());

        errors = validator.validateDebtors(debtors, mockTransaction);

        assertFalse(errors.hasErrors());

    }

    @Test
    @DisplayName("Tests the validation fails on single year filer filing previous period")
    void tesInvalidMultipleYearDebtorsNote() throws DataException, ServiceException {

        addValidCurrentDebtors();

        PreviousPeriod previousDebtors = new PreviousPeriod();
        previousDebtors.setTradeDebtors(2L);
        previousDebtors.setPrepaymentsAndAccruedIncome(4L);
        previousDebtors.setGreaterThanOneYear(6L);
        previousDebtors.setOtherDebtors(8L);
        previousDebtors.setTotal(20L);

        debtors.setPreviousPeriod(previousDebtors);

        when(mockCompanyService.getCompanyProfile(mockTransaction.getCompanyNumber())).thenReturn(createCompanyProfileSingleYearFiler());

        ReflectionTestUtils.setField(validator, "inconsistentData", "inconsistent_data");

        errors = validator.validateDebtors(debtors, mockTransaction);

        assertTrue(errors.hasErrors());

        assertTrue(errors.containsError(
            new Error("inconsistent_data", PREVIOUS_TRADE_DEBTORS,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));
        assertTrue(errors.containsError(
            new Error("inconsistent_data", PREVIOUS_PREPAYMENTS,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));
        assertTrue(errors.containsError(
            new Error("inconsistent_data", PREVIOUS_GREATER_THAN_ONE_YEAR,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));
        assertTrue(errors.containsError(
            new Error("inconsistent_data", PREVIOUS_OTHER_DEBTORS,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));

        assertTrue(errors.containsError(
            new Error("inconsistent_data", PREVIOUS_TOTAL_PATH,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));

    }

    @Test
    @DisplayName("Tests the validation fails on previous period incorrect total")
    void testIncorrectPreviousDebtorsTotal() throws DataException, ServiceException {

        addValidCurrentDebtors();

        PreviousPeriod previousDebtors = new PreviousPeriod();
        previousDebtors.setTradeDebtors(2L);
        previousDebtors.setTotal(INVALID_TOTAL);

        debtors.setPreviousPeriod(previousDebtors);

        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        when(mockCompanyService.getCompanyProfile(mockTransaction.getCompanyNumber())).thenReturn(createCompanyProfileMultipleYearFiler());

        errors = validator.validateDebtors(debtors, mockTransaction);

        assertTrue(errors.containsError(
            new Error(INCORRECT_TOTAL_VALUE, PREVIOUS_TOTAL_PATH,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));

    }

    @Test
    @DisplayName("Tests the validation fails on previous period missing total")
    void testMissingPreviousDebtorsTotal() throws DataException, ServiceException {

        addValidCurrentDebtors();

        PreviousPeriod previousDebtors = new PreviousPeriod();
        previousDebtors.setTradeDebtors(2L);

        debtors.setPreviousPeriod(previousDebtors);

        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(mockCompanyService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(createCompanyProfileMultipleYearFiler());

        ReflectionTestUtils.setField(validator, INVALID_NOTE_NAME, INVALID_NOTE_VALUE);

        errors = validator.validateDebtors(debtors, mockTransaction);

        assertTrue(errors.hasErrors());

        assertTrue(errors.containsError(
            new Error(INVALID_NOTE_VALUE, PREVIOUS_TOTAL_PATH,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));

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

        errors = validator.validateDebtors(debtors, mockTransaction);

        assertTrue(errors.containsError(
            new Error(INCORRECT_TOTAL_VALUE, CURRENT_TOTAL_PATH,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));
    }

    @Test
    @DisplayName("Tests current period missing total throws error")
    void testMissingCurrentTotal() throws DataException {

        CurrentPeriod currentDebtors = new CurrentPeriod();
        currentDebtors.setTradeDebtors(1L);

        debtors.setCurrentPeriod(currentDebtors);
        ReflectionTestUtils.setField(validator, INVALID_NOTE_NAME, INVALID_NOTE_VALUE);

        errors = validator.validateDebtors(debtors, mockTransaction);

        assertTrue(errors.hasErrors());

        assertTrue(errors.containsError(
            new Error(INVALID_NOTE_VALUE, CURRENT_TOTAL_PATH,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));
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
        companyAccountApi.setLastAccounts(lastAccountsApi);
        companyProfileApi.setAccounts(companyAccountApi);

        return companyProfileApi;
    }

    private CompanyProfileApi createCompanyProfileSingleYearFiler() {

        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        return companyProfileApi;
    }

}

