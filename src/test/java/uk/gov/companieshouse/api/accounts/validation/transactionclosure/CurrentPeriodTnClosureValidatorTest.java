package uk.gov.companieshouse.api.accounts.validation.transactionclosure;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.impl.CompanyServiceImpl;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.validation.ErrorType;
import uk.gov.companieshouse.api.accounts.validation.LocationType;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CurrentPeriodTnClosureValidatorTest {

    @Mock
    private CompanyAccountService companyAccountService;

    @Mock
    private CompanyServiceImpl companyService;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private CurrentPeriodService currentPeriodService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ResponseObject<CurrentPeriod> currentPeriodResponseObject;

    @Mock
    private SmallFull smallFull;

    @Mock
    private BalanceSheet balanceSheet;

    @Mock
    private CurrentPeriod currentPeriod;

    private CurrentPeriodTxnClosureValidator currentPeriodTnClosureValidator;


    private static final String SMALL_FULL_CURRENT_PERIOD_PATH = "$.small_full.current_period";
    private static final String SMALL_FULL_CURRENT_PERIOD_BALANCE_SHEET_PATH = SMALL_FULL_CURRENT_PERIOD_PATH + ".balance_sheet";

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final String CURRENT_PERIOD_LINK = "currentPeriodLink";

    private static final String MANDATORY_ELEMENT_MISSING_KEY = "mandatoryElementMissing";
    private static final String MANDATORY_ELEMENT_MISSING = "mandatory.element.missing";

    @BeforeEach
    void setup() {
        this.currentPeriodTnClosureValidator = new CurrentPeriodTxnClosureValidator(companyService, currentPeriodService);
    }

    @Test
    @DisplayName("isValid method returns no errors - successful")
    void isValidNoErrors() throws DataException {

        Errors errors = new Errors(); // Empty.
        when(smallFull.getLinks()).thenReturn(createSmallFullLinks(true));

        when(currentPeriodService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(currentPeriodResponseObject);
        when(currentPeriodResponseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(currentPeriodResponseObject.getData()).thenReturn(currentPeriod);
        when(currentPeriod.getBalanceSheet()).thenReturn(balanceSheet);

        Errors responseErrors = currentPeriodTnClosureValidator.isValid(COMPANY_ACCOUNTS_ID, smallFull, request, errors);

        assertFalse(responseErrors.hasErrors());
        assertEquals(errors.getErrors(), responseErrors.getErrors());
    }

    @Test
    @DisplayName("isValid method returns errors - (current period) failed")
    void isValidReturnsErrors() throws DataException {

        ReflectionTestUtils.setField(currentPeriodTnClosureValidator, MANDATORY_ELEMENT_MISSING_KEY, MANDATORY_ELEMENT_MISSING);

        when(smallFull.getLinks()).thenReturn(createSmallFullLinks(true));

        when(currentPeriodService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(currentPeriodResponseObject);
        when(currentPeriodResponseObject.getStatus()).thenReturn(ResponseStatus.NOT_FOUND);

        Errors responseErrors = currentPeriodTnClosureValidator.isValid(COMPANY_ACCOUNTS_ID, smallFull, request, new Errors());

        assertTrue(responseErrors.hasErrors());
        assertTrue(responseErrors.containsError(createError(MANDATORY_ELEMENT_MISSING, SMALL_FULL_CURRENT_PERIOD_PATH)));
    }

    @Test
    @DisplayName("isValid method returns errors - (balance sheet) failed")
    void isValidReturnsErrorsNoBalanceSheet() throws DataException {

        ReflectionTestUtils.setField(currentPeriodTnClosureValidator, MANDATORY_ELEMENT_MISSING_KEY, MANDATORY_ELEMENT_MISSING);
        when(smallFull.getLinks()).thenReturn(createSmallFullLinks(true));

        when(currentPeriodService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(currentPeriodResponseObject);
        when(currentPeriodResponseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(currentPeriodResponseObject.getData()).thenReturn(currentPeriod);
        when(currentPeriod.getBalanceSheet()).thenReturn(null);

        Errors responseErrors = currentPeriodTnClosureValidator.isValid(COMPANY_ACCOUNTS_ID, smallFull, request, new Errors());

        assertTrue(responseErrors.hasErrors());
        assertTrue(responseErrors.containsError(createError(MANDATORY_ELEMENT_MISSING, SMALL_FULL_CURRENT_PERIOD_BALANCE_SHEET_PATH)));
    }

    @Test
    @DisplayName("isValid method returns errors - (no current period link) failed")
    void isValidReturnsErrorsNoCurrentPeriodLink() throws DataException {

        ReflectionTestUtils.setField(currentPeriodTnClosureValidator, MANDATORY_ELEMENT_MISSING_KEY, MANDATORY_ELEMENT_MISSING);

        Errors responseErrors = currentPeriodTnClosureValidator.isValid(COMPANY_ACCOUNTS_ID, smallFull, request, new Errors());

        assertTrue(responseErrors.hasErrors());
        assertTrue(responseErrors.containsError(createError(MANDATORY_ELEMENT_MISSING, SMALL_FULL_CURRENT_PERIOD_PATH)));
    }

    private Map<String, String> createSmallFullLinks(boolean includeCurrentPeriodLink) {
        Map<String, String> links = new HashMap<>();

        if (includeCurrentPeriodLink) {
            links.put(SmallFullLinkType.CURRENT_PERIOD.getLink(), CURRENT_PERIOD_LINK);
        }

        return links;
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}
