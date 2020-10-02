package uk.gov.companieshouse.api.accounts.validation.transactionclosure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
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
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrentPeriodTnClosureValidatorTest {

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
    private Transaction transaction;

    @Mock
    private ResponseObject<CompanyAccount> companyAccountResponseObject;

    @Mock
    private ResponseObject<SmallFull> smallFullResponseObject;

    @Mock
    private ResponseObject<CurrentPeriod> currentPeriodResponseObject;

    @Mock
    private CompanyAccount companyAccount;

    @Mock
    private SmallFull smallFull;

    @Mock
    private CurrentPeriod currentPeriod;

    private CurrentPeriodTnClosureValidator currentPeriodTnClosureValidator;

    private static final String CURRENT_PERIOD_PATH = "$.current_period";

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final String SMALL_FULL_LINK = "smallFullLink";

    private static final String CURRENT_PERIOD_LINK = "currentPeriodLink";

    private static final String MANDATORY_ELEMENT_MISSING_KEY = "mandatoryElementMissing";
    private static final String MANDATORY_ELEMENT_MISSING = "mandatory.element.missing";

    @BeforeEach
    void setup() {
        this.currentPeriodTnClosureValidator = new CurrentPeriodTnClosureValidator(companyService, companyAccountService, smallFullService, currentPeriodService);
    }

    @Test
    @DisplayName("isValid method returns no errors - successful")
    void isValidNoErrors() throws DataException {

        Errors errors = new Errors(); // Empty.

        when(companyAccountService.findById(COMPANY_ACCOUNTS_ID, request)).thenReturn(companyAccountResponseObject);
        when(companyAccountResponseObject.getData()).thenReturn(companyAccount);
        when(companyAccount.getLinks()).thenReturn(createCompanyAccountLinks());

        when(smallFullService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(smallFullResponseObject);
        when(smallFullResponseObject.getData()).thenReturn(smallFull);
        when(smallFull.getLinks()).thenReturn(createSmallFullLinks(true));

        when(currentPeriodService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(currentPeriodResponseObject);
        when(currentPeriodResponseObject.getStatus()).thenReturn(ResponseStatus.FOUND);

        Errors responseErrors = currentPeriodTnClosureValidator.isValid(transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(responseErrors.hasErrors());
        assertEquals(errors.getErrors(), responseErrors.getErrors());
    }

    @Test
    @DisplayName("isValid method returns errors - failed")
    void isValidReturnsErrors() throws DataException {

        ReflectionTestUtils.setField(currentPeriodTnClosureValidator, MANDATORY_ELEMENT_MISSING_KEY, MANDATORY_ELEMENT_MISSING);

        when(companyAccountService.findById(COMPANY_ACCOUNTS_ID, request)).thenReturn(companyAccountResponseObject);
        when(companyAccountResponseObject.getData()).thenReturn(companyAccount);
        when(companyAccount.getLinks()).thenReturn(createCompanyAccountLinks());

        when(smallFullService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(smallFullResponseObject);
        when(smallFullResponseObject.getData()).thenReturn(smallFull);
        when(smallFull.getLinks()).thenReturn(createSmallFullLinks(true));

        when(currentPeriodService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(currentPeriodResponseObject);
        when(currentPeriodResponseObject.getStatus()).thenReturn(ResponseStatus.NOT_FOUND);

        Errors responseErrors = currentPeriodTnClosureValidator.isValid(transaction, COMPANY_ACCOUNTS_ID, request);

        assertTrue(responseErrors.hasErrors());
        assertTrue(responseErrors.containsError(createError(MANDATORY_ELEMENT_MISSING, CURRENT_PERIOD_PATH)));
    }

    private Map<String, String> createCompanyAccountLinks() {
        Map<String, String> links = new HashMap<>();

        links.put(CompanyAccountLinkType.SMALL_FULL.getLink(), SMALL_FULL_LINK);

        return links;
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
