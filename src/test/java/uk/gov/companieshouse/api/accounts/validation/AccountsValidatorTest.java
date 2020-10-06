package uk.gov.companieshouse.api.accounts.validation;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.impl.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.validation.transactionclosure.CurrentPeriodTnClosureValidator;
import uk.gov.companieshouse.api.accounts.validation.transactionclosure.PreviousPeriodTnClosureValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountsValidatorTest {

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

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
    private Errors errors;
    
    @Mock
    private SmallFull smallFull;

    @Mock
    private CurrentPeriodTnClosureValidator currentPeriodTnClosureValidator;

    @Mock
    private PreviousPeriodTnClosureValidator previousPeriodTnClosureValidator;

    @InjectMocks
    private AccountsValidator validator;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final String SMALL_FULL_LINK = "smallFullLink";

    private static final String MANDATORY_ELEMENT_MISSING = "mandatory.element.missing";

    private static final String SMALL_FULL_CURRENT_PERIOD_PATH = "$.small_full.current_period";

    @BeforeEach
    private void setUp() throws DataException {
        when(companyAccountService.findById(COMPANY_ACCOUNTS_ID, request)).thenReturn(companyAccountResponseObject);
        when(companyAccountResponseObject.getData()).thenReturn(companyAccount);
        when(companyAccount.getLinks()).thenReturn(createCompanyAccountLinks(true));

        when(smallFullService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(smallFullResponseObject);
        when(smallFullResponseObject.getData()).thenReturn(smallFull);
    }

    @Test
    @DisplayName("Validate Submission - successful, no errors")
    void validateSubmissionNoErrorsFound() throws DataException {

        Errors errors = new Errors();

        when(currentPeriodTnClosureValidator.isValid(any(String.class), any(SmallFull.class),
                any(HttpServletRequest.class), any(Errors.class))).thenReturn(errors);
        when(previousPeriodTnClosureValidator.isValid(any(String.class), any(SmallFull.class),
                any(Transaction.class), any(HttpServletRequest.class), any(Errors.class)))
                        .thenReturn(errors);

        Errors responseErrors = validator.validationSubmission(transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(responseErrors.hasErrors());
        assertEquals(errors.getErrors(), responseErrors.getErrors());
    }

    @Test
    @DisplayName("Validate Submission - failed, errors")
    void validateSubmissionErrorsFound() throws DataException {

        Errors errors = new Errors();
        errors.addError(createError(MANDATORY_ELEMENT_MISSING, SMALL_FULL_CURRENT_PERIOD_PATH));

        when(currentPeriodTnClosureValidator.isValid(any(String.class), any(SmallFull.class),
                any(HttpServletRequest.class), any(Errors.class))).thenReturn(errors);
        when(previousPeriodTnClosureValidator.isValid(any(String.class), any(SmallFull.class),
                any(Transaction.class), any(HttpServletRequest.class), any(Errors.class)))
                        .thenReturn(errors);

        Errors responseErrors = validator.validationSubmission(transaction, COMPANY_ACCOUNTS_ID, request);

        assertTrue(responseErrors.hasErrors());
        assertEquals(errors.getErrors(), responseErrors.getErrors());
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
