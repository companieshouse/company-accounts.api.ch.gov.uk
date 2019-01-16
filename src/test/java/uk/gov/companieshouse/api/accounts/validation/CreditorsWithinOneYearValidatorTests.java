package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.CreditorsWithinOneYear;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

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
    private static final String CREDITORS_WITHIN_CURRENT_PERIOD_DETAILS_PATH =
            CREDITORS_WITHIN_CURRENT_PERIOD_PATH + ".details";

    private static final String INVALID_NOTE_VALUE = "invalid_note";
    private static final String INVALID_NOTE_NAME = "invalidNote";
    private static final String INCORRECT_TOTAL_NAME = "incorrectTotal";
    private static final String INCORRECT_TOTAL_VALUE = "incorrect_total";
    private static final String INCONSISTENT_DATA_NAME = "inconsistentData";
    private static final String INCONSISTENT_DATA_VALUE = "inconsistent_data";

    private CreditorsWithinOneYear creditorsWithinOneYear;
    private Errors errors;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private CompanyService mockCompanyService;

    @Mock
    private ServiceException mockServiceException;

    private CreditorsWithinOneYearValidator validator;

    @BeforeEach
    void setup() {
        creditorsWithinOneYear = new CreditorsWithinOneYear();
        errors = new Errors();
        validator = new CreditorsWithinOneYearValidator(mockCompanyService);
    }

    @Test
    @DisplayName("Validation passes on valid single year creditors within resource")
    void testSuccessfulSingleYearCreditorsWithinNote() throws DataException, ServiceException {

        CreditorsWithinOneYear creditorsWithinOneYear = createValidCurrentPeriodCreditors();

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction);

        assertFalse(errors.hasErrors());

    }

    @Test
    @DisplayName("Validation passes on valid multiple year filer creditors within resource")
    void testsuccessfulMultipleYearCreditorsWithinNote() throws ServiceException, DataException {

        createValidCurrentPeriodCreditors();
        createPreviousPeriodCreditors();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction);

        assertFalse(errors.hasErrors());

    }

    @Test
    @DisplayName("Validation passes on valid creditors within resource with only additional " +
            "information entered")
    void testMultipleYearCreditorsWithinNoteAdditionalInfoOnly() throws ServiceException,
            DataException {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setDetails("test details");

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction);

        assertFalse(errors.hasErrors());

    }

    @Test
    @DisplayName("Error thrown when total field missing")
    void testErrorThrownWhenMandatoryFieldsMissing() throws ServiceException,
            DataException {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setTradeCreditors(1L);
        creditorsWithinOneYear.setCurrentPeriod(currentPeriod);

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setTaxationAndSocialSecurity(5L);
        creditorsWithinOneYear.setPreviousPeriod(previousPeriod);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INVALID_NOTE_NAME, INVALID_NOTE_VALUE);

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(INVALID_NOTE_VALUE,
                CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(INVALID_NOTE_VALUE,
                CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Error thrown when total value incorrect")
    void testErrorThrownWhenTotalIsIncorrect() throws ServiceException,
            DataException {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setTradeCreditors(1L);
        currentPeriod.setTotal(2L);
        creditorsWithinOneYear.setCurrentPeriod(currentPeriod);

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setTaxationAndSocialSecurity(5L);
        previousPeriod.setTotal(50L);
        creditorsWithinOneYear.setPreviousPeriod(previousPeriod);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_NAME, INCORRECT_TOTAL_VALUE);

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE,
                CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE,
                CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Error thrown when single year filer creates previous period creditors")
    void testErrorThrownWhenPreviousPeriodFiledOnSingleYearCompany() throws ServiceException,
            DataException {

        createValidCurrentPeriodCreditors();
        createPreviousPeriodCreditors();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(false);

        ReflectionTestUtils.setField(validator, INCONSISTENT_DATA_NAME, INCONSISTENT_DATA_VALUE);

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(INCONSISTENT_DATA_VALUE,
                CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Error thrown when no total and no additional information provided")
    void testErrorThrownWhenNoTotalAndNoDetailsProvided() throws ServiceException,
            DataException {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        creditorsWithinOneYear.setCurrentPeriod(currentPeriod);

        ReflectionTestUtils.setField(validator, INVALID_NOTE_NAME, INVALID_NOTE_VALUE);

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(INVALID_NOTE_VALUE,
                CREDITORS_WITHIN_CURRENT_PERIOD_DETAILS_PATH)));
    }

    @Test
    @DisplayName("Data exception thrown when company service api call fails")
    void testDataExceptionThrown() throws ServiceException,
            DataException {

        createValidCurrentPeriodCreditors();
        createPreviousPeriodCreditors();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenThrow(mockServiceException);

        assertThrows(DataException.class,
                () -> validator.validateCreditorsWithinOneYear(creditorsWithinOneYear,
                        mockTransaction));
    }

    private void createPreviousPeriodCreditors() {
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
    
    private CreditorsWithinOneYear createValidCurrentPeriodCreditors() {
        CreditorsWithinOneYear creditorsWithinOneYear = new CreditorsWithinOneYear();
        CurrentPeriod creditorsCurrent = new CurrentPeriod();
        creditorsCurrent.setAccrualsAndDeferredIncome(1L);
        creditorsCurrent.setBankLoansAndOverdrafts(1L);
        creditorsCurrent.setFinanceLeasesAndHirePurchaseContracts(1L);
        creditorsCurrent.setOtherCreditors(1L);
        creditorsCurrent.setTaxationAndSocialSecurity(1L);
        creditorsCurrent.setTradeCreditors(1L);
        creditorsCurrent.setTotal(6L);

        creditorsWithinOneYear.setCurrentPeriod(creditorsCurrent);
        return creditorsWithinOneYear;
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}
