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

    private static String DEBTORS_PATH = "$.debtors";
    private static String DEBTORS_PATH_PREVIOUS = DEBTORS_PATH + ".previous_period";

    private static String CURRENT_TOTAL_PATH = DEBTORS_PATH + ".current_period.total";
    private static String PREVIOUS_TOTAL_PATH = DEBTORS_PATH_PREVIOUS + ".total";
    private static String PREVIOUS_TRADE_DEBTORS = DEBTORS_PATH_PREVIOUS + ".trade_debtors";
    private static String PREVIOUS_PREPAYMENTS = DEBTORS_PATH_PREVIOUS + ".prepayments_and_accrued_income";
    private static String PREVIOUS_OTHER_DEBTORS = DEBTORS_PATH_PREVIOUS + ".other_debtors";
    private static String PREVIOUS_GREATER_THAN_ONE_YEAR = DEBTORS_PATH_PREVIOUS + ".greater_than_one_year";

    private Debtors debtors;
    private Errors errors;

    @Mock
    private Transaction transaction;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CompanyProfile companyProfile;

    @Mock
    private EnvironmentReader environmentReader;

    @Mock
    private RestClientException restClientException;

    private DebtorsValidator validator;

    @BeforeEach
    void setup() {
        debtors = new Debtors();
        errors = new Errors();
        validator = new DebtorsValidator(environmentReader, restTemplate);
    }

    @Test
    @DisplayName("Tests the validation passes on valid single year debtors resource")
    public void testSuccessfulSingleYearDebtorsNote() {

        addValidCurrentDebtors();

        errors = validator.validateDebtors(debtors, transaction);

        assertFalse(errors.hasErrors());

    }

    @Test
    @DisplayName("Tests the validation passes on valid multiple year debtors resource")
    public void testSuccessfulMultipleYearDebtorsNote() throws Throwable {

        CompanyProfile companyProfile = addMultipleYearFilingCompany();

        addValidCurrentDebtors();

        PreviousPeriod previousDebtors = new PreviousPeriod();
        previousDebtors.setTradeDebtors(2L);
        previousDebtors.setPrepaymentsAndAccruedIncome(4L);
        previousDebtors.setGreaterThanOneYear(6L);
        previousDebtors.setOtherDebtors(8L);
        previousDebtors.setTotal(20L);

        debtors.setPreviousPeriod(previousDebtors);

        when(transaction.getCompanyNumber()).thenReturn("12345");
        when(environmentReader.getMandatoryString(any())).thenReturn("http://test-url");
        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(companyProfile);

        validator.isMultipleYearFiler(transaction);
        errors = validator.validateDebtors(debtors, transaction);

        assertFalse(errors.hasErrors());

    }

    @Test
    @DisplayName("Tests the validation fails on single year filer filing previous period")
    public void tesInvalidMultipleYearDebtorsNote() throws Throwable {

        CompanyProfile companyProfile = new CompanyProfile();

        addValidCurrentDebtors();

        PreviousPeriod previousDebtors = new PreviousPeriod();
        previousDebtors.setTradeDebtors(2L);
        previousDebtors.setTotal(20L);

        debtors.setPreviousPeriod(previousDebtors);

        when(transaction.getCompanyNumber()).thenReturn("12345");
        when(environmentReader.getMandatoryString(any())).thenReturn("http://test-url");
        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(companyProfile);

        ReflectionTestUtils.setField(validator, "inconsistentData", "inconsistent_data");

        validator.isMultipleYearFiler(transaction);
        errors = validator.validateDebtors(debtors, transaction);

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
    public void testIncorrectPreviousDebtorsTotal() throws Throwable {

        CompanyProfile companyProfile = addMultipleYearFilingCompany();

        addValidCurrentDebtors();

        PreviousPeriod previousDebtors = new PreviousPeriod();
        previousDebtors.setTradeDebtors(2L);
        previousDebtors.setTotal(20L);

        debtors.setPreviousPeriod(previousDebtors);

        when(transaction.getCompanyNumber()).thenReturn("12345");
        when(environmentReader.getMandatoryString(any())).thenReturn("http://test-url");
        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(companyProfile);

        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        validator.isMultipleYearFiler(transaction);
        errors = validator.validateDebtors(debtors, transaction);

        assertTrue(errors.containsError(
            new Error("incorrect_total", PREVIOUS_TOTAL_PATH,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));

    }

    @Test
    @DisplayName("Tests the validation fails on previous period missing total")
    public void testMissingPreviousDebtorsTotal() throws Throwable {

        CompanyProfile companyProfile = addMultipleYearFilingCompany();

        addValidCurrentDebtors();

        PreviousPeriod previousDebtors = new PreviousPeriod();
        previousDebtors.setTradeDebtors(2L);

        debtors.setPreviousPeriod(previousDebtors);

        when(transaction.getCompanyNumber()).thenReturn("12345");
        when(environmentReader.getMandatoryString(any())).thenReturn("http://test-url");
        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(companyProfile);

        ReflectionTestUtils.setField(validator, "invalidNote", "invalid_note");

        validator.isMultipleYearFiler(transaction);
        errors = validator.validateDebtors(debtors, transaction);

        Mockito.verify(restTemplate, Mockito.atLeastOnce()).getForObject(anyString(), any(Class.class));

        assertTrue(errors.containsError(
            new Error("invalid_note", PREVIOUS_TOTAL_PATH,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));

    }

    @Test
    @DisplayName("Tests Rest exception is thrown when company-profile not found")
    public void testRestExceptionThrownWhenApiCallFails() throws RestException {

        addValidCurrentDebtors();

        when(restTemplate.getForObject(anyString(), any(Class.class))).thenThrow(restClientException);

        Executable executable = () -> {
            validator.isMultipleYearFiler(transaction);
        };
        assertThrows(RestException.class, executable);
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

    @Test
    @DisplayName("Tests current period incorrect total throws error")
    public void testIncorrectCurrentTotal() {

        CurrentPeriod currentDebtors = new CurrentPeriod();
        currentDebtors.setTradeDebtors(1L);
        currentDebtors.setPrepaymentsAndAccruedIncome(2L);
        currentDebtors.setGreaterThanOneYear(3L);
        currentDebtors.setOtherDebtors(4L);
        currentDebtors.setTotal(100L);

        debtors.setCurrentPeriod(currentDebtors);
        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        errors = validator.validateDebtors(debtors, transaction);

        assertTrue(errors.containsError(
            new Error("incorrect_total", CURRENT_TOTAL_PATH,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));
    }

    @Test
    @DisplayName("Tests current period missing total throws error")
    public void testMissingCurrentTotal() {

        CurrentPeriod currentDebtors = new CurrentPeriod();
        currentDebtors.setTradeDebtors(1L);

        debtors.setCurrentPeriod(currentDebtors);
        ReflectionTestUtils.setField(validator, "invalidNote", "invalid_note");

        errors = validator.validateDebtors(debtors, transaction);

        assertTrue(errors.hasErrors());

        assertTrue(errors.containsError(
            new Error("invalid_note", CURRENT_TOTAL_PATH,
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
}

