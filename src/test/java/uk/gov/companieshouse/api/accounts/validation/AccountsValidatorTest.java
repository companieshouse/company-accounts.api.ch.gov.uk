package uk.gov.companieshouse.api.accounts.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountsValidatorTest {

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    @InjectMocks
    private AccountsValidator validator;


    @Test
    @DisplayName("Validate Submission - successful, no errors")
    void validateSubmissionNoErrorsFound() throws DataException {

        Errors errors = new Errors();

        Errors responseErrors = validator.validationSubmission(transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(responseErrors.hasErrors());
        assertEquals(errors.getErrors(), responseErrors.getErrors());
    }
}
