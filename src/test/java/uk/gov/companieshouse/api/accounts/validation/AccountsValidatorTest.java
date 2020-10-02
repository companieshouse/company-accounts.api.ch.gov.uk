package uk.gov.companieshouse.api.accounts.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.validation.transactionclosure.CurrentPeriodTnClosureValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountsValidatorTest {

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    @Mock
    private CurrentPeriodTnClosureValidator currentPeriodTnClosureValidator;

    @InjectMocks
    private AccountsValidator validator;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final String MANDATORY_ELEMENT_MISSING = "mandatory.element.missing";

    private static final String CURRENT_PERIOD_PATH = "$.current_period";

    @Test
    @DisplayName("Validate Submission - successful, no errors")
    void validateSubmissionNoErrorsFound() throws DataException {

        Errors errors = new Errors(); // Empty

        when(currentPeriodTnClosureValidator.isValid(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);

        Errors responseErrors = validator.validationSubmission(transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(responseErrors.hasErrors());
        assertEquals(errors.getErrors(), responseErrors.getErrors());
    }

    @Test
    @DisplayName("Validate Submission - failed, errors")
    void validateSubmissionErrorsFound() throws DataException {

        Errors errors = new Errors(); // Empty
        errors.addError(createError(MANDATORY_ELEMENT_MISSING, CURRENT_PERIOD_PATH));

        when(currentPeriodTnClosureValidator.isValid(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);

        Errors responseErrors = validator.validationSubmission(transaction, COMPANY_ACCOUNTS_ID, request);

        assertTrue(responseErrors.hasErrors());
        assertEquals(errors.getErrors(), responseErrors.getErrors());
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}
