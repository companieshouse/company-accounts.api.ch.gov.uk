package uk.gov.companieshouse.api.accounts.validation.transactionclosure;

import jakarta.servlet.http.HttpServletRequest;
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
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.CompanyServiceImpl;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.validation.ErrorType;
import uk.gov.companieshouse.api.accounts.validation.LocationType;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PerviousPeriodTxnClosureValidatorTest {
    private static final String SMALL_FULL_PREVIOUS_PERIOD_PATH = "$.small_full.previous_period";
    private static final String SMALL_FULL_PREVIOUS_PERIOD_BALANCE_SHEET_PATH = SMALL_FULL_PREVIOUS_PERIOD_PATH + ".balance_sheet";

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final String PREVIOUS_PERIOD_LINK = "previousPeriodLink";

    private static final String MANDATORY_ELEMENT_MISSING_KEY = "mandatoryElementMissing";
    private static final String MANDATORY_ELEMENT_MISSING = "mandatory.element.missing";

    @Mock
    private Transaction transaction;

    @Mock
    private CompanyServiceImpl companyService;

    @Mock
    private PreviousPeriodService previousPeriodService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ResponseObject<PreviousPeriod> previousPeriodResponseObject;

    @Mock
    private SmallFull smallFull;

    @Mock
    private BalanceSheet balanceSheet;

    @Mock
    private PreviousPeriod previousPeriod;

    private PreviousPeriodTxnClosureValidator previousPeriodTxnClosureValidator;

    @BeforeEach
    void setup() {
        this.previousPeriodTxnClosureValidator = new PreviousPeriodTxnClosureValidator(companyService, previousPeriodService);
    }

    @Test
    @DisplayName("isValid method returns no errors for multi year filer - successful")
    void isValidNoErrorsForMultiYearFiler() throws DataException, ServiceException {
        Errors errors = new Errors();
        when(smallFull.getLinks()).thenReturn(createSmallFullLinks());

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);
        when(previousPeriodService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(previousPeriodResponseObject);
        when(previousPeriodResponseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(previousPeriodResponseObject.getData()).thenReturn(previousPeriod);
        when(previousPeriod.getBalanceSheet()).thenReturn(balanceSheet);

        Errors responseErrors = previousPeriodTxnClosureValidator.validate(COMPANY_ACCOUNTS_ID, smallFull,
                transaction, request, errors);

        assertFalse(responseErrors.hasErrors());
        assertEquals(errors.getErrors(), responseErrors.getErrors());
    }

    @Test
    @DisplayName("isValid method returns no errors for single year filer - successful")
    void isValidNoErrorsForSingleYearFiler() throws DataException, ServiceException {
        Errors errors = new Errors();

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(false);

        Errors responseErrors = previousPeriodTxnClosureValidator.validate(COMPANY_ACCOUNTS_ID, smallFull,
                transaction, request, errors);

        assertFalse(responseErrors.hasErrors());
        assertEquals(errors.getErrors(), responseErrors.getErrors());
        verify(previousPeriodService, never()).find(COMPANY_ACCOUNTS_ID, request);
    }

    @Test
    @DisplayName("isValid method returns errors - (previous period) failed")
    void isValidReturnsErrors() throws DataException, ServiceException {
        ReflectionTestUtils.setField(previousPeriodTxnClosureValidator, MANDATORY_ELEMENT_MISSING_KEY,
                MANDATORY_ELEMENT_MISSING);

        when(smallFull.getLinks()).thenReturn(createSmallFullLinks());

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);
        when(previousPeriodService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(previousPeriodResponseObject);
        when(previousPeriodResponseObject.getStatus()).thenReturn(ResponseStatus.NOT_FOUND);

        Errors responseErrors = previousPeriodTxnClosureValidator.validate(COMPANY_ACCOUNTS_ID,
                smallFull, transaction, request, new Errors());

        assertTrue(responseErrors.hasErrors());
        assertTrue(responseErrors.containsError(createError(
                SMALL_FULL_PREVIOUS_PERIOD_PATH)));
    }

    @Test
    @DisplayName("isValid method returns errors - (balance sheet) failed")
    void isValidReturnsErrorsNoBalanceSheet() throws DataException, ServiceException {
        ReflectionTestUtils.setField(previousPeriodTxnClosureValidator, MANDATORY_ELEMENT_MISSING_KEY,
                MANDATORY_ELEMENT_MISSING);

        when(smallFull.getLinks()).thenReturn(createSmallFullLinks());
        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);
        when(previousPeriodService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(previousPeriodResponseObject);
        when(previousPeriodResponseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(previousPeriodResponseObject.getData()).thenReturn(previousPeriod);
        when(previousPeriod.getBalanceSheet()).thenReturn(null);

        Errors responseErrors = previousPeriodTxnClosureValidator.validate(COMPANY_ACCOUNTS_ID, smallFull,
                transaction, request, new Errors());

        assertTrue(responseErrors.hasErrors());
        assertTrue(responseErrors.containsError(createError(
                SMALL_FULL_PREVIOUS_PERIOD_BALANCE_SHEET_PATH)));
    }

    @Test
    @DisplayName("isValid method returns errors - (no previous period link) failed")
    void isValidReturnsErrorsNoPreviousPeriodLink() throws DataException, ServiceException {
        ReflectionTestUtils.setField(previousPeriodTxnClosureValidator, MANDATORY_ELEMENT_MISSING_KEY,
                MANDATORY_ELEMENT_MISSING);

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        Errors responseErrors = previousPeriodTxnClosureValidator.validate(COMPANY_ACCOUNTS_ID, smallFull,
                transaction, request, new Errors());

        assertTrue(responseErrors.hasErrors());
        assertTrue(responseErrors.containsError(createError(
                SMALL_FULL_PREVIOUS_PERIOD_PATH)));
    }

    private Map<String, String> createSmallFullLinks() {
        Map<String, String> links = new HashMap<>();

        links.put(SmallFullLinkType.PREVIOUS_PERIOD.getLink(), PREVIOUS_PERIOD_LINK);

        return links;
    }

    private Error createError(String path) {
        return new Error(PerviousPeriodTxnClosureValidatorTest.MANDATORY_ELEMENT_MISSING, path,
                LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType());
    }
}
