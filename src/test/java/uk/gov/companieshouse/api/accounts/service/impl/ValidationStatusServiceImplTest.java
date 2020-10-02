package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.validation.AccountsValidator;
import uk.gov.companieshouse.api.accounts.validation.ErrorType;
import uk.gov.companieshouse.api.accounts.validation.LocationType;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ValidationStatusServiceImplTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private AccountsValidator validator;

    @Mock
    private Transaction transaction;

    @InjectMocks
    private ValidationStatusServiceImpl service;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    private static final String CURRENT_PERIOD_PATH = "$.current_period";

    private static final String MANDATORY_ELEMENT_MISSING = "mandatory.element.missing";


    @Test
    @DisplayName("Get validation returns no errors")
    void getValidationErrorsReturnsEmptyErrors() throws DataException {

        Errors errors = new Errors(); // Empty.

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);
        when(validator.validationSubmission(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);

        Errors responseErrors = service.getValidationErrors(COMPANY_ACCOUNTS_ID, request);

        assertEquals(responseErrors, errors);
    }

    @Test
    @DisplayName("Get validation returns errors")
    void getValidationErrorsReturnsErrors() throws DataException {

        Errors errors = new Errors();
        errors.addError(createError(MANDATORY_ELEMENT_MISSING, CURRENT_PERIOD_PATH));

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);
        when(validator.validationSubmission(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(errors);

        Errors responseErrors = service.getValidationErrors(COMPANY_ACCOUNTS_ID, request);

        assertEquals(responseErrors, errors);
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}
