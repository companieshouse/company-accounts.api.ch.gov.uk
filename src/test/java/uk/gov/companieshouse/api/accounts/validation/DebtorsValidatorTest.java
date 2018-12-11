package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.accounts.exception.RestException;
import uk.gov.companieshouse.api.accounts.model.rest.Accounts;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyProfile;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.Debtors;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.environment.EnvironmentReader;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DebtorsValidatorTest {

    private static final String DEBTORS_PATH = "$.debtors";
    private static final String DEBTORS_PATH_PREVIOUS = DEBTORS_PATH + ".previous_period";

    private static final String CURRENT_TOTAL_PATH = DEBTORS_PATH + ".current_period.total";
    private static final String PREVIOUS_TOTAL_PATH = DEBTORS_PATH_PREVIOUS + ".total";
    private static final String PREVIOUS_TRADE_DEBTORS = DEBTORS_PATH_PREVIOUS + ".trade_debtors";
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
    private Transaction mockTransaction;

    @Mock
    private RestTemplate mockRestTemplate;

    @Mock
    private EnvironmentReader mockEnvironmentReader;

    @Mock
    private RestClientException mockRestClientException;

    private DebtorsValidator validator;

    @BeforeEach
    void setup() {
        debtors = new Debtors();
        errors = new Errors();
        validator = new DebtorsValidator(mockEnvironmentReader, mockRestTemplate);
    }

    @Test
    @DisplayName("Tests the validation passes on valid single year debtors resource")
    void testSuccessfulSingleYearDebtorsNote() {

        addValidCurrentDebtors();

        errors = validator.validateDebtors(debtors, mockTransaction);

        assertFalse(errors.hasErrors());

    }

    @Test
    @DisplayName("Tests the validation passes on valid multiple year debtors resource")
    void testSuccessfulMultipleYearDebtorsNote() {

        CompanyProfile companyProfile = addMultipleYearFilingCompany();

        addValidCurrentDebtors();

        PreviousPeriod previousDebtors = new PreviousPeriod();
        previousDebtors.setTradeDebtors(2L);
        previousDebtors.setPrepaymentsAndAccruedIncome(4L);
        previousDebtors.setGreaterThanOneYear(6L);
        previousDebtors.setOtherDebtors(8L);
        previousDebtors.setTotal(20L);

        debtors.setPreviousPeriod(previousDebtors);

        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(mockEnvironmentReader.getMandatoryString(any())).thenReturn(TEST_URL);
        when(mockRestTemplate.getForObject(anyString(), any(Class.class))).thenReturn(companyProfile);

        validator.isMultipleYearFiler(mockTransaction);
        errors = validator.validateDebtors(debtors, mockTransaction);

        assertFalse(errors.hasErrors());

    }

    @Test
    @DisplayName("Tests the validation fails on single year filer filing previous period")
    void tesInvalidMultipleYearDebtorsNote() {

        CompanyProfile companyProfile = new CompanyProfile();

        addValidCurrentDebtors();

        PreviousPeriod previousDebtors = new PreviousPeriod();
        previousDebtors.setTradeDebtors(2L);
        previousDebtors.setTotal(2L);

        debtors.setPreviousPeriod(previousDebtors);

        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(mockEnvironmentReader.getMandatoryString(any())).thenReturn(TEST_URL);
        when(mockRestTemplate.getForObject(anyString(), any(Class.class))).thenReturn(companyProfile);

        ReflectionTestUtils.setField(validator, "inconsistentData", "inconsistent_data");

        validator.isMultipleYearFiler(mockTransaction);
        errors = validator.validateDebtors(debtors, mockTransaction);

        assertTrue(errors.hasErrors());

        assertTrue(errors.containsError(
            new Error("inconsistent_data", PREVIOUS_TRADE_DEBTORS,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));

        assertTrue(errors.containsError(
            new Error("inconsistent_data", PREVIOUS_TOTAL_PATH,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));

    }

    @Test
    @DisplayName("Tests the validation fails on previous period incorrect total")
    void testIncorrectPreviousDebtorsTotal() {

        CompanyProfile companyProfile = addMultipleYearFilingCompany();

        addValidCurrentDebtors();

        PreviousPeriod previousDebtors = new PreviousPeriod();
        previousDebtors.setTradeDebtors(2L);
        previousDebtors.setTotal(INVALID_TOTAL);

        debtors.setPreviousPeriod(previousDebtors);

        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(mockEnvironmentReader.getMandatoryString(any())).thenReturn(TEST_URL);
        when(mockRestTemplate.getForObject(anyString(), any(Class.class))).thenReturn(companyProfile);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_NAME, INCORRECT_TOTAL_VALUE);

        validator.isMultipleYearFiler(mockTransaction);
        errors = validator.validateDebtors(debtors, mockTransaction);

        assertTrue(errors.containsError(
            new Error(INCORRECT_TOTAL_VALUE, PREVIOUS_TOTAL_PATH,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));

    }

    @Test
    @DisplayName("Tests the validation fails on previous period missing total")
    void testMissingPreviousDebtorsTotal() {

        CompanyProfile companyProfile = addMultipleYearFilingCompany();

        addValidCurrentDebtors();

        PreviousPeriod previousDebtors = new PreviousPeriod();
        previousDebtors.setTradeDebtors(2L);

        debtors.setPreviousPeriod(previousDebtors);

        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(mockEnvironmentReader.getMandatoryString(any())).thenReturn(TEST_URL);
        when(mockRestTemplate.getForObject(anyString(), any(Class.class))).thenReturn(companyProfile);

        ReflectionTestUtils.setField(validator, INVALID_NOTE_NAME, INVALID_NOTE_VALUE);

        validator.isMultipleYearFiler(mockTransaction);
        errors = validator.validateDebtors(debtors, mockTransaction);

        Mockito.verify(mockRestTemplate, Mockito.atLeastOnce()).getForObject(anyString(), any(Class.class));

        assertTrue(errors.containsError(
            new Error(INVALID_NOTE_VALUE, PREVIOUS_TOTAL_PATH,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));

    }

    @Test
    @DisplayName("Tests Rest exception is thrown when company-profile not found")
    void testRestExceptionThrownWhenApiCallFails() {

        addValidCurrentDebtors();

        when(mockRestTemplate.getForObject(anyString(), any(Class.class))).thenThrow(mockRestClientException);

        Executable executable = () -> {
            validator.isMultipleYearFiler(mockTransaction);
        };
        assertThrows(RestException.class, executable);
    }

    @Test
    @DisplayName("Tests current period incorrect total throws error")
    void testIncorrectCurrentTotal() {

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
    void testMissingCurrentTotal() {

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

    private CompanyProfile addMultipleYearFilingCompany() {

        CompanyProfile companyProfile = new CompanyProfile();
        Accounts companyProfileAccounts = new Accounts();
        companyProfileAccounts.setLastAccounts("lastAccounts");
        companyProfile.setAccounts(companyProfileAccounts);
        return companyProfile;
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

}

