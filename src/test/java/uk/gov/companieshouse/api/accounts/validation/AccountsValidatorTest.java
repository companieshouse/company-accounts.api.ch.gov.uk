package uk.gov.companieshouse.api.accounts.validation;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
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
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.validation.transactionclosure.CurrentPeriodTxnClosureValidator;
import uk.gov.companieshouse.api.accounts.validation.transactionclosure.PreviousPeriodTxnClosureValidator;
import uk.gov.companieshouse.api.accounts.validation.transactionclosure.StocksTxnClosureValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountsValidatorTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String SMALL_FULL_LINK = "smallFullLink";
    private static final String MANDATORY_ELEMENT_MISSING_KEY = "mandatoryElementMissing";
    private static final String MANDATORY_ELEMENT_MISSING = "mandatory.element.missing";
    private static final String SMALL_FULL_PATH = "$.company_accounts.small_full";
    private static final String SMALL_FULL_CURRENT_PERIOD_PATH = "$.small_full.current_period";
    private static final String SMALL_FULL_PREVIOUS_PERIOD_PATH = "$.small_full.previous_period";
    private static final String SMALL_FULL_CURRENT_STOCKS = SMALL_FULL_CURRENT_PERIOD_PATH + ".notes.stocks";

    @Mock
    private CompanyService companyService;

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    @Mock
    private CurrentPeriodService currentPeriodService;

    @Mock
    private CurrentPeriod currentPeriod;

    @Mock
    private PreviousPeriod previousPeriod;

    @Mock
    private PreviousPeriodService previousPeriodService;

    @Mock
    private CompanyAccountService companyAccountService;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private ResponseObject<CompanyAccount> companyAccountResponseObject;

    @Mock
    private ResponseObject<SmallFull> smallFullResponseObject;

    @Mock
    private CompanyAccount companyAccount;
    
    @Mock
    private SmallFull smallFull;

    @Mock
    private BalanceSheet currentPeriodBalanceSheet;

    @Mock
    private BalanceSheet previousPeriodBalanceSheet;

    @Mock
    private ResponseObject<CurrentPeriod> cpResponseObj;

    @Mock
    private ResponseObject<PreviousPeriod> ppResponseObj;

    @Mock
    private CurrentPeriodTxnClosureValidator currentPeriodTnClosureValidator;

    @Mock
    private PreviousPeriodTxnClosureValidator previousPeriodTnClosureValidator;

    @Mock
    private StocksTxnClosureValidator stocksTnClosureValidator;

    private AccountsValidator validator;

    @BeforeEach
    void setUp() throws DataException {

        when(companyAccountService.findById(COMPANY_ACCOUNTS_ID, request)).thenReturn(companyAccountResponseObject);
        when(companyAccountResponseObject.getData()).thenReturn(companyAccount);
        when(companyAccount.getLinks()).thenReturn(createCompanyAccountLinks(true));
        
        this.validator =
                new AccountsValidator(companyService,
                        companyAccountService,
                        smallFullService,
                        currentPeriodTnClosureValidator,
                        previousPeriodTnClosureValidator,
                        currentPeriodService,
                        previousPeriodService,
                        stocksTnClosureValidator);

        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_KEY, MANDATORY_ELEMENT_MISSING);
    }

    @Test
    @DisplayName("Validate Submission - single year filer, successful, no errors")
    void validateSubmissionSYFNoErrorsFound() throws DataException, ServiceException {

        Errors emptyErrors = new Errors();

        when(smallFullService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(smallFullResponseObject);
        when(smallFullResponseObject.getData()).thenReturn(smallFull);

        when(currentPeriodTnClosureValidator.validate(any(String.class), any(SmallFull.class),
                any(HttpServletRequest.class), any(Errors.class))).thenReturn(emptyErrors);
        when(previousPeriodTnClosureValidator.validate(any(String.class), any(SmallFull.class),
                any(Transaction.class), any(HttpServletRequest.class), any(Errors.class)))
                .thenReturn(emptyErrors);

        when(currentPeriodService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(cpResponseObj);
        when(cpResponseObj.getData()).thenReturn(currentPeriod);
        when(currentPeriod.getBalanceSheet()).thenReturn(currentPeriodBalanceSheet);

        when (companyService.isMultipleYearFiler(transaction)).thenReturn(false);

        when(stocksTnClosureValidator
                .validate(COMPANY_ACCOUNTS_ID, smallFull, transaction, request, emptyErrors, currentPeriodBalanceSheet, null))
                .thenReturn(emptyErrors);

        Errors responseErrors = validator.validate(transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(responseErrors.hasErrors());
        assertEquals(emptyErrors.getErrors(), responseErrors.getErrors());
    }


    @Test
    @DisplayName("Validate Submission - successful, no errors")
    void validateSubmissionNoErrorsFound() throws DataException, ServiceException {

        Errors emptyErrors = new Errors();

        when(smallFullService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(smallFullResponseObject);
        when(smallFullResponseObject.getData()).thenReturn(smallFull);

        when(currentPeriodTnClosureValidator.validate(any(String.class), any(SmallFull.class),
                any(HttpServletRequest.class), any(Errors.class))).thenReturn(emptyErrors);
        when(previousPeriodTnClosureValidator.validate(any(String.class), any(SmallFull.class),
                any(Transaction.class), any(HttpServletRequest.class), any(Errors.class)))
                        .thenReturn(emptyErrors);

        when(currentPeriodService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(cpResponseObj);
        when(cpResponseObj.getData()).thenReturn(currentPeriod);
        when(currentPeriod.getBalanceSheet()).thenReturn(currentPeriodBalanceSheet);

        when (companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        when(previousPeriodService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(ppResponseObj);
        when(ppResponseObj.getData()).thenReturn(previousPeriod);
        when(previousPeriod.getBalanceSheet()).thenReturn(previousPeriodBalanceSheet);

        when(stocksTnClosureValidator
                .validate(COMPANY_ACCOUNTS_ID, smallFull, transaction, request, emptyErrors, currentPeriodBalanceSheet, previousPeriodBalanceSheet))
                .thenReturn(emptyErrors);

        Errors responseErrors = validator.validate(transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(responseErrors.hasErrors());
        assertEquals(emptyErrors.getErrors(), responseErrors.getErrors());
    }

    @Test
    @DisplayName("Validate Submission - failed no smallFull link, errors")
    void validateSubmissionNoSmallFullLinkErrorFound() throws DataException {

        Errors errors = new Errors();
        errors.addError(createError(MANDATORY_ELEMENT_MISSING, SMALL_FULL_PATH));

        when(companyAccount.getLinks()).thenReturn(createCompanyAccountLinks(false));

        Errors responseErrors = validator.validate(transaction, COMPANY_ACCOUNTS_ID, request);

        assertTrue(responseErrors.hasErrors());
        assertEquals(errors.getErrors(), responseErrors.getErrors());
        verify(currentPeriodTnClosureValidator, never()).validate(any(String.class), any(SmallFull.class),
                any(HttpServletRequest.class), any(Errors.class));
        verify(previousPeriodTnClosureValidator, never()).validate(any(String.class), any(SmallFull.class),
                any(Transaction.class), any(HttpServletRequest.class), any(Errors.class));
    }

    @Test
    @DisplayName("Validate Submission - failed no current period, errors")
    void validateSubmissionNoCurrentPeriodErrorsFound() throws DataException {

        Errors errors = new Errors();
        errors.addError(createError(MANDATORY_ELEMENT_MISSING, SMALL_FULL_CURRENT_PERIOD_PATH));

        when(smallFullService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(smallFullResponseObject);
        when(smallFullResponseObject.getData()).thenReturn(smallFull);

        when(currentPeriodTnClosureValidator.validate(any(String.class), any(SmallFull.class),
                any(HttpServletRequest.class), any(Errors.class))).thenReturn(errors);
        when(previousPeriodTnClosureValidator.validate(any(String.class), any(SmallFull.class),
                any(Transaction.class), any(HttpServletRequest.class), any(Errors.class)))
                        .thenReturn(errors);

        Errors responseErrors = validator.validate(transaction, COMPANY_ACCOUNTS_ID, request);

        assertTrue(responseErrors.hasErrors());
        assertEquals(errors.getErrors(), responseErrors.getErrors());
    }

    @Test
    @DisplayName("Validate Submission - failed no previous period, errors")
    void validateSubmissionNoPreviousPeriodErrorsFound() throws DataException {

        Errors errors = new Errors();
        errors.addError(createError(MANDATORY_ELEMENT_MISSING, SMALL_FULL_PREVIOUS_PERIOD_PATH));

        when(smallFullService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(smallFullResponseObject);
        when(smallFullResponseObject.getData()).thenReturn(smallFull);

        when(currentPeriodTnClosureValidator.validate(any(String.class), any(SmallFull.class),
                any(HttpServletRequest.class), any(Errors.class))).thenReturn(errors);
        when(previousPeriodTnClosureValidator.validate(any(String.class), any(SmallFull.class),
                any(Transaction.class), any(HttpServletRequest.class), any(Errors.class)))
                        .thenReturn(errors);

        Errors responseErrors = validator.validate(transaction, COMPANY_ACCOUNTS_ID, request);

        assertTrue(responseErrors.hasErrors());
        assertEquals(errors.getErrors(), responseErrors.getErrors());
    }

    @Test
    @DisplayName("Validate Submission - failed no current or previous period, errors")
    void validateSubmissionNoCurrentOrPreviousPeriodErrorsFound() throws DataException {

        Errors errors = new Errors();
        errors.addError(createError(MANDATORY_ELEMENT_MISSING, SMALL_FULL_CURRENT_PERIOD_PATH));
        errors.addError(createError(MANDATORY_ELEMENT_MISSING, SMALL_FULL_PREVIOUS_PERIOD_PATH));

        when(smallFullService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(smallFullResponseObject);
        when(smallFullResponseObject.getData()).thenReturn(smallFull);

        when(currentPeriodTnClosureValidator.validate(any(String.class), any(SmallFull.class),
                any(HttpServletRequest.class), any(Errors.class))).thenReturn(errors);
        when(previousPeriodTnClosureValidator.validate(any(String.class), any(SmallFull.class),
                any(Transaction.class), any(HttpServletRequest.class), any(Errors.class)))
                        .thenReturn(errors);

        Errors responseErrors = validator.validate(transaction, COMPANY_ACCOUNTS_ID, request);

        assertTrue(responseErrors.hasErrors());
        assertEquals(errors.getErrors(), responseErrors.getErrors());
    }

    @Test
    @DisplayName("Validate Submission - stocks tn closure validator returns errors")
    void validateSubmissionStocksReturnsErrors() throws DataException, ServiceException {
        Errors emptyErrors = new Errors(); //Empty to skip initial period validation

        when(smallFullService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(smallFullResponseObject);
        when(smallFullResponseObject.getData()).thenReturn(smallFull);

        when(currentPeriodTnClosureValidator.validate(any(String.class), any(SmallFull.class),
                any(HttpServletRequest.class), any(Errors.class))).thenReturn(emptyErrors);
        when(previousPeriodTnClosureValidator.validate(any(String.class), any(SmallFull.class),
                any(Transaction.class), any(HttpServletRequest.class), any(Errors.class)))
                .thenReturn(emptyErrors);

        ResponseObject<CurrentPeriod> cpResponseObj = new ResponseObject<>(ResponseStatus.FOUND);
        cpResponseObj.setData(currentPeriod);
        when(currentPeriodService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(cpResponseObj);
        when(currentPeriod.getBalanceSheet()).thenReturn(currentPeriodBalanceSheet);

        when (companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        ResponseObject<PreviousPeriod> ppResponseObj = new ResponseObject<>(ResponseStatus.FOUND);
        ppResponseObj.setData(previousPeriod);
        when(previousPeriodService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(ppResponseObj);
        when(previousPeriod.getBalanceSheet()).thenReturn(previousPeriodBalanceSheet);

        Errors stocksErrors = new Errors();
        stocksErrors.addError(createError(MANDATORY_ELEMENT_MISSING, SMALL_FULL_CURRENT_STOCKS));
        when(stocksTnClosureValidator
                .validate(COMPANY_ACCOUNTS_ID, smallFull, transaction, request, emptyErrors, currentPeriodBalanceSheet, previousPeriodBalanceSheet))
                .thenReturn(stocksErrors);

        Errors responseErrors = validator.validate(transaction, COMPANY_ACCOUNTS_ID, request);

        assertTrue(responseErrors.hasErrors());
        assertEquals(stocksErrors.getErrors(), responseErrors.getErrors());
    }
    
    private Map<String, String> createCompanyAccountLinks(boolean includeSmallFull) {
        Map<String, String> links = new HashMap<>();

        if (includeSmallFull) {
            links.put(CompanyAccountLinkType.SMALL_FULL.getLink(), SMALL_FULL_LINK);
        }

        return links;
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}
