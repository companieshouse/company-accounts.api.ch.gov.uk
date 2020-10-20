package uk.gov.companieshouse.api.accounts.validation.transactionclosure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.Note;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.stocks.Stocks;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.NoteService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.validation.ErrorType;
import uk.gov.companieshouse.api.accounts.validation.LocationType;
import uk.gov.companieshouse.api.accounts.validation.NoteValidator;
import uk.gov.companieshouse.api.accounts.validation.NoteValidatorFactory;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StocksTxnClosureValidatorTest {

    private static final String STOCKS_LINK = "stocksLink";

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final String MANDATORY_ELEMENT_MISSING_KEY = "mandatoryElementMissing";
    private static final String MANDATORY_ELEMENT_MISSING = "mandatory.element.missing";

    private static final String SMALL_FULL_CURRENT_STOCKS_PATH = "$.company_accounts.small_full.current_period.notes.stocks";
    private static final String SMALL_FULL_PREVIOUS_STOCKS_PATH = "$.company_accounts.small_full.previous_period.notes.stocks";

    @Mock
    private SmallFull smallFull;

    @Mock
    private CompanyService companyService;

    @Mock
    private NoteService stocksNoteService;

    @Mock
    private ResponseObject<Note> noteResponseObject;

    @Mock
    private NoteValidator<Note> noteValidator;

    @Mock
    private BalanceSheet currentPeriodBs;

    @Mock
    private BalanceSheet previousPeriodBs;

    @Mock
    private CurrentAssets currentAssets;

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Stocks stocks;

    @Mock
    private NoteValidatorFactory<Note> noteValidatorFactory;

    private StocksTxnClosureValidator stocksTnClosureValidator;

    @BeforeEach
    void setup() {
        this.stocksTnClosureValidator = new StocksTxnClosureValidator(companyService, stocksNoteService, noteValidatorFactory);
    }

    @Test
    @DisplayName("Validate stocks on txn closure - has stocks note - no errors found")
    void validateStocksTnClosureNoErrors() throws DataException {
        Errors errors = new Errors(); //Empty.

        when(smallFull.getLinks()).thenReturn(createSmallFullLinks(true));

        when(stocksNoteService.find(AccountingNoteType.SMALL_FULL_STOCKS, COMPANY_ACCOUNTS_ID)).thenReturn(noteResponseObject);

        when(noteValidatorFactory.getValidator(AccountingNoteType.SMALL_FULL_STOCKS)).thenReturn(noteValidator);

        noteResponseObject.setData(stocks);
        when(noteValidator.validateSubmission(noteResponseObject.getData(), transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);

        Errors responseErrors = stocksTnClosureValidator.validate(COMPANY_ACCOUNTS_ID, smallFull, transaction, request, errors, currentPeriodBs, previousPeriodBs);

        assertFalse(responseErrors.hasErrors());
        assertEquals(errors, responseErrors);
    }

    @Test
    @DisplayName("Validate stocks on txn closure - has stocks note - errors found")
    void validateStocksTnClosureHasErrors() throws DataException {
        Errors errors = new Errors();
        errors.addError(createError(MANDATORY_ELEMENT_MISSING, SMALL_FULL_CURRENT_STOCKS_PATH));

        ReflectionTestUtils.setField(stocksTnClosureValidator, MANDATORY_ELEMENT_MISSING_KEY, MANDATORY_ELEMENT_MISSING);

        when(smallFull.getLinks()).thenReturn(createSmallFullLinks(true));

        when(stocksNoteService.find(AccountingNoteType.SMALL_FULL_STOCKS, COMPANY_ACCOUNTS_ID)).thenReturn(noteResponseObject);

        when(noteValidatorFactory.getValidator(AccountingNoteType.SMALL_FULL_STOCKS)).thenReturn(noteValidator);

        noteResponseObject.setData(stocks);
        when(noteValidator.validateSubmission(noteResponseObject.getData(), transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);

        Errors responseErrors = stocksTnClosureValidator.validate(COMPANY_ACCOUNTS_ID, smallFull, transaction, request, errors, currentPeriodBs, previousPeriodBs);

        assertTrue(responseErrors.hasErrors());
        assertEquals(errors, responseErrors);
    }

    @Test
    @DisplayName("Validate stocks on txn closure - no stocks note - no balance sheet data - SF")
    void validateStocksTnClosureNoNoteNoErrorsSF() throws ServiceException, DataException {
        Errors errors = new Errors(); //Empty.

        when(smallFull.getLinks()).thenReturn(createSmallFullLinks(false));

        when(currentPeriodBs.getCurrentAssets()).thenReturn(currentAssets);
        when(currentAssets.getStocks()).thenReturn(null);

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(false);

        Errors responseErrors = stocksTnClosureValidator.validate(COMPANY_ACCOUNTS_ID, smallFull, transaction, request, errors, currentPeriodBs, null);

        assertFalse(responseErrors.hasErrors());
        assertEquals(errors, responseErrors);
    }

    @Test
    @DisplayName("Validate stocks on txn closure - no stocks note - balance sheet data - SF")
    void validateStocksTnClosureNoNoteErrorsSF() throws ServiceException, DataException {
        Errors errors = new Errors(); //Empty.
        errors.addError(createError(MANDATORY_ELEMENT_MISSING, SMALL_FULL_CURRENT_STOCKS_PATH));

        ReflectionTestUtils.setField(stocksTnClosureValidator, MANDATORY_ELEMENT_MISSING_KEY, MANDATORY_ELEMENT_MISSING);


        when(smallFull.getLinks()).thenReturn(createSmallFullLinks(false));

        when(currentPeriodBs.getCurrentAssets()).thenReturn(currentAssets);
        when(currentAssets.getStocks()).thenReturn(1L);

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(false);

        Errors responseErrors = stocksTnClosureValidator.validate(COMPANY_ACCOUNTS_ID, smallFull, transaction, request, errors, currentPeriodBs, null);

        assertTrue(responseErrors.hasErrors());
        assertEquals(errors, responseErrors);
    }

    @Test
    @DisplayName("Validate stocks on txn closure - no stocks note - no balance sheet data - MF")
    void validateStocksTnClosureNoNoteNoErrorsMF() throws ServiceException, DataException {
        Errors errors = new Errors(); //Empty.

        when(smallFull.getLinks()).thenReturn(createSmallFullLinks(false));

        when(currentPeriodBs.getCurrentAssets()).thenReturn(currentAssets);
        when(currentAssets.getStocks()).thenReturn(null);

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);
        when(previousPeriodBs.getCurrentAssets()).thenReturn(currentAssets);
        when(currentAssets.getStocks()).thenReturn(null);

        Errors responseErrors = stocksTnClosureValidator.validate(COMPANY_ACCOUNTS_ID, smallFull, transaction, request, errors, currentPeriodBs, previousPeriodBs);

        assertFalse(responseErrors.hasErrors());
        assertEquals(errors, responseErrors);
    }

    @Test
    @DisplayName("Validate stocks on txn closure - no stocks note - balance sheet data - MF")
    void validateStocksTnClosureNoNoteErrorsMF() throws ServiceException, DataException {
        Errors errors = new Errors(); //Empty.
        errors.addError(createError(MANDATORY_ELEMENT_MISSING, SMALL_FULL_PREVIOUS_STOCKS_PATH));

        ReflectionTestUtils.setField(stocksTnClosureValidator, MANDATORY_ELEMENT_MISSING_KEY, MANDATORY_ELEMENT_MISSING);

        when(smallFull.getLinks()).thenReturn(createSmallFullLinks(false));

        when(currentPeriodBs.getCurrentAssets()).thenReturn(currentAssets);
        when(currentAssets.getStocks()).thenReturn(null);

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);
        when(previousPeriodBs.getCurrentAssets()).thenReturn(currentAssets);
        when(currentAssets.getStocks()).thenReturn(1L);

        Errors responseErrors = stocksTnClosureValidator.validate(COMPANY_ACCOUNTS_ID, smallFull, transaction, request, errors, currentPeriodBs, previousPeriodBs);

        assertTrue(responseErrors.hasErrors());
        assertEquals(errors, responseErrors);
    }

    private Map<String, String> createSmallFullLinks(boolean includeStocksLink) {
        Map<String, String> links = new HashMap<>();

        if (includeStocksLink) {
            links.put(SmallFullLinkType.STOCKS_NOTE.getLink(), STOCKS_LINK);
        }

        return links;
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}
