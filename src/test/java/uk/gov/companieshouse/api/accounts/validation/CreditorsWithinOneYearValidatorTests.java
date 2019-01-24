package uk.gov.companieshouse.api.accounts.validation;

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
import com.mongodb.MongoException;
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
    
    private static final String CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME = "currentBalanceSheetNotEqual";
    private static final String CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE =
            "value_not_equal_to_current_period_on_balance_sheet";
    private static final String PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME = "previousBalanceSheetNotEqual";
    private static final String PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE =
            "value_not_equal_to_previous_period_on_balance_sheet";
    
    private static final String COMPANY_ACCOUNTS_ID = "123abcefg";

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
    @DisplayName("Validation passes on valid single year creditors within resource")
    void testSuccessfulSingleYearCreditorsWithinNote() throws DataException, ServiceException {

        createValidCurrentPeriodCreditors();

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);
    
        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertFalse(errors.hasErrors());

    }

    @Test
    @DisplayName("Validation passes on valid multiple year filer creditors within resource")
    void testSuccessfulMultipleYearCreditorsWithinNote() throws ServiceException, DataException {

        createValidCurrentPeriodCreditors();
        createPreviousPeriodCreditors();
        
        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);
  
        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

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
        previousPeriod.setOtherCreditors(5L);
        creditorsWithinOneYear.setPreviousPeriod(previousPeriod);
        
        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);
  
        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INVALID_NOTE_NAME, INVALID_NOTE_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);
    
        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

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

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);
  
        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);
        
        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_NAME, INCORRECT_TOTAL_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE,
                CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE,
                CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH)));
    }
    
    @Test
    @DisplayName("Error thrown when current period balance sheet creditors is not provided but the note has a value")
    void testErrorThrownWhenBalanceSheetValueNotProvidedButNoteValueIsProvided() throws ServiceException,
            DataException {

        createValidCurrentPeriodCreditors();
        createPreviousPeriodCreditors();
        
        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_NAME, INCORRECT_TOTAL_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE,
                CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH)));
        assertTrue(errors.containsError(createError(PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE,
                CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH)));
    }

    @Test
    @DisplayName("Error thrown when single year filer creates previous period creditors")
    void testErrorThrownWhenPreviousPeriodFiledOnSingleYearCompany() throws ServiceException,
            DataException {

        createValidCurrentPeriodCreditors();
        createPreviousPeriodCreditors();

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);
  
        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);
        
        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(false);

        ReflectionTestUtils.setField(validator, INCONSISTENT_DATA_NAME, INCONSISTENT_DATA_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

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
        
        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);
  
        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);

        ReflectionTestUtils.setField(validator, INVALID_NOTE_NAME, INVALID_NOTE_VALUE);
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        errors = validator.validateCreditorsWithinOneYear(creditorsWithinOneYear, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

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

        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);
        
        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_NAME,
            CURRENT_BALANCE_SHEET_NOT_EQUAL_VALUE);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_NAME,
            PREVIOUS_BALANCE_SHEET_NOT_EQUAL_VALUE);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenThrow(mockServiceException);

        assertThrows(DataException.class,
                () -> validator.validateCreditorsWithinOneYear(creditorsWithinOneYear,
                        mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest));
    }
    
    @Test
    @DisplayName("Data exception thrown when mongo current balancesheet call fails")
    void testDataExceptionThrownWhenCurrentBalanceSheetMongoCallFails() throws ServiceException,
            DataException {

        createValidCurrentPeriodCreditors();
        createPreviousPeriodCreditors();
        
        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        when(mockCurrentPeriodService.findById(COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(new MongoException(""));

        assertThrows(DataException.class,
                () -> validator.validateCreditorsWithinOneYear(creditorsWithinOneYear,
                        mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest));
    }
    
    @Test
    @DisplayName("Data exception thrown when mongo previous balancesheet call fails")
    void testDataExceptionThrownWhenPreviousBalanceSheetMongoCallFails() throws ServiceException,
            DataException {

        createValidCurrentPeriodCreditors();
        createPreviousPeriodCreditors();
        
        when(mockCurrentPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).findById(
            COMPANY_ACCOUNTS_ID, mockRequest);
      
        when(mockPreviousPeriodService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(
            COMPANY_ACCOUNTS_ID);
        when(mockPreviousPeriodService.findById(COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(new MongoException(""));

        assertThrows(DataException.class,
                () -> validator.validateCreditorsWithinOneYear(creditorsWithinOneYear,
                        mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest));
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
    
    private void createValidCurrentPeriodCreditors() {
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

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
    
    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> generateValidCurrentPeriodResponseObject() {
      ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> currentPeriodResponseObject =
              new ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod>(
                      ResponseStatus.FOUND);

      uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod currentPeriodTest =
              new uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod();
      
      OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
      otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(6L);
      BalanceSheet balanceSheet = new BalanceSheet();
      balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);
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
      
      OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
      otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(12L);
      BalanceSheet balanceSheet = new BalanceSheet();
      balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);
      previousPeriodTest.setBalanceSheet(balanceSheet);

      previousPeriodResponseObject.setData(previousPeriodTest);
      return previousPeriodResponseObject;
  }
}
